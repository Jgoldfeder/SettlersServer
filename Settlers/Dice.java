import javafx.animation.RotateTransition;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.*;
import javafx.scene.Group;
import java.util.Random;
public class Dice{
    private Die d1;
    private Die d2;
    private Client client;
    private Random random = new Random();

    Dice(int x, int y,Client c){
        d1 = new Die(this);
        d2 = new Die(this);
        this.client = c;
        d1.setTranslateX(x);
        d1.setTranslateY(y);
        d2.setTranslateX(x+60);
        d2.setTranslateY(y);

        
    }
    
    public void roll(){
        int num1 = random.nextInt(6)+1;
        int num2 = random.nextInt(6)+1;

        int num = num1+num2;
        Info info = new Info();
        info.color = client.player.color;
        info.diceRoll = true;
        info.d1 = num1;
        info.d2 = num2;
        client.sendInfo(info);
        
        d1.roll(num1);
        d2.roll(num2);   
        client.giveCards(num);
    }
    
    public void roll(int num1,int num2){
        int num = 0;
        num+=d1.roll(num1);
        num+=d2.roll(num2); 
        client.giveCards(num);
    }
        
    public void addToRoot(Group r){
        r.getChildren().addAll(d1,d2);
    }



    private class Die extends StackPane {
        public final SimpleIntegerProperty valueProperty = new SimpleIntegerProperty();

        public Die(Dice d) {
            Rectangle rect = new Rectangle(50, 50);

            Text text = new Text();
            text.setFill(Color.WHITE);
            text.textProperty().bind(valueProperty.asString());

            this.setAlignment(Pos.CENTER);
            getChildren().addAll(rect, text);

            this.setOnMouseClicked(event -> d.roll());
        }

        public int roll(int num) {
            RotateTransition rt = new RotateTransition(Duration.seconds(1), this);
            rt.setFromAngle(0);
            rt.setToAngle(360);
            rt.setOnFinished(event -> {
                valueProperty.set(num);
            });
            rt.play();
            return num;
        }

    }
    
}