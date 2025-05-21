package src;

import java.awt.Color;

import javax.swing.JButton;

//copied from 
public class Tile extends JButton{
	private Piece piece;
	public static int num = 0;
	private int row, col;
	
	public void setRow(int row) {
		this.row = row;
	}

	public void setCol(int col) {
		this.col = col;
	}
	
	public Tile(int r, int c) {
	    this.row = r;
	    this.col = c;
	    this.setBackground(Color.LIGHT_GRAY); // if we want to have colored tile placements for a visual cue to piece placement
	    this.setFocusPainted(false);
	}


	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public void removePiece() {
	    this.piece = null;
	    this.setIcon(null);
	}

	public void setPiece(Piece piece) {
	    this.piece = piece;
	
	    if (piece == null) {
	        this.setIcon(null);
	    } else {
	        this.setIcon(piece.getIcon());
	        piece.setLocation(row, col);  // Optional: useful if pieces need to track their position
	    }
	}
	
	public Piece getPiece() {
		return piece;
	}

	public boolean hasPiece() {
    		return piece != null;
	}

	@Override
	public String toString() {
    		return "Tile[" + row + "," + col + "] - " + (piece == null ? "empty" : piece.toString());
	}
}
