import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Player{
    
    //0-blue,   1-red,   2-white ,  3-yellow
    int color;
    private final int DEVEL = 0;
    private final int WHEAT = 1;
    private final int BRICK=2;
    private final int SHEEP=3;
    private final int ORE=4;
    private final int WOOD=5;
    private int[] cards;
    private Image[] cardImages;
    
    public Player(int color){
        cardImages = Util.loadCards();  
        cards = new int[6];        
        this.color = color;
    }
    
    public void addCard(int type){
        System.out.println("hey!");
        cards[type]++;
    }
    
    public void draw(GraphicsContext gc, int x,int y){
        int offset = 0;
        for(int i = 0;i<cards.length;i++){
            if(cards[i]!=0){
                for(int j = 0;j<cards[i];j++){
                    gc.drawImage(cardImages[i] , x+offset, y-(j*10) );
                }
                offset+=100;
            }
        }
           
    }
}