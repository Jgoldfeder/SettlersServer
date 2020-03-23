import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.animation.RotateTransition;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.util.*;
import javafx.scene.Group;
import java.util.*;

public class Hex extends StackPane{
    int type;
    Image image;
    int num;
    Image num_image;
    int x;
    int y;
    Polygon polygon;
    
    //0-blue,   1-red,   2-white ,  3-yellow
    //keeps track of how many cards each player gets when this hexes number is spun
    ArrayList<Player> players;
    
    Hex(int x,int y,int type, Image i, int num, Image num_image){
        this.type = type;
        this.image = i;
        this.num = num;
        this.num_image = num_image;
        this.x=x;
        this.y = y;
        players = new ArrayList<>();
        
        //Rectangle rect = new Rectangle(80, 80);
        int offset = 6;
        this.polygon = new Polygon();
        polygon.getPoints().addAll(new Double[]{
            0.0-offset, 30.0-offset,
            54.0, 0.0 - offset,
            110.0+offset, 30.0-offset,
            
            110.0+offset, 95.0+offset,
            54.0, 125.0+offset,
            0.0-offset, 95.0+offset
            });
        polygon.setVisible(false);    
        setTranslateX(x-offset/2);
        setTranslateY(y-offset/2);
        this.setAlignment(Pos.CENTER);

        getChildren().addAll(polygon);
   
    }
        
    boolean click(int x,int y){
        if(polygon.contains(x-this.x,y-this.y)){ 
            return true;
        }
        return false;
    }

    Shape getPoly(){
        return polygon;
    }
    
    void draw(GraphicsContext gc){
        gc.drawImage(image , x, y );
        if(type!=6){
            //not a desert
            gc.drawImage(num_image , x, y );
        }
    }
    
    
    void rolled(){
        for(Player p:players){
            p.addCard(type);
        }
    }
    
    void registerPlayer(Player player){
        players.add(player);
    }
    
    
    
}