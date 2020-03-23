import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.*; 
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Shape;
import javafx.geometry.Bounds;
import java.net.*; 
import java.io.*; 
import java.util.concurrent.locks.ReentrantLock;
import javafx.scene.shape.Polygon;
import java.net.InetAddress;
public class Client extends Application 
{
    public static void main(String[] args) {
        if(args.length>0){
            ip_name = args[0];
        }
        if(args.length>1){
            port = Integer.parseInt(args[1]);
        }
        launch(args);
    }
    
    private static String ip_name = "localhost";
    private static int port = 80;
    Player player;
    private Group root;
    private GraphicsContext gc;
    private Socket socket            = null;  
    private ObjectInputStream odis;
    private ObjectOutputStream odos;
    private Scene theScene;
    private ArrayList<Hex> board;
    private ReentrantLock lock = new ReentrantLock();
    private Queue<Info> infos = new LinkedList<>();
    private Dice dice;
    private HashMap<String,Info> database = new HashMap<>();  
    private Bank bank;
    public void start(Stage theStage){
        theStage.setTitle( "Settlers of Catan" );
             
        root = new Group();
        theScene = new Scene( root );
        theStage.setScene( theScene );
             
        Canvas canvas = new Canvas( 640*2, 480*2 );
        root.getChildren().add( canvas );
             
        gc = canvas.getGraphicsContext2D();
             
        gc.setFill( Color.RED );
        gc.setStroke( Color.BLACK );
        gc.setLineWidth(2);
        Font theFont = Font.font( "Times New Roman", FontWeight.BOLD, 48 );
        gc.setFont( theFont );
        gc.fillText( "Settlers of Catan", 60, 50 );
        gc.strokeText( "Settlers of Catan", 60, 50 );                
         
        
        
        theStage.show();
        
        new AnimationTimer(){
            public void handle(long currentNanoTime)
            {
                            //player.draw(gc,700,300);

                processInfo();
            }
        }.start();
        
        
        network();
        dice = new Dice(575,100,this);
        dice.addToRoot(root);
        bank = new Bank(19,700,90);
        bank.draw(gc);
    }
    
