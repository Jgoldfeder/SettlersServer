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

public class Util{
    //0-blue,   1-red,   2-white ,  3-yellow
    public static final int BLUE = 0;
    public static final int RED = 1;
    public static final int WHITE = 2;
    public static final int YELLOW = 3;
    
    
    public static ArrayList<Integer> genBoard(){
        ArrayList<Integer> board = new ArrayList<>();
        // 4 wheat
        board.add(1);board.add(1);board.add(1);board.add(1);
         // 3 brick
        board.add(2);board.add(2);board.add(2);
        // 4 sheep
        board.add(3);board.add(3);board.add(3);board.add(3);
        // 3 ore
        board.add(4);board.add(4);board.add(4);
        // 4 wood
        board.add(5);board.add(5);board.add(5);board.add(5);  
        // 1 desert
        board.add(6);
        
        //randomize 
        Collections.shuffle (board);
        return board;
    }
    
    private static ArrayList<Integer> getNumbers(){
        ArrayList<Integer>  nums = new ArrayList<>();
        nums.add(5);nums.add(2);nums.add(6);
        nums.add(3);nums.add(8);nums.add(10);
        nums.add(9);nums.add(12);nums.add(11);
        nums.add(4);nums.add(8);nums.add(10);
        nums.add(9);nums.add(4);nums.add(5);
        nums.add(6);nums.add(3);nums.add(11);
        return nums;
    }
    
    public static ArrayList<Hex> getStandardBoard(ArrayList<Integer> board,int x,int y){
        ArrayList<Hex> hexes = new ArrayList<>();
        Image[] tiles = loadHexTiles();
        Image[] numTiles = loadNumTiles();
        ArrayList<Integer> numbers = insertDeserts(board,getNumbers());
        genRow(hexes,x,y,3,tiles,board,0,numbers,numTiles);
        genRow(hexes,x-55,y+94,4,tiles,board,3,numbers,numTiles);
        genRow(hexes,x-55*2,y+94*2,5,tiles,board,7,numbers,numTiles);
        genRow(hexes,x-55,y+94*3,4,tiles,board,12,numbers,numTiles);
        genRow(hexes,x,y+94*4,3,tiles,board,16,numbers,numTiles);
        
        return hexes;

    }
    
    private static  ArrayList<Integer> insertDeserts( ArrayList<Integer> hex, ArrayList<Integer> num){
        for(int i = 0;i<hex.size();i++){
            if(hex.get(i)== 6){
                //desert
                num.add(i,0);
            }
        }
        return num;
    }
    
    private static void genRow(ArrayList<Hex> hexes,int x, int y, int num, Image[] tiles, ArrayList<Integer> board,int index,ArrayList<Integer> numbers,Image[] numTiles){
        for(int i = 0; i< num; i++){
            int tile_num = board.get(index+i);
            int number = numbers.get(index+i);
            Hex h = new Hex(x+i*110, y ,tile_num,tiles[tile_num],number,numTiles[number]);
            hexes.add(h);
        }
    }
    
    private static Image[] loadHexTiles(){
        Image[] hex = new Image[7];
        hex[0] = new Image( "water.png" );
        hex[1] = new Image( "wheat.png" );
        hex[2] = new Image( "brick.png" );
        hex[3] = new Image( "sheep.png" );
        hex[4] = new Image( "ore.png" );
        hex[5] = new Image( "wood.png" );
        hex[6] = new Image( "desert.png" );

        return hex;       
    }
    
    public static Image[] loadCards(){
        Image[] c = new Image[6];
        c[0] = new Image( "development_card.png" );
        c[1] = new Image( "wheat_card.png" );
        c[2] = new Image( "brick_card.png" );
        c[3] = new Image( "sheep_card.png" );
        c[4] = new Image( "ore_card.png" );
        c[5] = new Image( "wood_card.png" );

        return c;       
    }
    
    
    
    
    private static Image[] loadNumTiles(){
        Image[] numTiles = new Image[13];
        for(int i  = 2;i<13;i++){
            numTiles[i] = new Image(i+".png");
        }
        return numTiles;       
    }
    
    public static Image[] loadCities(){
        Image[] i = new Image[4];
        i[0] = new Image( "blue_city.png" );
        i[1] = new Image( "red_city.png" );
        i[2] = new Image( "white_city.png" );
        i[3] = new Image( "yellow_city.png" );
        return i;
    }
    
    public static Image[] loadSettlements(){
        Image[] i = new Image[4];
        i[0] = new Image( "blue_settlement.png" );
        i[1] = new Image( "red_settlement.png" );
        i[2] = new Image( "white_settlement.png" );
        i[3] = new Image( "yellow_settlement.png" );
        return i;
    }    
    
    
    
    
}