package src;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Driver {
	public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);

        // Ask for player's name
        System.out.print("Enter your name: ");
        String playerName = scanner.nextLine();
        System.out.print("Enter your signature catch phrase: ");
        String playerPhrase = scanner.nextLine();

        // Create objects
        new Board();
        ArrayList<Player> players = new ArrayList<Player>();
        new GameLogic(players);

        // Print output
        System.out.println("\n\n\n\n\n\nHeya, I'm " + playerName + "! The first player will be playing as me! " + playerPhrase);
        System.out.println("\nHey, I'm Mr. Biatchin! The second player will be playing as me! Let's stir up a really, really biatchin game!");
        System.out.println("\nHello, I'm Just_Do_It_Later! The third player will be playing as me! Win now, lose later!");
        System.out.println("\nHi, I'm Mr. David! The fourth player will be playing as me! Don't forget to push your code!");

        scanner.close();
    }
}
