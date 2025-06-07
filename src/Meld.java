package src;

import java.util.ArrayList;
import java.util.List;

public class Meld {

    private String type;
    private List<Piece> pieces;

    public Meld(String type, List<Piece> pieces) {
        this.type = type;
        this.pieces = new ArrayList<>(pieces);
    }

    public String getType() {
        return type;
    }

    public List<Piece> getPieces() {
        return new ArrayList<>(pieces);
    }

    // Return player index if someone can call Pong on lastDiscard, else -1
    public static int pongable(ArrayList<Player> players, Piece lastDiscard) {
        for (int i = 0; i < players.size(); i++) {
            if (canPong(players.get(i), lastDiscard)) {
                return i;
            }
        }
        return -1;
    }

    // Return player index if someone can call Chow on lastDiscard, else -1
    public static int chowable(ArrayList<Player> players, Piece lastDiscard) {
        for (int i = 0; i < players.size(); i++) {
            if (canChow(players.get(i), lastDiscard)) {
                return i;
            }
        }
        return -1;
    }

    // Return player index if someone can call Kong on lastDiscard, else -1
    public static int kongable(ArrayList<Player> players, Piece lastDiscard) {
        for (int i = 0; i < players.size(); i++) {
            if (canKong(players.get(i), lastDiscard)) {
                return i;
            }
        }
        return -1;
    }

    // Return player index if someone can declare Riichi, else -1
    public static int riichi(ArrayList<Player> players) {
        for (int i = 0; i < players.size(); i++) {
            if (canRiichi(players.get(i))) {
                return i;
            }
        }
        return -1;
    }

    // === Individual checkers ===
    public static boolean canPong(Player player, Piece lastDiscard) {
        int count = 0;
        for (Piece p : player.getHand()) {
            if (p.getType().equals(lastDiscard.getType()) &&
                p.getValue().equals(lastDiscard.getValue())) {
                count++;
            }
        }
        return count >= 2;
    }

    public static boolean canChow(Player player, Piece lastDiscard) {
        if (!(lastDiscard instanceof Number || lastDiscard instanceof Bamboo || lastDiscard instanceof Circles)) {
            return false;
        }

        int[] values = new int[10]; // 1–9 range
        for (Piece p : player.getHand()) {
            if (p.getType().equals(lastDiscard.getType())) {
                int val = Integer.parseInt(p.getValue());
                values[val]++;
            }
        }

        int mid = Integer.parseInt(lastDiscard.getValue());

        return (mid >= 3 && values[mid - 2] > 0 && values[mid - 1] > 0) ||
               (mid >= 2 && mid <= 8 && values[mid - 1] > 0 && values[mid + 1] > 0) ||
               (mid <= 7 && values[mid + 1] > 0 && values[mid + 2] > 0);
    }

    public static boolean canKong(Player player, Piece lastDiscard) {
        int count = 0;
        for (Piece p : player.getHand()) {
            if (p.getType().equals(lastDiscard.getType()) &&
                p.getValue().equals(lastDiscard.getValue())) {
                count++;
            }
        }
        return count >= 3;
    }

    public static boolean canRiichi(Player player) {
        return player.getHand().size() == 13;
    }

    public static boolean canRon(Player player, Piece lastDiscard) {
        // TODO: Implement real Ron detection logic based on player's hand and last discard.
        // This is a complex check in Mahjong (e.g., waiting for a specific tile to complete a winning hand).
        // For now, always return false as a placeholder.
        return false;
    }
}
