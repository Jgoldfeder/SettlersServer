import javafx.scene.image.Image;
import javafx.scene.shape.Shape;
import java.io.Serializable;
import javafx.collections.*;
import java.util.ArrayList;

public class Info implements Serializable{
    int color = -1;
    boolean settlement = false;
    boolean city = false;
    boolean road = false;
    boolean exit = false;
    boolean diceRoll = false;
    int d1 = -1;
    int d2 = -1;
    ArrayList<Integer> hexIndices = null;
    String message = "";
    
}