package src;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Driver {
    public static void main(String[] args) throws InterruptedException {
        // Create objects
        // ClassName obj = new ClassName();
    	new Board();
    	ArrayList<Player> players = new ArrayList<Player>(); 
//    	Player[] players = new Player[4];
    	new GameLogic(players);
        // Call methods
        // obj.someMethod();
    	
        // Print output
        
        // You can add more logic here to test your classes
    }
}
