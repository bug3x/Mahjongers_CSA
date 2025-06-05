package src;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GameLogic {
    private Tile[][] board;

    public List<Piece> deadWall;
    public Stack<Piece> drawWall;

    private Piece[] meld3;
    private Piece[] meld4;

    private int currentPlayerIndex;
    private boolean yaku;
    private boolean win;

    private ArrayList<Piece> discards;

    private List<Player> players;
    public ArrayList<Piece> discard1, discard2, discard3, discard4;

    private GameLogic logic;

    public GameLogic(List<Player> players) {
        this.players = players;
        this.discards = new ArrayList<>();
        setupWalls(players);
        discard1 = new ArrayList<>();
        discard2 = new ArrayList<>();
        discard3 = new ArrayList<>();
        discard4 = new ArrayList<>();
    }

    public void setupPlayers(List<Player> players) {
        this.players = players;
        java.util.Collections.shuffle(drawWall);

        for (Player p : players) {
            for (int i = 0; i < 13; i++) {
                if (!drawWall.isEmpty()) {
                    Piece piece = drawWall.pop();
                    System.out.println("Dealing " + piece.toString() + " to " + p.getName());
                    p.addToHand(piece);
                }
            }
            java.util.Collections.sort(p.getHand(), (p1, p2) -> {
                String[] typeOrder = {"Number", "Circle", "Bamboo", "Wind", "Dragon"};
                int type1Index = java.util.Arrays.asList(typeOrder).indexOf(p1.getType());
                int type2Index = java.util.Arrays.asList(typeOrder).indexOf(p2.getType());
                if (type1Index != type2Index) return type1Index - type2Index;
                if (p1.getType().equals("Wind")) {
                    String[] windOrder = {"East", "South", "West", "North"};
                    return java.util.Arrays.asList(windOrder).indexOf(p1.getValue()) - java.util.Arrays.asList(windOrder).indexOf(p2.getValue());
                } else if (p1.getType().equals("Dragon")) {
                    String[] dragonOrder = {"Red", "Green", "White"};
                    return java.util.Arrays.asList(dragonOrder).indexOf(p1.getValue()) - java.util.Arrays.asList(dragonOrder).indexOf(p2.getValue());
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
        int row = -1, col = -1;

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

        for (int i = 1; i <= 9; i++) {
            for (int j = 0; j < 4; j++) {
                boolean isGreen = i >= 2 && i <= 8;
                allTiles.add(new Bamboo(String.valueOf(i), i, isGreen, row, col, "bamboo_" + i + ".png"));
            }
        }

        for (int i = 1; i <= 9; i++) {
            for (int j = 0; j < 4; j++) {
                boolean isRedDot = (i == 5);
                allTiles.add(new Circles(String.valueOf(i), i, isRedDot, row, col, "circle_" + i + ".png"));
            }
        }

        for (int i = 1; i <= 9; i++) {
            for (int j = 0; j < 4; j++) {
                boolean isEven = i % 2 == 0;
                allTiles.add(new Number(String.valueOf(i), i, isEven, row, col, i + "_wan.png"));
            }
        }

        String[] dragonNames = {"Red", "Green", "White"};
        String[] dragonFiles = {"zhong.png", "fa.png", "blank.png"};
        for (int i = 0; i < dragonNames.length; i++) {
            String color = dragonNames[i];
            String file = dragonFiles[i];
            for (int j = 0; j < 4; j++) {
                boolean isPowerTile = true;
                allTiles.add(new Dragon(color, color, isPowerTile, row, col, file));
            }
        }

        java.util.Collections.shuffle(allTiles);
        for (int i = 0; i < 14; i++) {
            deadWall.add(allTiles.remove(allTiles.size() - 1));
        }
        drawWall.addAll(allTiles);

        System.out.println("Setup complete:");
        System.out.println("  Draw wall size: " + drawWall.size());
        System.out.println("  Dead wall size: " + deadWall.size());
    }
    


    public Piece getLastDiscard(ArrayList<Piece> discard) {
        return discard.get(discard.size() - 1);
    }

    public void removeLastDiscard(ArrayList<Piece> discard) {
        if (!discard.isEmpty()) discard.remove(discard.size() - 1);
    }

    public Piece[] callPong(Tile a, Tile b, int playerIndex, int discardOwnerIndex) {
        Piece p3 = getLastDiscardForPlayer(discardOwnerIndex);
        Player player = players.get(playerIndex);
        if (Meld.canPong(player, p3)) {
            Piece p1 = a.getPiece();
            Piece p2 = b.getPiece();
            Piece[] pongSet = new Piece[] {p1, p2, p3};
            setMeld3(pongSet);
            removeLastDiscard(getDiscardForPlayer(discardOwnerIndex));
            return pongSet;
        } else {
            System.out.println("INVALID PONG MOVE!!!");
            return null;
        }
    }

    public Piece[] callChow(Tile a, Tile b, int playerIndex, int discardOwnerIndex) {
        Piece p3 = getLastDiscardForPlayer(discardOwnerIndex);
        Player player = players.get(playerIndex);
        if (Meld.canChow(player, p3)) {
            Piece p1 = a.getPiece();
            Piece p2 = b.getPiece();
            Piece[] chowSet = new Piece[] {p1, p2, p3};
            setMeld3(chowSet);
            removeLastDiscard(getDiscardForPlayer(discardOwnerIndex));
            return chowSet;
        } else {
            System.out.println("INVALID CHOW MOVE!!!");
            return null;
        }
    }

    public Piece[] callKong(Tile a, Tile b, Tile c, int playerIndex, int discardOwnerIndex) {
        Piece p4 = getLastDiscardForPlayer(discardOwnerIndex);
        Player player = players.get(playerIndex);
        if (Meld.canKong(player, p4)) {
            Piece p1 = a.getPiece();
            Piece p2 = b.getPiece();
            Piece p3 = c.getPiece();
            Piece[] kongSet = new Piece[] {p1, p2, p3, p4};
            setMeld4(kongSet);
            removeLastDiscard(getDiscardForPlayer(discardOwnerIndex));
            return kongSet;
        } else {
            System.out.println("INVALID KONG MOVE!!!");
            return null;
        }
    }

    public void callRiichi() {
        System.out.println("RIICHI!!!");
        return;
    }

    public void noYaku() {
        System.out.println("NO Yaku.");
    }

    public int calcPoints() {
        return -1;
    }

    public Tile[][] getBoard() {
        return board;
    }

    public Piece[] getMeld3() {
        return meld3;
    }

    public void setMeld3(Piece[] meld3) {
        this.meld3 = meld3;
    }

    public Piece[] getMeld4() {
        return meld4;
    }

    public void setMeld4(Piece[] meld4) {
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
