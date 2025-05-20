package src;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GameLogic {
	// private List<Player> players;
	private Board board;
	private Stack<Piece> wall; // the walls to take pieces from
	
	private int currentPlayerIndex;
	private boolean yaku;
	private boolean win;
//	private boolean pongable, chowable, kongable;
//	private boolean riichi;
	
	public void noYaku() {
		System.out.println("NO Yaku.");
	}
	
	// calculate points for each, both addition and deduction
	public int calcPoints() {
		return -1;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

}
