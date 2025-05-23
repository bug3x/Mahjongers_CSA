package src;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;

//public class Board extends JPanel implements MouseListener, ActionListener {
//    JFrame frame;
//
//    GameLogic logic;
//    
//    private final int width 	= 800;
//	private final int height 	= 800;
//
//	private Image backgroundImage;
//

//
//
//    
//	public void setup() {
//        frame.setContentPane(this);
//        this.setLayout(new BorderLayout());
//        this.setOpaque(false);
//
//        setupBoard();
//        addMenus();
//
//        frame.setSize(width, height);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setUndecorated(true);
//        frame.setResizable(false);
//        frame.getContentPane().setCursor(new Cursor(Cursor.HAND_CURSOR));
//        frame.setVisible(true);
//
//        revalidate();
//        repaint();
//    }
//
//    public void setupBoard() {
//        JPanel mainPanel = new JPanel(new BorderLayout());
//        mainPanel.setOpaque(false);
//
//        JPanel playerBottomHand = new JPanel(new GridLayout(1, 13));
//        JPanel playerLeftHand = new JPanel(new GridLayout(13, 1));
//        JPanel playerTopHand = new JPanel(new GridLayout(1, 13));
//        JPanel playerRightHand = new JPanel(new GridLayout(13, 1));
//        JPanel centerDiscards = new JPanel(new GridLayout(10, 10));
//
//        JPanel[] hands = { playerBottomHand, playerLeftHand, playerTopHand, playerRightHand, centerDiscards };
//        for (JPanel panel : hands) panel.setOpaque(false);
//
//        mainPanel.add(playerBottomHand, BorderLayout.SOUTH);
//        mainPanel.add(playerTopHand, BorderLayout.NORTH);
//        mainPanel.add(playerLeftHand, BorderLayout.WEST);
//        mainPanel.add(playerRightHand, BorderLayout.EAST);
//        mainPanel.add(centerDiscards, BorderLayout.CENTER);
//
////        Tile[][] board = logic.getBoard(); // will result in button objects following your cursor
////        for (Tile[] row : board) {
////            for (Tile tile : row) {
////                centerDiscards.add(tile); // or a different panel as needed
////                tile.addMouseListener(this);
////            }
////        }
//
//        add(mainPanel, BorderLayout.CENTER);
//    }


    
    
    
	


public class Board extends JPanel implements MouseListener, ActionListener {
    private JFrame frame;
    private Image backgroundImage;

    public Board() {
        frame = new JFrame("Mahjong");
        backgroundImage = new ImageIcon("imgs/mahjongboard1.png").getImage();
        setup();
    }
    
    public void addToDiscard(Piece p, ArrayList<Piece> discard) {
        discard.add(p);
        updateDiscardPanel(); // hypothetical method
    }


    private void updateDiscardPanel() {
		// TODO Auto-generated method stub
		
	}

	public Piece getLastDiscard(ArrayList<Piece> discard) {
        return discard.get(discard.size() - 1);
    }

    public void removeLastDiscard(ArrayList<Piece> discard) {
        if (!discard.isEmpty()) discard.remove(discard.size() - 1);
    }

    public void setup() {
        // Setup frame first
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);
        frame.setResizable(false);
        frame.getContentPane().setCursor(new Cursor(Cursor.HAND_CURSOR));
        addMenus();

        // Setup this panel
        this.setLayout(new BorderLayout());
        this.setOpaque(false); // Let background image show

        // Add one transparent main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false); // transparent
        this.add(mainPanel, BorderLayout.CENTER);

        // Example transparent child
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Set this panel as content pane
        frame.setContentPane(this);
        frame.setVisible(true);
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
    
    public void reset() {
		 frame.dispose(); // Close the current JFrame
		 new Board();
	}
    
    
    
    public void addMenus() {
		//Where the GUI is created:
		JMenuBar menuBar;
		JMenu menu, submenu;
		JMenuItem menuItem;
		JRadioButtonMenuItem rbMenuItem;
		JCheckBoxMenuItem cbMenuItem;

		//Create the menu bar.
		menuBar = new JMenuBar();

		//Build the first menu.
		menu = new JMenu("Menu");
		menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription(
		        "The only menu in this program that has menu items");
		menuBar.add(menu);

		//a group of JMenuItems
		menuItem = new JMenuItem("New Game");
 
		menuItem.getAccessibleContext().setAccessibleDescription(
		        "Reset the game back to new game");
		menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Handle the click event for New Game
               reset();
            }
        });
		menu.add(menuItem);

		menuItem = new JMenuItem("Undo",
		                         new ImageIcon(""));
		menuItem.setMnemonic(KeyEvent.VK_B);
		menu.add(menuItem);

		menuItem = new JMenuItem("Quit", new ImageIcon("images/middle.gif"));
		menuItem.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Define an array of custom options for the dialog
		        Object[] options = { "Yes", "Cancel" };

		        // Display an option dialog with custom options
		        // The user's choice is stored in the 'choice'
		        // variable
		        int choice = JOptionPane.showOptionDialog(
		            null, // Parent component (null means center on screen)
		            "Do you want to proceed?", // Message to display
		            "Quit the Game", // Dialog title
		            JOptionPane.YES_NO_CANCEL_OPTION, // Option type (Yes, No, Cancel)
		            JOptionPane.QUESTION_MESSAGE, // Message type (question icon)
		            null, // Custom icon (null means no custom icon)
		            options, // Custom options array
		            options[1] // Initial selection (default is "Cancel")
		        );

		        // Check the user's choice and display a
		        // corresponding message
		        if (choice == JOptionPane.YES_OPTION) {
		            // If the user chose 'Yes'
		            // show a message indicating that they are
		            // proceeding
 		            System.exit(0);
		        }
		        else {
		            // If the user chose 'Cancel' or closed the
		            // dialog
		            // show a message indicating the operation is
		            // canceled
		            JOptionPane.showMessageDialog(null, "Operation canceled.");
		        }
			}	
		});
 		menu.add(menuItem);
		frame.setJMenuBar(menuBar);
	}

    public static void main(String[] args) {
        new Board();
    }

    // Required methods, stubbed
    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void actionPerformed(ActionEvent e) {}
}

