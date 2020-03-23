import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Bank{
    private final int maxPerResource;
    private int wheat;
    private int sheep;
    private int wood;
    private int brick;
    private int ore;
    private Image[] cards;
    private int x;
    private int y;
    
    public Bank(int maxPerResource, int x,int y){
        this.maxPerResource=maxPerResource;
        wheat = maxPerResource;
        sheep = maxPerResource;
        wood = maxPerResource;
        brick = maxPerResource;
        ore = maxPerResource;
        cards = Util.loadCards();
        this.x=x;
        this.y=y;
    }
    
    void draw(GraphicsContext gc){
        
        for(int i = 0;i<cards.length;i++){
            gc.drawImage(cards[i] , x+i*100, y );
        }
        
    }
    
}