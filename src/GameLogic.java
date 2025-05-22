package src;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GameLogic {
	// private List<Player> players;
//	private Board board;
	private Tile[][] board;
	
	private List<Piece> deadWall;
	private Stack<Piece> drawWall; // the walls to take pieces from
	public static List<Player> players;
	
	private int currentPlayerIndex;
	private boolean yaku;
	private boolean win;
//	private boolean pongable, chowable, kongable;
//	private boolean riichi;
	
	public GameLogic(List<Player> players) {
	    board = new Tile[8][8];
	    this.players = players;

	    for (int i = 0; i < board.length; i++) {
	        for (int j = 0; j < board[0].length; j++) {
	            board[i][j] = new Tile(i, j);
	        }
	    }

	    setupWalls(players);
	    // Remove setupPlayers from here — do it *after* adding players
	}

	
	public void setupPlayers(List<Player> players) {
	    this.players = players;
	    for (Player p : players) {
	        for (int i = 0; i < 13; i++) {
	            p.addToHand(drawWall.pop()); // you wanna randomize the wall with the 144 tiles
	        }
	    }
	}
	
	public void setupWalls(List<Player> players) {
	    drawWall = new Stack<>();
	    deadWall = new ArrayList<>();
	    List<Piece> allTiles = new ArrayList<>();

	    int row = -1, col = -1; // Placeholder for unplaced tiles

	    // Winds: East, South, West, North (4 of each)
	    String[] winds = {"East", "South", "West", "North"};
	    for (String wind : winds) {
	        for (int i = 0; i < 4; i++) {
	            boolean isMajorWind = wind.equals("East"); // Set major wind logic
	            allTiles.add(new Wind(wind, wind, isMajorWind, row, col, wind.toLowerCase() + ".png"));
	        }
	    }

	    // Bamboo: 1–9, 4 of each
	    for (int i = 1; i <= 9; i++) {
	        for (int j = 0; j < 4; j++) {
	            boolean isGreen = i >= 2 && i <= 8; // Optional logic
	            allTiles.add(new Bamboo(String.valueOf(i), i, isGreen, row, col, "bamboo" + i + ".png"));
	        }
	    }

	    // Circles (Dots): 1–9, 4 of each
	    for (int i = 1; i <= 9; i++) {
	        for (int j = 0; j < 4; j++) {
	            boolean isRedDot = (i == 5); // Red dot on 5-dot (like red fives)
	            allTiles.add(new Circles(String.valueOf(i), i, isRedDot, row, col, "dots" + i + ".png"));
	        }
	    }

	    // Numbers: 1–9 (assuming it's a Characters suit), 4 of each
	    for (int i = 1; i <= 9; i++) {
	        for (int j = 0; j < 4; j++) {
	            boolean isEven = i % 2 == 0;
	            allTiles.add(new Number(String.valueOf(i), i, isEven, row, col, "characters" + i + ".png"));
	        }
	    }

	    // Dragons: Red, Green, White — 4 of each
	    String[] dragonColors = {"Red", "Green", "White"};
	    for (String color : dragonColors) {
	        for (int i = 0; i < 4; i++) {
	            boolean isPowerTile = true; // All dragons are power tiles
	            allTiles.add(new Dragon(color, color, isPowerTile, row, col, color.toLowerCase() + ".png"));
	        }
	    }

	    // Shuffle everything
	    java.util.Collections.shuffle(allTiles);

	    // Split off last 14 tiles as dead wall
	    for (int i = 0; i < 14; i++) {
	        deadWall.add(allTiles.remove(allTiles.size() - 1));
	    }

	    // Remaining become the draw wall
	    drawWall.addAll(allTiles);

	    System.out.println("Setup complete:");
	    System.out.println("  Draw wall size: " + drawWall.size());   // should be 122
	    System.out.println("  Dead wall size: " + deadWall.size());   // should be 14
	}


	
	public void noYaku() {
		System.out.println("NO Yaku.");
	}
	
	// calculate points for each, both addition and deduction
	public int calcPoints() {
		return -1;
	}

	public Tile[][] getBoard() {
		return board;
	}
	
	public static void main(String[] args) {
	    List<Player> players = new ArrayList<>();
	    players.add(new Player("John"));
	    players.add(new Player("Bot 1"));
	    players.add(new Player("Bot 2"));
	    players.add(new Player("Bot 3"));

	    GameLogic logic = new GameLogic(players);
	    logic.setupPlayers(players);
	}


}
