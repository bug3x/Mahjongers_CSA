package src;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
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

public class Board extends JPanel implements MouseListener, ActionListener {
    private JFrame frame;
    private Image backgroundImage;
    private JPanel mainPanel;
    private JPanel newPanel;
    private JPanel centerDiscards;
    
    private GameLogic logic;
    private ArrayList<Player> players;

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

    
    // Then implement updateDiscardPanel():
    private void updateDiscardPanel() {
        centerDiscards.removeAll();
     // In setupBoard(), after creating centerDiscards:
        this.centerDiscards = centerDiscards;
        ArrayList<Piece> discards = logic.getDiscards(); // Assume method exists
        for (Piece p : discards) {
            JLabel label = new JLabel(p.getIcon());
            centerDiscards.add(label);
        }
        centerDiscards.revalidate();
        centerDiscards.repaint();
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
//            updateDrawWallPanel();
//            updatePlayerHand(player);
        }
    }
    
    public void setup() {
        // Setup frame
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);
        frame.setResizable(false);
        frame.getContentPane().setCursor(new Cursor(Cursor.HAND_CURSOR));
        addMenus();

        // Setup this panel
        this.setLayout(new BorderLayout());
        this.setOpaque(false); // Let background show

        // Transparent main panel
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        this.add(mainPanel, BorderLayout.CENTER);

        // Set this panel as the content pane and show frame
        frame.setContentPane(this);
        frame.setVisible(true);

        // Build the visual board
        setupBoard(mainPanel, logic.drawWall, logic.deadWall);
        createPlayerHandsPanel(logic.drawWall);
    }



    public void setupBoard(JPanel mainPanel, Stack<Piece> drawW, List<Piece> deadW) {

        // Create a transparent panel with border padding that holds the whole game board
        newPanel = new JPanel(new BorderLayout());
        newPanel.setOpaque(false);
        newPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Define the base size of tiles (used for all hands, unrotated is vertical)
        Dimension baseTileSize = new Dimension(40, 60); 
        
        int hGap = 0; // horizontal spacing between tiles in top/bottom hand
        int vGap = 0; // vertical spacing between tiles in left/right hand

        // === CENTER DISCARD GRID ===
        int rows = 4, cols = 4;
        int spacing = 0;
        Dimension centerTileSize = new Dimension(60, 45); // smaller tile size for discards

        // Create 4x4 grid for center discards
        JPanel centerDiscards = new JPanel(new GridLayout(rows, cols, spacing, spacing));
        centerDiscards.setOpaque(false);

        // Compute the total width and height of the discard grid
        int gridW = centerTileSize.width * cols + spacing * (cols - 1);
        int gridH = centerTileSize.height * rows + spacing * (rows - 1);
        // Fix the discard panel's size so it doesn't stretch
        centerDiscards.setPreferredSize(new Dimension(gridW, gridH));
        centerDiscards.setMaximumSize(new Dimension(gridW, gridH));
        centerDiscards.setMinimumSize(new Dimension(gridW, gridH));
        centerDiscards.setSize(new Dimension(gridW, gridH));

        // Fill the discard grid with transparent placeholder Tile objects
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Tile tile = new Tile(r, c);
                tile.setOpaque(false);
                tile.setContentAreaFilled(false);
                tile.setPreferredSize(centerTileSize);
                tile.setMaximumSize(centerTileSize);
                tile.setMinimumSize(centerTileSize);
                tile.setSize(centerTileSize);
                centerDiscards.add(tile);
            }
        }

        // === PLAYER HAND PANELS ===
        // Create the four hand panels with appropriate layout direction
        JPanel playerBottom = new JPanel(new FlowLayout(FlowLayout.CENTER, hGap, 0));
        JPanel playerTop = new JPanel(new FlowLayout(FlowLayout.CENTER, hGap, 0));
        JPanel playerLeft = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, vGap));
        JPanel playerRight = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, vGap));

        // Make hand panels transparent
        playerBottom.setOpaque(false);
        playerTop.setOpaque(false);
        playerLeft.setOpaque(false);
        playerRight.setOpaque(false);

        // Left/right panels get vertical layout for stacking tiles
        playerLeft.setLayout(new BoxLayout(playerLeft, BoxLayout.Y_AXIS));
        playerRight.setLayout(new BoxLayout(playerRight, BoxLayout.Y_AXIS));

        // === WRAPPER PANELS ===
        // Wrapper panels hold the hand panels and help with alignment and padding
        JPanel bottomWrapper = new JPanel(new BorderLayout());
        JPanel leftWrapper = new JPanel(new BorderLayout());
        JPanel rightWrapper = new JPanel(new BorderLayout());

        bottomWrapper.setOpaque(false);
        leftWrapper.setOpaque(false);
        rightWrapper.setOpaque(false);

        // Set fixed sizes for wrapper panels to control layout space
        bottomWrapper.setPreferredSize(new Dimension(800, 120));
        leftWrapper.setPreferredSize(new Dimension(100, 600));
        rightWrapper.setPreferredSize(new Dimension(100, 600));

        // Add top margin to bottom wrapper for spacing from the discard grid
        bottomWrapper.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // === VERTICAL CENTERING FOR LEFT/RIGHT HANDS ===
        // These inner panels vertically center left/right hands
        JPanel leftInner = new JPanel();
        leftInner.setOpaque(false);
        leftInner.setLayout(new BoxLayout(leftInner, BoxLayout.Y_AXIS));
        leftInner.add(Box.createVerticalGlue());  // push content to center
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

        // Add bottom hand to its wrapper
        bottomWrapper.add(playerBottom, BorderLayout.CENTER);

        // === CREATE TILES ===
        for (int i = 0; i < 13; i++) {
            // Create bottom hand tile
            Tile b = new Tile(0, i);
            b.setOpaque(false);
            b.setContentAreaFilled(false);
            b.setPreferredSize(baseTileSize);
            playerBottom.add(b);

            // Create top hand tile
            Tile t = new Tile(1, i);
            t.setOpaque(false);
            t.setContentAreaFilled(false);
            t.setPreferredSize(baseTileSize);
            playerTop.add(t);

            // Create left hand tile (rotated)
            Tile l = new Tile(i, 0);
            l.setOpaque(false);
            l.setContentAreaFilled(false);
            l.setPreferredSize(baseTileSize);
            l.setRotated(true); // rotate 90 degrees to simulate horizontal tile
            playerLeft.add(l);

            // Create right hand tile (rotated)
            Tile r = new Tile(i, 2);
            r.setOpaque(false);
            r.setContentAreaFilled(false);
            r.setPreferredSize(baseTileSize);
            r.setRotated(true); // rotate 90 degrees to simulate horizontal tile
            playerRight.add(r);
        }

        // === TABLE AREA ===
        // This panel contains all major board components: hands and discards
        JPanel tableArea = new JPanel(new BorderLayout());
        tableArea.setOpaque(false);
        tableArea.add(centerDiscards, BorderLayout.CENTER);  // center discard grid
        tableArea.add(playerTop, BorderLayout.NORTH);         // top hand
        tableArea.add(bottomWrapper, BorderLayout.SOUTH);     // bottom hand
        tableArea.add(leftWrapper, BorderLayout.WEST);        // left hand
        tableArea.add(rightWrapper, BorderLayout.EAST);       // right hand

        // === ASSEMBLE FULL BOARD ===
        newPanel.add(tableArea, BorderLayout.CENTER); // add table to the main panel

        // === ADD TO MAIN PANEL ===
        mainPanel.add(newPanel, BorderLayout.CENTER); // add everything to the input panel
    }



    private JPanel createPlayerHandsPanel(Stack<Piece> drawW) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel playerBottomHand = new JPanel(new GridLayout(1, 13));
        JPanel playerTopHand = new JPanel(new GridLayout(1, 13));
        JPanel playerLeftHand = new JPanel(new GridLayout(13, 1));
        JPanel playerRightHand = new JPanel(new GridLayout(13, 1));

        for (JPanel p : new JPanel[]{playerBottomHand, playerTopHand, playerLeftHand, playerRightHand})
            p.setOpaque(false);

        panel.add(playerBottomHand, BorderLayout.SOUTH);
        panel.add(playerTopHand, BorderLayout.NORTH);
        panel.add(playerLeftHand, BorderLayout.WEST);
        panel.add(playerRightHand, BorderLayout.EAST);

        for (int i = 0; i < 13; i++) {
            playerBottomHand.add(new JLabel(drawW.pop().getIcon()));
            playerRightHand.add(new JLabel(rotateIcon(drawW.pop().getIcon(), 270)));
            playerTopHand.add(new JLabel(rotateIcon(drawW.pop().getIcon(), 180)));
            playerLeftHand.add(new JLabel(rotateIcon(drawW.pop().getIcon(), 90)));
        }

        return panel;
    }


