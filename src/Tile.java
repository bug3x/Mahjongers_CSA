package src;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JButton;

public class Tile extends JButton {
    private boolean rotate = false;
    private Piece piece;
    private int row, col;

    public static int num = 0;


    public Tile(int row, int col) {
        super();
        this.row = row;
        this.col = col;
//        this.setBackground(Color.LIGHT_GRAY); // visual cue for placement comment out, want transparent
        this.setFocusPainted(false);
        // your existing setup
    }

    public void setRotated(boolean rotated) {
        this.rotate = rotated;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (rotate) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.rotate(Math.toRadians(90), getWidth() / 2.0, getHeight() / 2.0);
            super.paintComponent(g2);
            g2.dispose();
        } else {
            super.paintComponent(g);
        }
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
