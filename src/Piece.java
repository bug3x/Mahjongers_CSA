package src;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.*;

public abstract class Piece {

    protected String type;     
    protected String value;     
    protected int row;
    protected int col;
    protected ImageIcon icon;
    protected String imageFileName;
    
    private AffineTransform tx;
	
    public Piece(String type, String value, int row, int col, String imageFileName) {
        this.type = type;
        this.value = value;
        this.row = row;
        this.col = col;
        this.imageFileName = imageFileName;
        setIcon(imageFileName);
        
        tx = AffineTransform.getTranslateInstance(0, 0);
    }

    private void setIcon(String fileName) {
        ImageIcon rawIcon = new ImageIcon("imgs/" + fileName);
        Image img = rawIcon.getImage();
        this.icon = createScaledIcon(img, 80, 100);
    }

    private static ImageIcon createScaledIcon(Image img, int width, int height) {
        Image scaledImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage buffered = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = buffered.createGraphics();
        g2d.drawImage(scaledImage, 0, 0, null);
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
    
    public String getImageFileName() {
        return imageFileName;
    }

    public void setLocation(int row, int col) {
        this.row = row;
        this.col = col;
    }
    
    public void setValue(String value) {
    	this.value = value;
    }

    @Override
    public String toString() {
        return "Piece[type=" + type + ", value=" + value + ", row=" + row + ", col=" + col + "]";
    }
}
	