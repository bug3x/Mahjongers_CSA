

/**
 * Represents a Mahjong tile using string-based type and value.
 */
package src;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public abstract class Piece {

    protected String type;      // e.g., "Bamboo", "Dragon"
    protected String value;     // e.g., "3", "Red"
    protected int row;
    protected int col;
    protected ImageIcon icon;
    
    public Piece piece;

    public Piece(String type, String value, int row, int col, String imageFileName) {
        this.type = type;
        this.value = value;
        this.row = row;
        this.col = col;
        setIcon(imageFileName);
    }

    private void setIcon(String fileName) {
        ImageIcon rawIcon = new ImageIcon("imgs/" + fileName);
        Image img = rawIcon.getImage();
        this.icon = createScaledIcon(img, 80, 100);
    }

    private static ImageIcon createScaledIcon(Image img, int width, int height) {
        BufferedImage buffered = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = buffered.createGraphics();
        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.fillRect(0, 0, width, height);

        int originalWidth = img.getWidth(null);
        int originalHeight = img.getHeight(null);
        int scaledWidth = width;
        int scaledHeight = height;

        if (originalWidth > 0 && originalHeight > 0) {
            double aspectRatio = (double) originalWidth / originalHeight;
            if (width / aspectRatio <= height) {
                scaledHeight = (int) (width / aspectRatio);
            } else {
                scaledWidth = (int) (height * aspectRatio);
            }
        }

        int x = (width - scaledWidth) / 2;
        int y = (height - scaledHeight) / 2;
        g2d.drawImage(img.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH), x, y, null);
        g2d.dispose();
        return new ImageIcon(buffered);
    }

    public abstract void operation();
    public abstract void operation2();

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public Icon getIcon() {
        return icon;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
    
    public Piece getPiece() {
		// TODO Auto-generated method stub
		return piece;
	}

    public void setLocation(int row, int col) {
        this.row = row;
        this.col = col;
    }
    
    public void setValue(String value) {
    	this.value = value;
    }

    public void setPiece(Piece piece) {
    	this.piece = piece;
    }
}

