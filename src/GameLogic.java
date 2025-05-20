package src;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GameLogic {
	// private List<Player> players;
//	private Board board;
	private Tile[][] board;
	
	private List<Player> players;
	private List<Piece> deadWall;
	private Stack<Piece> drawWall; // the walls to take pieces from
	
	private int currentPlayerIndex;
	private boolean yaku;
	private boolean win;
//	private boolean pongable, chowable, kongable;
//	private boolean riichi;
	
	public GameLogic() {
		board = new Tile[8][8];
		
		for(int i =0; i < board.length;i++) {
			for(int j = 0; j < board[0].length;j++) {
				board[i][j] = new Tile(i, j);
			}
		}	
		
		setupWalls();
		
		//setup other pieces
		setupPlayers(players);
	}
	
	public void setupPlayers(List<Player> players) {
	    this.players = players;
	    for (Player p : players) {
	        for (int i = 0; i < 13; i++) {
	            p.addToHand(wall.pop()); // you wanna randomize the wall with the 144 tiles
	        }
	    }
	}
	
	public void setupWalls() {
		//walls
		//for loop for each 
		board[board.length-1][3].setPiece(new Bamboo(".png", number, board.length-2, board ));
		board[board.length-1][4].setPiece(new Numbers("king.png", color,board.length-2,3, board ));
	}
	
	public void noYaku() {
		System.out.println("NO Yaku.");
	}
	
	// calculate points for each, both addition and deduction
	public int calcPoints() {
		return -1;
	}
	
	public 
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

}