    void sendInfo(Info info){
        try{
            odos.writeObject(info);
        }catch (Exception e){
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }
    
    private void processInfo(){

        lock.lock();
        try{
            while(!infos.isEmpty()){
                Info info = infos.remove();
                // if data is from us, ignore
                if(info.color==player.color){
                    continue;
                }
                if(info.road){
                    buildRoad(info.hexIndices,info.color);
                }
                if(info.settlement){
                    buildSettlement(info.hexIndices,info.color);
                }
                if(info.city){
                    buildCity(info.hexIndices,info.color);
                }
                
                if(info.diceRoll){
                    dice.roll(info.d1,info.d2);
                }
            }
        }finally{
            lock.unlock();
        }
        
    }
    
    
    private void network(){
     
          try
        { 
            System.out.println("Connecting to network");  

            InetAddress ip = InetAddress.getByName(ip_name); 
      
            // establish the connection with server port  
            Socket s = new Socket(ip, port);
            System.out.println("Connected");

            // obtaining input and out streams             
            odos = new ObjectOutputStream(s.getOutputStream());
            InputStream i = s.getInputStream();

            odis = new ObjectInputStream(i); 
            

            // the following loop performs the exchange of 
            // information between client and client handler 
            
            // get what color we are
            int color = ((Info)odis.readObject()).color;
            player = new Player(color);
            player.draw(gc,700,300);
            //get the hex board
            board  = Util.getStandardBoard((ArrayList<Integer>) odis.readObject(),200,200);
            //draw board
            drawBoard(board,gc,root);  
            //register board
            theScene.setOnMouseClicked(event -> click(board,event));
            
            //input loop on new thread
            Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                inputLoop();
            }
            });  
            t1.start();
                
        }catch(Exception e){ 
            e.printStackTrace(); 
        } 
         
        
    }
    
    
    private void inputLoop(){
        try{
             Info info = null;
            //read in input
            
            while(true){
                info = (Info)odis.readObject();
                if(info.exit){
                    break;
                }
                lock.lock();
                try{
                    infos.add(info);
                }finally{
                    lock.unlock();
                }
                
                
            }
            
            
            
            // closing resources 
            odis.close(); 
            odos.close(); 
            
        }catch (Exception e){ 
            e.printStackTrace(); 
        } 
        
    }
    
    private void click(ArrayList<Hex> board,MouseEvent e){
        System.out.println((int)e.getX()+":"+(int)e.getY());
        ArrayList<Integer> clicked = new ArrayList<>();
        for(int i = 0;i<board.size();i++){
            if(board.get(i).click((int)e.getX(),(int)e.getY())){
                clicked.add(i);
            }
        }
        if(clicked.size()==2){
            buildRoad(clicked,player.color);
        }else if(clicked.size()==3){
            String hash = hexToHash(clicked);
            Info info = database.get(hash);
            if(info!=null && info.settlement){
                buildCity(clicked,player.color);
            }else{
                buildSettlement(clicked,player.color);
            }
        }
    }
    
    private String hexToHash(ArrayList<Integer> hexIndices){
        Collections. sort(hexIndices);
        String hash = "";
        for(Integer i:hexIndices){
            hash+=i.toString()+":";
        }    
        return hash;
    }
    
    private void buildRoad(ArrayList<Integer> hexIndices,int color){
        ArrayList<Hex> hex = new ArrayList<>();
        for(Integer i:hexIndices){
            hex.add(board.get(i));
        }
        Shape s = Shape.intersect(hex.get(0).getPoly(),hex.get(1).getPoly()); 
        s.setVisible(true);
        s.setScaleX(.5);
        s.setScaleY(.5);
        s.setTranslateX(-3);
        s.setTranslateY(-3);

        Color[] c = new Color[4];

        //Initialize the values of the array
        c[0] = Color.BLUE;
        c[1] = Color.RED;
        c[2] = Color.WHITE;
        c[3] = Color.YELLOW;
        
        s.setFill(c[color]);
        root.getChildren().add(s);
        

        
        Info info = new Info();
        info.color = color;
        info.road = true;
        info.hexIndices = hexIndices;
        
        //add road to database
        String hash = hexToHash(hexIndices);
        database.put(hash,info);
        
        if(color==player.color){
            //send info
            sendInfo(info);
        }
        
        
    }

    private void buildSettlement(ArrayList<Integer> hexIndices,int color){
        ArrayList<Hex> hex = new ArrayList<>();
        for(Integer i:hexIndices){
            hex.add(board.get(i));
        }
        Shape s = Shape.intersect(hex.get(0).getPoly(),hex.get(1).getPoly());
        s = Shape.intersect(hex.get(2).getPoly(),s);
        Bounds b = s.localToScene(s.getBoundsInLocal());
        int x = (int)b.getMinX();
        int y = (int)b.getMinY();
        Image[] settlements = Util.loadSettlements();
        gc.drawImage(settlements[color] , x-18, y-10 );
        

        
        Info info = new Info();
        info.color = color;
        info.settlement = true;
        info.hexIndices = hexIndices;
        
        //add settlement to database
        String hash = hexToHash(hexIndices);
        database.put(hash,info);
        
        //register on board
        for(Hex h:hex){
            h.registerPlayer(player);
        }
        
        
        if(color==player.color){
            //send info
            sendInfo(info);
        }
        

    }
    
    private void buildCity(ArrayList<Integer> hexIndices,int color){
        ArrayList<Hex> hex = new ArrayList<>();
        for(Integer i:hexIndices){
            hex.add(board.get(i));
        }
        Shape s = Shape.intersect(hex.get(0).getPoly(),hex.get(1).getPoly());
        s = Shape.intersect(hex.get(2).getPoly(),s);
        Bounds b = s.localToScene(s.getBoundsInLocal());
        int x = (int)b.getMinX();
        int y = (int)b.getMinY();
        Image[] cities = Util.loadCities();
        gc.drawImage(cities[color] , x-18, y-10 );
        

        
        Info info = new Info();
        info.color = color;
        info.city = true;
        info.hexIndices = hexIndices;
        
        //add city to database
        String hash = hexToHash(hexIndices);
        database.put(hash,info);
        
        //register on board
        for(Hex h:hex){
            h.registerPlayer(player);
        }
        
        
        if(color==player.color){
            //send info
            sendInfo(info);
        }
        
    }
    
    
    private static void drawBoard(ArrayList<Hex> hexes,GraphicsContext gc,Group r){
        for(Hex h : hexes){
            h.draw(gc);
            r.getChildren().add(h);
        }
    }
    
    void giveCards(int num){
        System.out.println(num);
        for(Hex h :board){
            if(h.num==num){
                h.rolled();
            }
        }
    }


}
