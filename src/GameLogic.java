
public class GameLogic {

	// pong method, called by player (ex. player1.pong()) once given a potential set of 3
	// will add a piece to that player
	// utilize helper method to display new set on the board
	public Piece[] pong() {
		Piece[] set3 = new Piece[3];
		System.out.println("pong!");
		return set3;
	}
	
	// same as pong, but only to take from the previous player's discard
	public Piece[] chow() {
		Piece[] set3 = new Piece[3];
		System.out.println("chow!");
		return set3;
	}
	
	// similar to pong and chow but to create a set of 4
	public Piece[] kong() {
		Piece[] set4 = new Piece[4];
		System.out.println("kong!");
		return set4;
	}
	
	// a method to declare riichi, returns nothing.
	public void riichi() {
		System.out.println("RICHI!!!");
		return;
	}
	
	public void noYaku() {
		System.out.println("NO Yaku.");
	}
	
	public int calcPoints() {
		return -1;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

}
