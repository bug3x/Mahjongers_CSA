package src;
import javax.swing.*;
import java.awt.*;

public class ImageTest extends JPanel {

    private Image backgroundImage;

    public ImageTest() {
        // Load the image
        backgroundImage = new ImageIcon("imgs/mahjongboard1.png").getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.RED);
            g.drawString("Image not found", 20, 20);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Background Image Test");
        ImageTest panel = new ImageTest();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null); // Center the window
        frame.setVisible(true);
    }
}
