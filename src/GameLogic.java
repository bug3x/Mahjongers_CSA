package src;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GameLogic {
	// private List<Player> players;
//	private Board board;
	private Tile[][] board;
	
	public List<Piece> deadWall;
	public Stack<Piece> drawWall; // the walls to take pieces from
	public static List<Player> players;
	
	private Piece[] meld3;
	private Piece[] meld4;
	
	private int currentPlayerIndex;
	private boolean yaku;
	private boolean win;
//	private boolean riichi;
	
	private ArrayList<Piece> discards;
	
	public static ArrayList<Piece> discard1;
    public static ArrayList<Piece> discard2;
    public static ArrayList<Piece> discard3;
    public static ArrayList<Piece> discard4;
	
	public GameLogic(List<Player> players) {
	    this.players = players;
	    this.discards = new ArrayList<>();  // Initialize discards list
	    setupWalls(players);
	    
	 // Setup discard piles
        discard1 = new ArrayList<>();
        discard2 = new ArrayList<>();
        discard3 = new ArrayList<>();
        discard4 = new ArrayList<>();
	}

	
	public void setupPlayers(List<Player> players) {
	    this.players = players;
	    // Make sure drawWall is properly shuffled
	    java.util.Collections.shuffle(drawWall);
	    
	    // Deal 13 tiles to each player
	    for (Player p : players) {
	        for (int i = 0; i < 13; i++) {
	            if (!drawWall.isEmpty()) {
	                Piece piece = drawWall.pop();
	                System.out.println("Dealing " + piece.toString() + " to " + p.getName());
	                p.addToHand(piece);
	            }
	        }
	        // Sort the hand after dealing
	        java.util.Collections.sort(p.getHand(), (p1, p2) -> {
	            // Define type order: Numbers -> Circles -> Bamboo -> Winds -> Dragons
	            String[] typeOrder = {"Number", "Circle", "Bamboo", "Wind", "Dragon"};
	            int type1Index = java.util.Arrays.asList(typeOrder).indexOf(p1.getType());
	            int type2Index = java.util.Arrays.asList(typeOrder).indexOf(p2.getType());
	            
	            if (type1Index != type2Index) {
	                return type1Index - type2Index;
	            }
	            
	            // If same type, sort by value
	            if (p1.getType().equals("Wind")) {
	                String[] windOrder = {"East", "South", "West", "North"};
	                return java.util.Arrays.asList(windOrder).indexOf(p1.getValue()) - 
	                       java.util.Arrays.asList(windOrder).indexOf(p2.getValue());
	            } else if (p1.getType().equals("Dragon")) {
	                String[] dragonOrder = {"Red", "Green", "White"};
	                return java.util.Arrays.asList(dragonOrder).indexOf(p1.getValue()) - 
	                       java.util.Arrays.asList(dragonOrder).indexOf(p2.getValue());
	            } else {
	                return Integer.parseInt(p1.getValue()) - Integer.parseInt(p2.getValue());
	            }
	        });
	    }
	}
	
	public void setupWalls(List<Player> players) {
	    drawWall = new Stack<>();
	    deadWall = new ArrayList<>();
	    List<Piece> allTiles = new ArrayList<>();

	    int row = -1, col = -1; // Placeholder for unplaced tiles

	    // Winds: East, South, West, North (4 of each)
	    String[] winds = {"East", "South", "West", "North"};
	    String[] windFiles = {"dong.png", "nan.png", "xi.png", "bei.png"};
	    for (int i = 0; i < winds.length; i++) {
	        String wind = winds[i];
	        String file = windFiles[i];
	        for (int j = 0; j < 4; j++) {
	            boolean isMajorWind = wind.equals("East");
	            allTiles.add(new Wind(wind, wind, isMajorWind, row, col, file));
	        }
	    }

	    // Bamboo: 1–9, 4 of each
	    for (int i = 1; i <= 9; i++) {
	        for (int j = 0; j < 4; j++) {
	            boolean isGreen = i >= 2 && i <= 8;
	            allTiles.add(new Bamboo(String.valueOf(i), i, isGreen, row, col, "bamboo_" + i + ".png"));
	        }
	    }

	    // Circles (Dots): 1–9, 4 of each
	    for (int i = 1; i <= 9; i++) {
	        for (int j = 0; j < 4; j++) {
	            boolean isRedDot = (i == 5);
	            allTiles.add(new Circles(String.valueOf(i), i, isRedDot, row, col, "circle_" + i + ".png"));
	        }
	    }

	    // Numbers (Characters): 1–9, 4 of each
	    for (int i = 1; i <= 9; i++) {
	        for (int j = 0; j < 4; j++) {
	            boolean isEven = i % 2 == 0;
	            allTiles.add(new Number(String.valueOf(i), i, isEven, row, col, i + "_wan.png"));
	        }
	    }

	    // Dragons: Red, Green, White — 4 of each
	    String[] dragonNames = {"Red", "Green", "White"};
	    String[] dragonFiles = {"zhong.png", "fa.png", "blank.png"};  // Update with correct white dragon image when available
	    for (int i = 0; i < dragonNames.length; i++) {
	        String color = dragonNames[i];
	        String file = dragonFiles[i];
	        for (int j = 0; j < 4; j++) {
	            boolean isPowerTile = true;
	            allTiles.add(new Dragon(color, color, isPowerTile, row, col, file));
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


 // Returns the last discarded piece
    public Piece getLastDiscard(ArrayList<Piece> discard) {
        return discard.get(discard.size() - 1);
    }

    // Removes last piece from discard pile
    public void removeLastDiscard(ArrayList<Piece> discard) {
        if (!discard.isEmpty()) discard.remove(discard.size() - 1);
    }

	// meld callouts
	public Piece[] callPong(Tile a, Tile b, boolean take) {
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
		
		if(!take) {
			a.setPiece(b.getPiece());
		}
		else {
			a.setPiece(null);
		}
		
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
	public Meld callPong() {
		return null;
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
	
	public Piece[] getMeld3(){
		return meld3;
	}
	
	public void setMeld3(Piece[] meld3){
		this.meld3 = meld3;
	}
	
	public Piece[] getMeld4(){
		return meld4;
	}
	
	public void setMeld4(Piece[] meld4){
		this.meld4 = meld4;
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


	public ArrayList<Piece> getDiscards() {
		return discards;
	}
	
	public ArrayList<Piece> getDiscardForPlayer(int playerIndex) {
	    switch (playerIndex) {
	        case 0: return discard1;
	        case 1: return discard2;
	        case 2: return discard3;
	        case 3: return discard4;
	        default: throw new IllegalArgumentException("Invalid player index: " + playerIndex);
	    }
	}

	public Piece getLastDiscardForPlayer(int playerIndex) {
	    ArrayList<Piece> discard = getDiscardForPlayer(playerIndex);
	    if (!discard.isEmpty()) return discard.get(discard.size() - 1);
	    return null;
	}



}
