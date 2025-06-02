package src;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import test_scripts.BoardTest;

public class Driver {
    public static void main(String[] args) throws InterruptedException {
        // Create objects
        // ClassName obj = new ClassName();
    	ArrayList<Player> players = new ArrayList<Player>(); 
    	new GameLogic(players);
    	new Board();
//    	Player[] players = new Player[4];
        // Call methods
        // obj.someMethod();
    	
        // Print output
        System.out.println("启动软件.");
        TimeUnit.SECONDS.sleep(50);
        System.out.println("千里之行，始于足下.");
        TimeUnit.SECONDS.sleep(50);
        System.out.println("七転び八起き");
        // You can add more logic here to test your classes
    }
}