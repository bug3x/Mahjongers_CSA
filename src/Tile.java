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

	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

//	public void setPiece(Piece piece) {
//		if(piece==null) {
//			this.piece = null;
//			this.setIcon(null);
//			return;
//		}
//		this.piece = piece;
//		this.setIcon(piece.getIcon());
//		piece.setLocation(row, col);
//	}
	
	
	public Piece getPiece() {
		return piece;
	}
	
}
