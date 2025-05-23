package src;

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
        return true;
    }

    public void discardPiece(Piece piece) {
        hand.remove(piece);
        discards.add(piece);
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
        }
    }

    private boolean canDeclareRiichi() {
        return true;
    }

    public String getName() {
        return name;
    }

    // Optional: sort hand, check tenpai, win detection, etc.
    public void addToHand(Piece pop) {
        hand.add(pop);
    }

   
    public Piece getPiece() {
        if (hand.isEmpty()) return null;
        return hand.get(hand.size() - 1); 
    }

    public void setPiece(Piece piece) {
        hand.add(piece); 
    }
}
