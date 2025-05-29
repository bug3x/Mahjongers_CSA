package src;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
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
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import java.awt.FlowLayout;

public class Board extends JPanel implements MouseListener, ActionListener {
    private JFrame frame;
    private Image backgroundImage;
    private JPanel mainPanel;
    private JPanel newPanel;
    
    private GameLogic logic;
    private ArrayList<Player> players;
    private List<Tile> bottomHandTiles = new ArrayList<>();
    public Board() {
        frame = new JFrame("Mahjong");
        backgroundImage = new ImageIcon("imgs/mahjongboard1.png").getImage();
        
        //construct player list
        players = new ArrayList<>();
        players.add(new Player("Player"));
        players.add(new Player("Bot 1"));
        players.add(new Player("Bot 2"));
        players.add(new Player("Bot 3"));

        logic = new GameLogic(players);
        logic.setupPlayers(players);
        
        //setup gui
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
    
    public void displayDrawPiece(Player player) {
        if (!logic.drawWall.isEmpty()) {
            Piece p = logic.drawWall.pop();
            player.addToHand(p);
            // update player panel and drawWallPanel
            updateDrawWallPanel();
            updatePlayerHand(player);
        }
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
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false); // transparent
        this.add(mainPanel, BorderLayout.CENTER);

        // Example transparent child
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Set this panel as content pane
        frame.setContentPane(this);
        frame.setVisible(true);
        
        this.logic = new GameLogic(players); // or however you're constructing i
        logic.setupPlayers(players);
        setupBoard(mainPanel, logic.drawWall, logic.deadWall);

    }
    
    public void setupBoard(JPanel mainPanel, Stack<Piece> drawW, List<Piece> deadW) {
        newPanel = new JPanel(new BorderLayout());
        newPanel.setOpaque(false);
        newPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        Dimension tileSize = new Dimension(40, 60);
        int hGap = 12;
        int vGap = 6;

        // === DRAW WALL ===
        JPanel drawWallWrapper = new JPanel(new GridLayout(2, 17, 2, 2));
        drawWallWrapper.setOpaque(false);
        drawWallWrapper.setPreferredSize(new Dimension(800, 120));
        for (int i = 0; i < Math.min(34, drawW.size()); i++) {
            Piece piece = drawW.get(i);
            Tile tile = new Tile(0, i);
            tile.setOpaque(false);
            tile.setContentAreaFilled(false);
            tile.setPreferredSize(tileSize);
            tile.setPiece(piece);
            drawWallWrapper.add(tile);
        }

        // === CENTER DISCARD GRID ===
        JPanel centerDiscards = new JPanel(new GridLayout(4, 4, 2, 2));
        centerDiscards.setOpaque(false);
        centerDiscards.setPreferredSize(new Dimension(240, 180));
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                Tile tile = new Tile(r, c);
                tile.setOpaque(false);
                tile.setContentAreaFilled(false);
                tile.setPreferredSize(tileSize);
                centerDiscards.add(tile);
            }
        }

        // === PLAYER HAND PANELS ===
        JPanel playerBottom = new JPanel(new FlowLayout(FlowLayout.CENTER, hGap, 0));
        JPanel playerTop = new JPanel(new FlowLayout(FlowLayout.CENTER, hGap, 0));
        JPanel playerLeft = new JPanel();
        JPanel playerRight = new JPanel();

        playerBottom.setOpaque(false);
        playerTop.setOpaque(false);
        playerLeft.setOpaque(false);
        playerRight.setOpaque(false);

        playerLeft.setLayout(new BoxLayout(playerLeft, BoxLayout.Y_AXIS));
        playerRight.setLayout(new BoxLayout(playerRight, BoxLayout.Y_AXIS));

        playerLeft.setAlignmentY(Component.CENTER_ALIGNMENT);
        playerRight.setAlignmentY(Component.CENTER_ALIGNMENT);

        // === WRAPPER PANELS ===
        JPanel bottomWrapper = new JPanel(new BorderLayout());
        
        JPanel leftWrapper = new JPanel(new BorderLayout());
        JPanel rightWrapper = new JPanel(new BorderLayout());

        bottomWrapper.setOpaque(false);
  
        leftWrapper.setOpaque(false);
        rightWrapper.setOpaque(false);

        bottomWrapper.setPreferredSize(new Dimension(800, 120));
       
        leftWrapper.setPreferredSize(new Dimension(100, 600));
        rightWrapper.setPreferredSize(new Dimension(100, 600));

        // === SPACING FIXES ===
        bottomWrapper.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
       

        // === VERTICAL CENTERING FOR LEFT/RIGHT HANDS ===
        JPanel leftInner = new JPanel();
        leftInner.setOpaque(false);
        leftInner.setLayout(new BoxLayout(leftInner, BoxLayout.Y_AXIS));
        leftInner.add(Box.createVerticalGlue());
        leftInner.add(playerLeft);
        leftInner.add(Box.createVerticalGlue());
        leftWrapper.add(leftInner, BorderLayout.CENTER);

        JPanel rightInner = new JPanel();
        rightInner.setOpaque(false);
        rightInner.setLayout(new BoxLayout(rightInner, BoxLayout.Y_AXIS));
        rightInner.add(Box.createVerticalGlue());
        rightInner.add(playerRight);
        rightInner.add(Box.createVerticalGlue());
        rightWrapper.add(rightInner, BorderLayout.CENTER);

  
        bottomWrapper.add(playerBottom, BorderLayout.CENTER);

        // === CREATE TILES ===
        bottomHandTiles.clear();
        for (int i = 0; i < 13; i++) {
            Tile b = new Tile(0, i);
            b.setOpaque(false);
            b.setContentAreaFilled(false);
            b.setPreferredSize(tileSize);
            playerBottom.add(b);
            bottomHandTiles.add(b);

            Tile t = new Tile(1, i);
            t.setOpaque(false);
            t.setContentAreaFilled(false);
            t.setPreferredSize(tileSize);
            playerTop.add(t);

            Tile l = new Tile(i, 0);
            l.setOpaque(false);
            l.setContentAreaFilled(false);
            l.setPreferredSize(tileSize);
            l.setAlignmentX(Component.CENTER_ALIGNMENT);
            playerLeft.add(l);

            Tile r = new Tile(i, 1);
            r.setOpaque(false);
            r.setContentAreaFilled(false);
            r.setPreferredSize(tileSize);
            r.setAlignmentX(Component.CENTER_ALIGNMENT);
            playerRight.add(r);
        }

        // === ASSEMBLE TABLE AREA ===
        JPanel tableArea = new JPanel(new BorderLayout());
        tableArea.setOpaque(false);
        tableArea.add(centerDiscards, BorderLayout.CENTER);
       
        tableArea.add(bottomWrapper, BorderLayout.SOUTH);
        tableArea.add(leftWrapper, BorderLayout.WEST);
        tableArea.add(rightWrapper, BorderLayout.EAST);

        // === ADD TO MAIN PANEL ===
        newPanel.add(drawWallWrapper, BorderLayout.NORTH);
        newPanel.add(tableArea, BorderLayout.CENTER);
        mainPanel.add(newPanel, BorderLayout.CENTER);
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


