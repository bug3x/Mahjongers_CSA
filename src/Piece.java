package src;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.net.URL;

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
    private Image forward;
    int width, height;
	int x, y;						//position of the object
    
    
    private AffineTransform tx;
	
    public Piece() {
    	forward 	= getImage("/imgs/"+"dong.png"); //load the image for Tree
        setIcon(imageFileName);
        
        tx = AffineTransform.getTranslateInstance(0, 0);
        width = 100;
		height = 100;
		x = 10;
		y = 10;
    }
    public void paint(Graphics g) {
		//these are the 2 lines of code needed draw an image on the screen
		Graphics2D g2 = (Graphics2D) g;
		
		
		
		init(x,y);
		g.setColor(Color.RED);
		g.drawRect(x, y, width, height);
		

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
    private void init(double a, double b) {
		tx.setToTranslation(a, b);
		tx.scale(scaleWidth, scaleHeight);
	}
    private Image getImage(String path) {
		Image tempImage = null;
		try {
			URL imageURL = Piece.class.getResource(path);
			tempImage = Toolkit.getDefaultToolkit().getImage(imageURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tempImage;
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
	