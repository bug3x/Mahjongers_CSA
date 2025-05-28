package src;

import java.awt.Color;
import javax.swing.JButton;

public class Tile extends JButton {

    private Piece piece;
    private int row, col;

    public static int num = 0;

    public Tile(int r, int c) {
        this.row = r;
        this.col = c;
//        this.setBackground(Color.LIGHT_GRAY); // visual cue for placement comment out, want transparent
        this.setFocusPainted(false);
    }

   
    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    // === Piece Handling ===
    public void setPiece(Piece piece) {
        this.piece = piece;
        if (piece == null) {
            this.setIcon(null);
        } else {
            this.setIcon(piece.getIcon());
            piece.setLocation(row, col);
        }
    }

    public Piece getPiece() {
        return piece;
    }

    public void removePiece() {
        this.piece = null;
        this.setIcon(null);
    }

    public boolean hasPiece() {
        return piece != null;
    }

    @Override
    public String toString() {
        return "Tile[" + row + "," + col + "] - " + (piece == null ? "empty" : piece.toString());
    }
}
