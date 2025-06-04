package src;
import java.util.ArrayList;

public class Meld {
	private Piece[] meld3;
	private Piece[] meld4;
	// pong method, called by player (ex. player1.pong()) once given a potential set of 3
	// will add a piece to that player
	// utilize helper method to display new set on the board
	
	// discard pile: an a
	// highlight the pong button
	public static boolean pongable(ArrayList<Player> player) {
		Player play;
		for(int i = 0; i < player.size(); i++) {
			play = player.get(i);
//			for(int j = 0; j < play.hand.size(); j++) {
//				// fill in;
//			}
		}
		return true;
	}
	
	//highlight the chow button
	public static boolean chowable() {
		 
		return true;
	}
	
	//highlight the kong button
	public static boolean kongable() {
		 return true;
	}
	
	//highlight the riichi button
	public static boolean riichi() {
		 return true;
	}
	
	// checking each meld boolean
	public static void checkMelds(ArrayList<Player> playerList) {
		pongable(playerList);
		chowable();
		kongable();
		riichi();
	}
	
	// each new round, check the melds for each player
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}


