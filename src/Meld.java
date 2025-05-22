
import java.util.ArrayList;

public class Meld {
	// pong method, called by player (ex. player1.pong()) once given a potential set of 3
	// will add a piece to that player
	// utilize helper method to display new set on the board
	public Piece[] callPong(Piece a, Piece b, boolean take) {
		Piece[] set3 = new Piece[3];
		System.out.println("pong!");
		Piece temp = a.getPiece();
		
		if(temp != null) {
			temp.setValue(temp.getValue()+1); // ex. value = "1", "1" + 1 = 11
		}
		
		Piece temp2 = b.getPiece();
		if(temp2 != null) {
			temp2.setValue(temp.getValue()+1);
		}
		
		if(!take)
			a.setPiece(b.getPiece());
		else
			a.setPiece(null);
		
		b.setPiece(temp);
		return set3;
	}
	
	// same as pong, but only to take from the previous player's discard
	public Piece[] callChow() {
		Piece[] set3 = new Piece[3];
		System.out.println("chow!");
		return set3;
	}
	
	// similar to pong and chow but to create a set of 4
	public Piece[] callKong() {
		Piece[] set4 = new Piece[4];
		System.out.println("kong!");
		return set4;
	}
	
	// a method to declare riichi, returns nothing.
	public void callRiichi() {
		System.out.println("RIICHI!!!");
		return;
	}
	
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


