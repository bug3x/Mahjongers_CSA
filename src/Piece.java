package src;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;

public abstract class Piece {

    protected String type;     
    protected String value;     
    protected int row;
    protected int col;
    protected ImageIcon icon;
    protected String imageFileName;
    double scaleWidth = 10.00;
    double scaleHeight = 10.00;
    private Image tileImage;
    int width, height;
	int x, y;						//position of the object
    
    
    private AffineTransform tx;
	
    public Piece() {
    	tx = AffineTransform.getTranslateInstance(0, 0);
        width = 100;
		height = 100;
		x = 10;
		y = 10;
    }
    public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		
		init(x,y);
		
		if (icon != null) {
			icon.paintIcon(null, g, x, y);
		}
	}

    protected void setIcon(String fileName) {
        try {
            File imgFile = new File("imgs/" + fileName);
            if (imgFile.exists()) {
                BufferedImage img = ImageIO.read(imgFile);
                Image scaledImg = img.getScaledInstance(40, 60, Image.SCALE_SMOOTH);
                this.icon = new ImageIcon(scaledImg);
                System.out.println("Loaded image: " + fileName);
            } else {
                System.out.println("Image file not found: " + fileName);
                // Set a default blank tile image
                File blankFile = new File("imgs/blank.png");
                if (blankFile.exists()) {
                    BufferedImage img = ImageIO.read(blankFile);
                    Image scaledImg = img.getScaledInstance(40, 60, Image.SCALE_SMOOTH);
                    this.icon = new ImageIcon(scaledImg);
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading image: " + fileName);
            e.printStackTrace();
        }
    }

    private void init(double a, double b) {
		tx.setToTranslation(a, b);
		tx.scale(scaleWidth, scaleHeight);
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
	