//    private JPanel createWallPanels(Stack<Piece> drawW, List<Piece> deadW) {
//        JPanel topWall = new JPanel(new GridLayout(2, 19));
//        JPanel rightWall = new JPanel(new GridLayout(19, 2));
//        JPanel bottomWall = new JPanel(new GridLayout(2, 19));
//        JPanel leftWall = new JPanel(new GridLayout(19, 2));
//        for (JPanel panel : new JPanel[]{topWall, rightWall, bottomWall, leftWall})
//            panel.setOpaque(false);
//
//        int totalTiles = drawW.size() + deadW.size();
//        for (int i = 0; i < totalTiles; i++) {
//            Piece piece = (i < drawW.size()) ? drawW.get(i) : deadW.get(i - drawW.size());
//            JLabel label;
//
//            if (i < 38) {
//                label = new JLabel(rotateIcon(piece.getIcon(), 180));
//                topWall.add(label);
//            } else if (i < 72) {
//                label = new JLabel(rotateIcon(piece.getIcon(), 270));
//                rightWall.add(label);
//            } else if (i < 110) {
//                label = new JLabel(piece.getIcon());
//                bottomWall.add(label);
//            } else {
//                label = new JLabel(rotateIcon(piece.getIcon(), 90));
//                leftWall.add(label);
//            }
//        }
//
//        // Dora indicator (first tile of dead wall)
//        if (!deadW.isEmpty()) {
//            JLabel doraLabel = new JLabel(rotateIcon(deadW.get(0).getIcon(), 270));
//            doraLabel.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
//            rightWall.add(doraLabel); // Show on right wall
//        }
//
//        JPanel wallWrapper = new JPanel(new BorderLayout());
//        wallWrapper.setOpaque(false);
//        wallWrapper.add(topWall, BorderLayout.NORTH);
//        wallWrapper.add(rightWall, BorderLayout.EAST);
//        wallWrapper.add(bottomWall, BorderLayout.SOUTH);
//        wallWrapper.add(leftWall, BorderLayout.WEST);
//
//        return wallWrapper;
//    }


    private ImageIcon rotateIcon(Icon icon, double angle) {
        if (!(icon instanceof ImageIcon)) return null;
        ImageIcon ii = (ImageIcon) icon;
        Image img = ii.getImage();
        int w = img.getWidth(null);
        int h = img.getHeight(null);

        BufferedImage rotated = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
        g2d.rotate(Math.toRadians(angle), w / 2.0, h / 2.0);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return new ImageIcon(rotated);
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

    public JPanel getCenterDiscardsPanel() {
        return centerDiscards;
    }
}

