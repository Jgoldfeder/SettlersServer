// Java implementation of  Server side 
// It contains two classes : Server and ClientHandler 
// Save file as Server.java 
  
import java.io.*; 
import java.text.*; 
import java.util.*; 
import java.net.*; 
import java.util.concurrent.locks.ReentrantLock;
// Server class 
public class Server  
{ 
    static int numPlayers = 0;
    static ReentrantLock lock = new ReentrantLock();
    static  ArrayList<Integer> board;
    static ArrayList<ObjectOutputStream> outStreams = new ArrayList<>();
    public static void main(String[] args) throws IOException  
    { 
        int port = 80;
        if(args.length>0){
            port = Integer.parseInt(args[0]);
        }
        //generate the board
        board =  Util.genBoard();
        // server is listening on port 80
        ServerSocket ss = new ServerSocket(port); 
          
        // running infinite loop for getting 
        // client request 
        while (true)  
        { 
            Socket s = null; 
              
            try 
            { 
                // socket object to receive incoming client requests 
                s = ss.accept(); 
                  
                System.out.println("A new client is connected : " + s); 
                  
                // obtaining input and out streams 
                OutputStream dos = s.getOutputStream();  
                InputStream dis = s.getInputStream(); 
                 
                 
                System.out.println("Assigning new thread for this client"); 
  
                // create a new thread object 
                Thread t = new ClientHandler(s, new ObjectOutputStream(dos),new ObjectInputStream(dis)); 
  
                // Invoking the start() method 
                t.start(); 
                  
            } 
            catch (Exception e){ 
                s.close(); 
                e.printStackTrace(); 
            } 
        } 
    } 
} 
  
// ClientHandler class 
class ClientHandler extends Thread  
{ 
    private ObjectInputStream odis = null; 
    private ObjectOutputStream odos = null; 
    private final Socket s; 
      
  
    // Constructor 
    public ClientHandler(Socket s, ObjectOutputStream dos, ObjectInputStream dis)  
    { 
        this.s = s; 

            this.odos = dos;
            this.odis = dis; 

        // add this client to list of streams
        Server.lock.lock();
        try{
            Server.outStreams.add(odos);
        }finally{
            Server.lock.unlock();
        }
        
    } 
  
    @Override
    public void run()  
    { 
        int color = 0;
        String received; 
        String toreturn; 
        Server.lock.lock();
        try{
            if(Server.numPlayers>=4){
                try
                { 
                    // closing resources 
                    System.out.println("GAME IS FULL");
                    this.odis.close(); 
                    this.odos.close(); 
                      
                }catch(IOException e){ 
                    e.printStackTrace(); 
                }
                System.exit(0);
            }
            color = Server.numPlayers;
            Server.numPlayers++;

        }finally{
            Server.lock.unlock();
        }

        try
        { 
            //send color
            Info info = new Info();
            info.color = color;
            odos.writeObject(info);
            //send board
            odos.writeObject(Server.board);
                   
            while(true){
                info = (Info)odis.readObject();
                if(info.exit){
                    break;
                }
                //broadcast info
                for(ObjectOutputStream s: Server.outStreams){
                    s.writeObject(info);
                }
            }
        
            // closing resources 
            this.odis.close(); 
            this.odos.close(); 
              
        }catch(Exception e){ 
            e.printStackTrace(); 
        } 
    } 
} 