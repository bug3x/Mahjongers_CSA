import java.util.ArrayList;

public class Player {
    
    private ArrayList<Piece> hand;
    private ArrayList<Piece> discards;
    private ArrayList<Meld> melds; // Pon/Chi/Kan
    private boolean inRiichi;
    private String name;

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.discards = new ArrayList<>();
        this.melds = new ArrayList<>();
        this.inRiichi = false;
    }

    public boolean isGrabbing() {
        // Example logic: maybe used to track if player is allowed to call Chi/Pon/Kan
        return true;
    }

    public void discardPiece(Piece piece) {
        hand.remove(piece);
        discards.add(piece);
        // You may want to add logic for if the player is in riichi (must discard the drawn tile)
    }

    public Piece grabPiece(Piece piece) {
        hand.add(piece);
        return piece;
    }

    public ArrayList<Piece> getHand() {
        return hand;
    }

    public ArrayList<Piece> getDiscards() {
        return discards;
    }

    public ArrayList<Meld> getMelds() {
        return melds;
    }

    public boolean isInRiichi() {
        return inRiichi;
    }

    public void declareRiichi() {
        if (canDeclareRiichi()) {
            inRiichi = true;
            // Possibly lock the hand or mark it for auto-discard
        }
    }

    private boolean canDeclareRiichi() {
        // Add logic to check if the hand is closed and tenpai (one tile from winning)
        return true;
    }

    public String getName() {
        return name;
    }

    // Optional: sort hand, check tenpai, win detection, etc.
}
