package src;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.GridLayout;
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

public class Board extends JPanel implements MouseListener, ActionListener {
    JFrame frame;

    GameLogic logic;
    
    private final int width 	= 800;
	private final int height 	= 800;

    
    public Board() {
    	frame = new JFrame("Mahjong");
        logic = new GameLogic(GameLogic.players);
        setup();
	}
    
    public void setup() {
		
		
		frame.setSize(width, height);
		setupBoard();
		addMenus();
		
		//add action for x button for a JFrame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setUndecorated(true);
		frame.setResizable(false);		
		frame.getContentPane().setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		//show the frame
		frame.setVisible(true);
		
	}
    
    public void setupBoard() {
    	JPanel mainPanel = new JPanel(new BorderLayout());

    	// Player Hands
    	JPanel playerBottomHand = new JPanel(new GridLayout(1, 13));
    	JPanel playerLeftHand = new JPanel(new GridLayout(13, 1));
    	JPanel playerTopHand = new JPanel(new GridLayout(1, 13));
    	JPanel playerRightHand = new JPanel(new GridLayout(13, 1));

    	// Discard Panels (central area), may need 4 of these
    	JPanel centerDiscards = new JPanel(new GridLayout(10, 10)); // Or something custom
    	
    	JPanel jp = new JPanel();

    	// Add to main layout
    	mainPanel.add(playerBottomHand, BorderLayout.SOUTH);
    	mainPanel.add(playerTopHand, BorderLayout.NORTH);
    	mainPanel.add(playerLeftHand, BorderLayout.WEST);
    	mainPanel.add(playerRightHand, BorderLayout.EAST);
    	mainPanel.add(centerDiscards, BorderLayout.CENTER);

    	frame.add(mainPanel);

		Tile[][] board = logic.getBoard();
		for(int i =0; i < board.length;i++) {
			for(int j = 0; j < board[0].length;j++) {
				jp.add(board[i][j]);
				board[i][j].addMouseListener(this);
			}
		}
		
		frame.add(jp);
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
    
    public void reset() {
		 frame.dispose(); // Close the current JFrame
		 new Board();
	}
    
    public void paint(Graphics g) {
		System.out.println("paint");
	}
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw tiles, melds, etc.
    }
    
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		repaint();
 	}
	
	// template for main menu screen buttons
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

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args) {
		// Create an instance of the board
		new Board();

	}
}
