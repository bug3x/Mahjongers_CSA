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
import java.util.Arrays;
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
import javax.swing.Timer;
import javax.swing.JButton;
import javax.swing.JDialog;

public class Board extends JPanel implements MouseListener, ActionListener {
    private JFrame frame;
    private Image backgroundImage;
    private JPanel mainPanel;
    private JPanel newPanel;

    private JPanel bottomDiscards;
    private JPanel topDiscards;
    private JPanel leftDiscards;
    private JPanel rightDiscards;
    
    
    private GameLogic logic;
    private ArrayList<Player> players;
    private List<Tile> bottomHandTiles = new ArrayList<>();
    ArrayList<Piece> pieces = new ArrayList<>();
    private int currentPlayerIndex = 0;  // Track current player

    private JPanel meldPanel; // Floating meld button panel
    private Timer meldTimer; // Timer for auto-pass
    private JDialog gifOverlay; // Overlay for meld GIFs

    private boolean isBotTurn = false;

    private JLabel[] turnLabels = new JLabel[4]; // One for each player position
    private Timer botCountdownTimer; // For bot turn countdown
    private int botCountdownSeconds; // For bot turn countdown

    // New panels for bot melds
    private JPanel botTopMelds; // For bot at top
    private JPanel botLeftMelds; // For bot at left
    private JPanel botRightMelds; // For bot at right

    // Human meld panel
    private JPanel humanMeldPanel; // For human player's melds

    private JPanel bottomWrapper;
    private JPanel topWrapper;
    private JPanel leftWrapper;
    private JPanel rightWrapper;

    // Last discarded piece and its discarder for human meld reactions
    private Piece lastDiscardedPiece;
    private int discarderOfLastPiece;

    // New fields for bot hand panels
    private JPanel playerTopHandPanel;
    private JPanel playerLeftHandPanel;
    private JPanel playerRightHandPanel;

    // New field for human hand panel
    private JPanel playerBottomHandPanel;

    // Game phase management
    private String gamePhase = "DRAW_PHASE"; // Initial phase
    private static final String PHASE_DRAW = "DRAW_PHASE";
    private static final String PHASE_DISCARD = "DISCARD_PHASE";
    private static final String PHASE_MELD_PENDING_HUMAN = "MELD_PENDING_HUMAN";
    private static final String PHASE_ROUND_END = "ROUND_END";

    // Declare tableArea as a class member
    private JPanel tableArea;

    public Board() {
        frame = new JFrame("Mahjong");
        backgroundImage = new ImageIcon("imgs/mahjongboard1.png").getImage();  // Load background

        // Initialize player list
        players = new ArrayList<>();
        players.add(new Player("You"));  // User
        players.add(new Player("Mr. Biatchin"));  // Right
        players.add(new Player("Just_Do_It_Later"));  // Top
        players.add(new Player("Mr. David"));  // Left

        // Game setup
        logic = new GameLogic(players);
        logic.setupPlayers(players);

        // Set up the GUI
        setup();

        // === MELD UI LOGIC ===
        meldPanel = new JPanel();
        meldPanel.setOpaque(false);
        meldPanel.setVisible(false);
        meldPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5)); // Smaller gap
        this.add(meldPanel, BorderLayout.CENTER);

        // Initialize turn labels
        for (int i = 0; i < 4; i++) {
            turnLabels[i] = new JLabel("", JLabel.CENTER);
            turnLabels[i].setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
            turnLabels[i].setForeground(Color.YELLOW);
            turnLabels[i].setOpaque(false);
            turnLabels[i].setVisible(false);
        }

        // Add turn labels in correct positions
        this.setLayout(new BorderLayout());
        this.add(turnLabels[0], BorderLayout.SOUTH);  // You (bottom)
        this.add(turnLabels[1], BorderLayout.EAST);   // Mr. Biatchin (right)
        this.add(turnLabels[2], BorderLayout.NORTH);  // Just_Do_It_Later (top)
        this.add(turnLabels[3], BorderLayout.WEST);   // Mr. David (left)
    }

    // Helper to create a smaller icon for left/right discards
    private ImageIcon getSmallIcon(Piece piece) {
        Icon origIcon = piece.getIcon();
        if (origIcon instanceof ImageIcon) {
            Image img = ((ImageIcon) origIcon).getImage();
            Image scaled = img.getScaledInstance(30, 40, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        }
        return null;
    }

    // Adds a piece to discard list and updates GUI
    public void addToDiscard(Piece p, ArrayList<Piece> discard, JPanel discardPanel) {
        discard.add(p);
        updateDiscardPanel(discard, discardPanel);
    }

    // Refreshes the discard panel with current discard pile
    private void updateDiscardPanel(ArrayList<Piece> discards, JPanel discardPanel) {
        discardPanel.removeAll();  // Clear old tiles
        boolean isHorizontal = (discardPanel == topDiscards || discardPanel == bottomDiscards);
        boolean isLeftOrRight = (discardPanel == leftDiscards || discardPanel == rightDiscards);
        int wrap = 7;
        int gap = 1;
        if (isHorizontal || isLeftOrRight) {
            int major = isHorizontal ? (int) Math.ceil(discards.size() / (double)wrap) : wrap;
            int minor = isHorizontal ? wrap : (int) Math.ceil(discards.size() / (double)wrap);
            discardPanel.setLayout(new GridLayout(major, minor, gap, gap));
        }
        for (Piece p : discards) {
            JLabel label = new JLabel(getSmallIcon(p));
            discardPanel.add(label);
        }
        discardPanel.revalidate();
        discardPanel.repaint();
    }

    // Helper to make discard panels with orientation
    private JPanel createDiscardPanel(String orientation) {
        JPanel panel;
        int gap = 1;
        if ("horizontal".equals(orientation)) {
            panel = new JPanel(new GridLayout(1, 7, gap, gap));
        } else {
            panel = new JPanel(new GridLayout(7, 1, gap, gap));
        }
        panel.setOpaque(false);
        // Preferred size is dynamic, so keep it minimal
        panel.setPreferredSize(null);
        return panel;
    }

    // Draws a tile from wall and adds to player's hand
    public void displayDrawPiece(Player player) {
        if (!logic.drawWall.isEmpty()) {
            Piece p = logic.drawWall.pop();  // Take from wall
            player.addToHand(p);             // Add to hand
        }
    }
    
 // GUI frame and layout setup
    public void setup() {
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);
        frame.setResizable(false);
        frame.getContentPane().setCursor(new Cursor(Cursor.HAND_CURSOR));

        this.setLayout(new BorderLayout());
        this.setOpaque(false);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        this.add(mainPanel, BorderLayout.CENTER);

        setupBoard(mainPanel, logic.drawWall, logic.deadWall);  // Build board layout
        addMenus();

        // After initial setup, the human player draws their first tile
        // The game starts with human drawing, then discarding.
        if (!logic.drawWall.isEmpty()) {
            players.get(0).addToHand(logic.drawWall.pop());
        }
        updateDisplay(); // Refresh human hand after initial draw
        gamePhase = PHASE_DISCARD; // Human is now ready to discard

        frame.setContentPane(this);
        frame.setVisible(true);

        frame.revalidate();
        frame.repaint();
    }

    // Rotate to next player's turn
    public void switchToNextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % 4;
        updateTurnLabel();
        updateDisplay();  // Refresh tile display
    }

    // Refresh tiles based on current player's hand
    private void updateDisplay() {
        // Only update human player's hand (player 0)
        if (currentPlayerIndex == 0) {
            Player humanPlayer = players.get(0);
            ArrayList<Piece> currentHand = humanPlayer.getHand();
            sortPlayerHand(currentHand);

            playerBottomHandPanel.removeAll(); // Clear existing tiles
            bottomHandTiles.clear(); // Clear the list of Tile objects

            Dimension tileSize = new Dimension(40, 60);

            for (int i = 0; i < currentHand.size(); i++) {
                Tile b = new Tile(0, i);  // Bottom player
                b.setPiece(currentHand.get(i));
                b.setOpaque(false);
                b.setContentAreaFilled(false);
                b.setPreferredSize(tileSize);
                b.addMouseListener(this);  // Interactivity
                playerBottomHandPanel.add(b);
                bottomHandTiles.add(b); // Add the new Tile object to the list
            }
            playerBottomHandPanel.revalidate();
            playerBottomHandPanel.repaint();
            mainPanel.revalidate(); // Revalidate the main panel
            mainPanel.repaint();    // Repaint the main panel
        }
    }


 // Build full game board layout with player hands and discards
    public void setupBoard(JPanel mainPanel, Stack<Piece> drawW, List<Piece> deadW) {
        newPanel = new JPanel(new BorderLayout());
        newPanel.setOpaque(false);
        newPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        Dimension tileSize = new Dimension(40, 60);
        int hGap = 4;
        int vGap = 2;

        // Initialize discard panels
        bottomDiscards = createDiscardPanel("horizontal");
        topDiscards = createDiscardPanel("horizontal");
        leftDiscards = createDiscardPanel("vertical");
        rightDiscards = createDiscardPanel("vertical");

        // Initialize bot meld panels
        botTopMelds = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        botTopMelds.setOpaque(false);
        botLeftMelds = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5)); // Changed horizontal gap to 0 and vertical gap to 5 for vertical stacking
        botLeftMelds.setOpaque(false);
        botRightMelds = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5)); // Changed horizontal gap to 0 and vertical gap to 5 for vertical stacking
        botRightMelds.setOpaque(false);

        // Initialize human meld panel
        humanMeldPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        humanMeldPanel.setOpaque(false);

        // Create player hand areas
        playerBottomHandPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, hGap, 0)); // Assign to class field
        playerTopHandPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, hGap, 0)); // Assign to class field
        playerLeftHandPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, vGap)); // Assign to class field
        playerRightHandPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, vGap)); // Assign to class field

        for (JPanel panel : new JPanel[]{playerBottomHandPanel, playerTopHandPanel, playerLeftHandPanel, playerRightHandPanel})
            panel.setOpaque(false);

        int verticalSpace = tileSize.height * 13 + vGap * 12;
        playerLeftHandPanel.setPreferredSize(new Dimension(60, verticalSpace)); // Use class field
        playerRightHandPanel.setPreferredSize(new Dimension(60, verticalSpace)); // Use class field

        // Wrappers for positioning (ensure these are assignments to class fields)
        bottomWrapper = new JPanel(new BorderLayout());
        topWrapper = new JPanel(new BorderLayout());
        leftWrapper = new JPanel(new BorderLayout());
        rightWrapper = new JPanel(new BorderLayout());

        for (JPanel wrapper : new JPanel[]{bottomWrapper, topWrapper, leftWrapper, rightWrapper})
            wrapper.setOpaque(false);

        bottomWrapper.setPreferredSize(new Dimension(800, 120));
        topWrapper.setPreferredSize(new Dimension(800, 100));
        leftWrapper.setPreferredSize(new Dimension(60, verticalSpace + 40));
        rightWrapper.setPreferredSize(new Dimension(60, verticalSpace + 40));

        bottomWrapper.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));
        topWrapper.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        leftWrapper.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        rightWrapper.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // --- NEW: Create FlowLayout wrappers for top/bottom discard panels ---
        JPanel bottomDiscardFlow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bottomDiscardFlow.setOpaque(false);
        bottomDiscardFlow.add(bottomDiscards);
        JPanel topDiscardFlow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topDiscardFlow.setOpaque(false);
        topDiscardFlow.add(topDiscards);
        // --- END NEW ---

        // Generate player's starting hand
        bottomHandTiles.clear();
        ArrayList<Piece> currentHand = players.get(currentPlayerIndex).getHand();
        sortPlayerHand(currentHand);

        for (int i = 0; i < 13; i++) {
            Tile b = new Tile(0, i);  // Bottom player
            if (i < currentHand.size()) b.setPiece(currentHand.get(i));
            b.setOpaque(false);
            b.setContentAreaFilled(false);
            b.setPreferredSize(tileSize);
            b.addMouseListener(this);  // Interactivity
            playerBottomHandPanel.add(b); // Use class field
            bottomHandTiles.add(b);

            Tile t = new Tile(1, i);  // Top bot
            t.setOpaque(false);
            t.setContentAreaFilled(false);
            t.setPreferredSize(tileSize);
            t.setPiece(createBlankPiece("top"));
            playerTopHandPanel.add(t); // Use class field

            Tile l = new Tile(i, 0);  // Left bot
            l.setOpaque(false);
            l.setContentAreaFilled(false);
            l.setPreferredSize(tileSize);
            l.setPiece(createBlankPiece("left"));
            if (l.getIcon() != null) l.setIcon(rotateIcon(l.getIcon(), -90));
            playerLeftHandPanel.add(l); // Use class field

            Tile r = new Tile(i, 1);  // Right bot
            r.setOpaque(false);
            r.setContentAreaFilled(false);
            r.setPreferredSize(tileSize);
            r.setPiece(createBlankPiece("right"));
            if (r.getIcon() != null) r.setIcon(rotateIcon(r.getIcon(), 90));
            playerRightHandPanel.add(r); // Use class field
        }

        // Assemble wrappers with hands and discards
        bottomWrapper.add(playerBottomHandPanel, BorderLayout.CENTER); // Use class field
        bottomWrapper.add(bottomDiscardFlow, BorderLayout.NORTH);
        bottomWrapper.add(humanMeldPanel, BorderLayout.WEST); // Add human meld panel

        topWrapper.add(playerTopHandPanel, BorderLayout.CENTER); // Use class field
        topWrapper.add(topDiscardFlow, BorderLayout.SOUTH);

        leftWrapper.add(playerLeftHandPanel, BorderLayout.CENTER); // Use class field
        leftWrapper.add(leftDiscards, BorderLayout.EAST);

        rightWrapper.add(playerRightHandPanel, BorderLayout.CENTER); // Use class field
        rightWrapper.add(rightDiscards, BorderLayout.WEST);

        // Add bot meld panels to their respective wrappers
        topWrapper.add(botTopMelds, BorderLayout.SOUTH); // Positioned above the top player's hand
        leftWrapper.add(botLeftMelds, BorderLayout.EAST); // Positioned to the right of the left player's hand
        rightWrapper.add(botRightMelds, BorderLayout.WEST); // Positioned to the left of the right player's hand

        // Optional: Central discard zone
        JPanel centralDiscards = new JPanel(new BorderLayout());
        centralDiscards.setOpaque(false);
        centralDiscards.add(topDiscards, BorderLayout.NORTH);
        centralDiscards.add(bottomDiscards, BorderLayout.SOUTH);
        centralDiscards.add(leftDiscards, BorderLayout.WEST);
        centralDiscards.add(rightDiscards, BorderLayout.EAST);

        // Initialize tableArea as a class member
        tableArea = new JPanel(new BorderLayout());
        tableArea.setOpaque(false);
        tableArea.add(topWrapper, BorderLayout.NORTH);
        tableArea.add(bottomWrapper, BorderLayout.SOUTH);
        tableArea.add(leftWrapper, BorderLayout.WEST);
        tableArea.add(rightWrapper, BorderLayout.EAST);
        tableArea.add(centralDiscards, BorderLayout.CENTER);  // Discards go in center

        newPanel.add(tableArea, BorderLayout.CENTER);
        mainPanel.add(newPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    // Creates a blank piece with back-face icon
    private Piece createBlankPiece(String side) {
        String sideImage;
        switch(side) {
            case "left": sideImage = "side4.png"; break;
            case "right": sideImage = "side2.png"; break;
            default: sideImage = "blank.png"; break;
        }
        return new BlankPiece(-1, -1, sideImage);
    }

    // Sort hand by suit then value
    private void sortPlayerHand(ArrayList<Piece> hand) {
        hand.sort((p1, p2) -> {
            String[] typeOrder = {"Number", "Circle", "Bamboo", "Wind", "Dragon"};
            int type1Index = Arrays.asList(typeOrder).indexOf(p1.getType());
            int type2Index = Arrays.asList(typeOrder).indexOf(p2.getType());
            if (type1Index != type2Index) return type1Index - type2Index;

            if (p1.getType().equals("Wind")) {
                String[] windOrder = {"East", "South", "West", "North"};
                return Arrays.asList(windOrder).indexOf(p1.getValue()) -
                       Arrays.asList(windOrder).indexOf(p2.getValue());
            } else if (p1.getType().equals("Dragon")) {
                String[] dragonOrder = {"Red", "Green", "White"};
                return Arrays.asList(dragonOrder).indexOf(p1.getValue()) -
                       Arrays.asList(dragonOrder).indexOf(p2.getValue());
            } else {
                return Integer.parseInt(p1.getValue()) - Integer.parseInt(p2.getValue());
            }
        });
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


    private JPanel createWallPanels(Stack<Piece> drawW, List<Piece> deadW) {
        JPanel topWall = new JPanel(new GridLayout(2, 19));
        JPanel rightWall = new JPanel(new GridLayout(19, 2));
        JPanel bottomWall = new JPanel(new GridLayout(2, 19));
        JPanel leftWall = new JPanel(new GridLayout(19, 2));
        for (JPanel panel : new JPanel[]{topWall, rightWall, bottomWall, leftWall})
            panel.setOpaque(false);

        int totalTiles = drawW.size() + deadW.size();
        for (int i = 0; i < totalTiles; i++) {
            Piece piece = (i < drawW.size()) ? drawW.get(i) : deadW.get(i - drawW.size());
            JLabel label;

            if (i < 38) {
                label = new JLabel(rotateIcon(piece.getIcon(), 180));
                topWall.add(label);
            } else if (i < 76) {
                label = new JLabel(rotateIcon(piece.getIcon(), 270));
                rightWall.add(label);
            } else if (i < 114) {
                label = new JLabel(piece.getIcon());
                bottomWall.add(label);
            } else {
                label = new JLabel(rotateIcon(piece.getIcon(), 90));
                leftWall.add(label);
            }
        }

        // Dora indicator (first tile of dead wall)
        if (!deadW.isEmpty()) {
            JLabel doraLabel = new JLabel(rotateIcon(deadW.get(0).getIcon(), 270));
            doraLabel.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            rightWall.add(doraLabel); // Show on right wall
        }

        JPanel wallWrapper = new JPanel(new BorderLayout());
        wallWrapper.setOpaque(false);
        wallWrapper.add(topWall, BorderLayout.NORTH);
        wallWrapper.add(rightWall, BorderLayout.EAST);
        wallWrapper.add(bottomWall, BorderLayout.SOUTH);
        wallWrapper.add(leftWall, BorderLayout.WEST);

        return wallWrapper;
    }


 // Rotate image by angle and return icon
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

    public void discardPiece(Piece piece, int playerIndex) {
        @SuppressWarnings("unchecked")
		List<Piece>[] gameDiscards = new List[]{
            logic.getDiscardForPlayer(0),
            logic.getDiscardForPlayer(1),
            logic.getDiscardForPlayer(2),
            logic.getDiscardForPlayer(3),
        };

        JPanel[] boardDiscards = new JPanel[]{
            bottomDiscards,
            rightDiscards,
            topDiscards,
            leftDiscards
        };

        if (playerIndex < 0 || playerIndex >= gameDiscards.length) {
            throw new IllegalArgumentException("Invalid player index: " + playerIndex);
        }

        addToDiscard(piece, (ArrayList<Piece>) gameDiscards[playerIndex], boardDiscards[playerIndex]);
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
        for (Piece piece : pieces) {
	        piece.paint(g); // Assuming `Totem` has a paint method
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

    @Override
    public void mouseClicked(MouseEvent e) {
        // Only allow interaction if it's the human's turn and in the DISCARD phase
        if (currentPlayerIndex != 0 || !gamePhase.equals(PHASE_DISCARD)) return;
        if (!(e.getSource() instanceof Tile clickedTile)) return;
        if (!clickedTile.hasPiece()) return;

        // The player is discarding a tile
        Piece piece = clickedTile.getPiece();
        // clickedTile.removePiece(); // Removed: updateDisplay() will handle visual removal/rebuild

        // Remove from hand and add to global discards
        Player currentPlayer = players.get(currentPlayerIndex);
        currentPlayer.getHand().remove(piece); // Remove from hand
        logic.getDiscards().add(piece); // Global discard pool

        // Add to discard panel and player's discard list
        JPanel discardPanel = switch (currentPlayerIndex) {
            case 0 -> bottomDiscards;
            case 1 -> rightDiscards;
            case 2 -> topDiscards;
            case 3 -> leftDiscards;
            default -> throw new IllegalStateException("Invalid player index");
        };
        addToDiscard(piece, currentPlayer.getDiscards(), discardPanel);

        // Check for melds for player 0 after every discard
        promptHumanForMelds(piece, currentPlayerIndex);

        // If meld buttons are shown, game is in MELD_PENDING_HUMAN phase, so return.
        // The rest of the turn progression will be handled by performMeld or the meldTimer.
        if (meldPanel.isVisible()) {
            gamePhase = PHASE_MELD_PENDING_HUMAN; // Update game phase
            return;
        }

        // If no meld options were shown, proceed with normal turn advancement
        // Human needs to draw a tile (if not already done by a meld response)
        if (!logic.drawWall.isEmpty() && currentPlayer.getHand().size() < 14) { // Ensure hand size before drawing
            Piece newPiece = logic.drawWall.pop();
            currentPlayer.addToHand(newPiece);
        }
        updateDisplay(); // Refresh human hand after drawing (if no meld)

        // Advance turn if no reactions from other players
        advanceTurnIfNoReactions(piece, currentPlayerIndex); // Pass the piece and original discarder index
    }

    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void actionPerformed(ActionEvent e) {}

    // --- Helper to show meld buttons ---
    private void showMeldButtons(java.util.List<String> melds) {
        meldPanel.removeAll();
        for (String meld : melds) {
            String imgFile = switch (meld) {
                case "chow" -> "chi.png";
                case "pong" -> "Pon.png";
                case "kong" -> "Kong.png";
                case "riichi" -> "Riichi.png";
                case "tsumo" -> "Tsumo.png";
                case "ron" -> "ron.png";
                default -> null;
            };
            if (imgFile != null) {
                ImageIcon icon = new ImageIcon("imgs/" + imgFile);
                Image img = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                JButton btn = new JButton(new ImageIcon(img));
                btn.setFocusPainted(false);
                btn.setContentAreaFilled(false);
                btn.setBorderPainted(false);
                btn.setOpaque(false);
                btn.setToolTipText(meld.substring(0,1).toUpperCase() + meld.substring(1));
                btn.addActionListener(e -> {
                    meldPanel.setVisible(false);
                    if (meldTimer != null) meldTimer.stop();
                    performMeld(meld);
                });
                meldPanel.add(btn);
            }
        }
        meldPanel.setVisible(true);
        meldPanel.revalidate();
        meldPanel.repaint();
        // Start 5s timer for auto-pass
        if (meldTimer != null) meldTimer.stop();
        meldTimer = new Timer(5000, e -> {
            meldPanel.setVisible(false);
            // Human player passed on meld. Now check if any other players react, then advance turn.
            gamePhase = PHASE_DISCARD; // Reset phase after human decision to pass on meld
            advanceTurnIfNoReactions(lastDiscardedPiece, discarderOfLastPiece);
        });
        meldTimer.setRepeats(false);
        meldTimer.start();
    }

    // --- Helper to perform meld action ---
    private void performMeld(String meld) {
        meldPanel.setVisible(false);
        if (meldTimer != null) meldTimer.stop();

        Player humanPlayer = players.get(0);
        java.util.List<Piece> meldedPieces = new java.util.ArrayList<>();

        // Remove the last discarded piece from the discard panel
        removePieceFromPlayerDiscardPanel(lastDiscardedPiece, discarderOfLastPiece);

        meldedPieces.add(lastDiscardedPiece); // The claimed discarded tile

        switch (meld) {
            case "pong":
                // Find and remove 2 matching tiles from human's hand
                int pongCount = 0;
                java.util.Iterator<Piece> pongIterator = humanPlayer.getHand().iterator();
                while (pongIterator.hasNext() && pongCount < 2) {
                    Piece p = pongIterator.next();
                    if (p.getType().equals(lastDiscardedPiece.getType()) && p.getValue().equals(lastDiscardedPiece.getValue())) {
                        meldedPieces.add(p);
                        pongIterator.remove();
                        pongCount++;
                    }
                }
                humanPlayer.getMelds().add(new Meld("pong", meldedPieces));
                // After Pong, human player must discard one tile
                gamePhase = PHASE_DISCARD; // Set phase to allow human to discard
                currentPlayerIndex = 0; // Ensure it's human's turn to discard
                updateTurnLabel();
                updateDisplay(); // Refresh human hand to enable interactivity for discard
                break;
            case "kong":
                // Find and remove 3 matching tiles from human's hand
                int kongCount = 0;
                java.util.Iterator<Piece> kongIterator = humanPlayer.getHand().iterator();
                while (kongIterator.hasNext() && kongCount < 3) {
                    Piece p = kongIterator.next();
                    if (p.getType().equals(lastDiscardedPiece.getType()) && p.getValue().equals(lastDiscardedPiece.getValue())) {
                        meldedPieces.add(p);
                        kongIterator.remove();
                        kongCount++;
                    }
                }
                humanPlayer.getMelds().add(new Meld("kong", meldedPieces));
                // After Kong, human player must discard one tile
                gamePhase = PHASE_DISCARD; // Set phase to allow human to discard
                currentPlayerIndex = 0; // Ensure it's human's turn to discard
                updateTurnLabel();
                // Also, draw a replacement tile after Kong
                if (!logic.drawWall.isEmpty()) {
                    humanPlayer.addToHand(logic.drawWall.pop());
                }
                updateDisplay(); // Refresh hand after drawing replacement
                break;
            case "chow":
                // Find and remove the two sequential tiles from human's hand
                int lastDiscardValue = Integer.parseInt(lastDiscardedPiece.getValue());
                Piece piece1 = findAndRemovePiece(humanPlayer.getHand(), lastDiscardedPiece.getType(), String.valueOf(lastDiscardValue - 1));
                Piece piece2 = findAndRemovePiece(humanPlayer.getHand(), lastDiscardedPiece.getType(), String.valueOf(lastDiscardValue - 2));
                Piece piece3 = findAndRemovePiece(humanPlayer.getHand(), lastDiscardedPiece.getType(), String.valueOf(lastDiscardValue + 1));
                Piece piece4 = findAndRemovePiece(humanPlayer.getHand(), lastDiscardedPiece.getType(), String.valueOf(lastDiscardValue + 2));

                if (piece1 != null && piece2 != null) {
                    meldedPieces.add(piece1);
                    meldedPieces.add(piece2);
                } else if (piece1 != null && piece3 != null) {
                    meldedPieces.add(piece1);
                    meldedPieces.add(piece3);
                } else if (piece3 != null && piece4 != null) {
                    meldedPieces.add(piece3);
                    meldedPieces.add(piece4);
                }
                humanPlayer.getMelds().add(new Meld("chow", meldedPieces));
                // After Chow, human player must discard one tile
                gamePhase = PHASE_DISCARD; // Set phase to allow human to discard
                currentPlayerIndex = 0; // Ensure it's human's turn to discard
                updateTurnLabel();
                updateDisplay(); // Refresh human hand to enable interactivity for discard
                break;
            case "riichi":
                showGifOverlay("imgs/mahjong-all-day-riichi.gif");
                // Riichi is a declaration, not a discard reaction that takes a tile.
                // Player will discard after declaring riichi on their turn.
                // Set phase to discard to allow human to discard
                gamePhase = PHASE_DISCARD;
                currentPlayerIndex = 0;
                updateTurnLabel();
                updateDisplay();
                break;
            case "tsumo":
                showGifOverlay("imgs/tsumo-mahjong.gif");
                gamePhase = PHASE_ROUND_END;
                return; // End turn/game
            case "ron":
                showGifOverlay("imgs/yakuza-ron.gif");
                gamePhase = PHASE_ROUND_END;
                return; // End turn/game
        }

        // Display meld for human player
        displayMeld(humanPlayer, meldedPieces); // Reuse displayMeld for human, it will use humanMeldPanel

    }

    // --- Helper to show GIF overlay ---
    private void showGifOverlay(String gifPath) {
        if (gifOverlay != null) gifOverlay.dispose();
        gifOverlay = new JDialog(frame, true);
        gifOverlay.setUndecorated(true);
        JLabel gifLabel = new JLabel(new ImageIcon(gifPath));
        gifOverlay.getContentPane().add(gifLabel);
        gifOverlay.pack();
        gifOverlay.setLocationRelativeTo(frame);
        // Timer to close overlay after GIF duration (3s default)
        Timer closeTimer = new Timer(3000, e -> gifOverlay.dispose());
        closeTimer.setRepeats(false);
        closeTimer.start();
        gifOverlay.setVisible(true);
    }

    // --- Prompt human player for melds after a discard ---
    private void promptHumanForMelds(Piece lastDiscard, int discarderIndex) {
        java.util.List<String> melds = new java.util.ArrayList<>();
        Player humanPlayer = players.get(0);

        // Store the last discarded piece and its discarder for performMeld to use
        this.lastDiscardedPiece = lastDiscard;
        this.discarderOfLastPiece = discarderIndex;

        // Check for Ron first (highest priority)
        if (Meld.canRon(humanPlayer, lastDiscard)) {
            melds.add("ron");
        }

        // Only allow Chow if last discard was from the player to the right of the human (relative to game flow)
        // In a 4-player game, if human is P0, discarder P3 (left) would allow chow for P0
        // For P0, the discarder to their right is P3.
        int humanPlayerExpectedChowDiscarderIndex = (0 + 3) % 4; // Player 3 is to the right of Player 0
        if (discarderIndex == humanPlayerExpectedChowDiscarderIndex && Meld.canChow(humanPlayer, lastDiscard)) {
            melds.add("chow");
        }

        // Allow Pong and Kong from any player if applicable
        if (Meld.canPong(humanPlayer, lastDiscard)) {
            melds.add("pong");
        }
        if (Meld.canKong(humanPlayer, lastDiscard)) {
            melds.add("kong");
        }

        if (!melds.isEmpty()) {
            showMeldButtons(melds);
        } else {
            meldPanel.setVisible(false);
        }
    }

    // --- After human player's turn, process bot turns ---
    private void processBotTurns() {
        isBotTurn = true;
        processNextBotTurn(currentPlayerIndex);
    }

    private void processNextBotTurn(int botIndex) {
        // If the round has ended (e.g., due to Ron/Tsumo), stop processing bot turns
        if (gamePhase.equals(PHASE_ROUND_END)) {
            return;
        }

        if (botIndex == 0) {
            isBotTurn = false;
            updateTurnLabel();
            return;
        }

        Player bot = players.get(botIndex);
        // Generate random delay between 1-5 seconds
        int randomDelay = (int)(Math.random() * 5) + 1;
        turnLabels[botIndex].setText(bot.getName() + "'s Turn (" + randomDelay + "s)");
        for (int i = 0; i < 4; i++) turnLabels[i].setVisible(i == botIndex);
        botCountdownSeconds = randomDelay;

        if (botCountdownTimer != null) botCountdownTimer.stop();
        botCountdownTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                botCountdownSeconds--;
                if (botCountdownSeconds > 0) {
                    turnLabels[botIndex].setText(bot.getName() + "'s Turn (" + botCountdownSeconds + "s)");
                } else {
                    botCountdownTimer.stop();
                    doBotAction(bot, botIndex);
                }
            }
        });
        botCountdownTimer.setRepeats(true);
        botCountdownTimer.start();
    }

    private void doBotAction(Player bot, int botIndex) {
        // If the round has ended (e.g., due to Ron/Tsumo), stop processing bot turns
        if (gamePhase.equals(PHASE_ROUND_END)) {
            return;
        }

        // Draw a tile
        if (!logic.drawWall.isEmpty()) {
            Piece drawn = logic.drawWall.pop();
            bot.addToHand(drawn);
        }

        // Check for win (Tsumo)
        if (checkBotWin(bot, botIndex)) {
            showGifOverlay("imgs/tsumo-mahjong.gif");
            gamePhase = PHASE_ROUND_END; // Set game phase to round end
            return; // Game ends
        }

        // Discard a tile
        Piece discard = chooseBotDiscard(bot);
        if (discard != null) {
            bot.getHand().remove(discard);
            logic.getDiscards().add(discard);
            JPanel discardPanel = switch (botIndex) {
                case 1 -> rightDiscards;
                case 2 -> topDiscards;
                case 3 -> leftDiscards;
                default -> null;
            };
            if (discardPanel != null) addToDiscard(discard, bot.getDiscards(), discardPanel);

            // After a bot discard, check for melds from all other players
            // If a meld is found, the turn will be handled by checkAllPlayersForReactions
            if (checkAllPlayersForReactions(discard, botIndex)) {
                return; // A reaction occurred, do not proceed with normal turn advancement
            }
        }

        // Move to next player and ensure we process their turn, only if no reaction occurred
        currentPlayerIndex = (botIndex + 1) % 4;
        if (currentPlayerIndex != 0) {
            processNextBotTurn(currentPlayerIndex);
        } else {
            isBotTurn = false;
            updateTurnLabel();
            // After bots are done, human draws a tile and gets ready to discard.
            Player humanPlayer = players.get(0);
            if (!logic.drawWall.isEmpty()) {
                humanPlayer.addToHand(logic.drawWall.pop());
            }
            updateDisplay(); // Refresh human hand after drawing
            gamePhase = PHASE_DISCARD; // Human's turn to discard
        }
    }

    // --- Bot discard logic: keep pairs, discard singletons ---
    private Piece chooseBotDiscard(Player bot) {
        java.util.Map<String, Integer> tileCounts = new java.util.HashMap<>();
        for (Piece p : bot.getHand()) {
            String key = p.getType() + ":" + p.getValue();
            tileCounts.put(key, tileCounts.getOrDefault(key, 0) + 1);
        }
        // Prefer to discard singletons
        for (Piece p : bot.getHand()) {
            String key = p.getType() + ":" + p.getValue();
            if (tileCounts.get(key) == 1) return p;
        }
        // Otherwise, discard any tile
        return bot.getHand().isEmpty() ? null : bot.getHand().get(0);
    }

    // --- Bot meld logic ---
    private boolean tryBotMeld(Player bot, int botIndex, Piece lastDiscard, int originalDiscarderIndex) {
        // Try Ron (win on discard)
        if (checkBotRon(bot, botIndex, lastDiscard)) {
            showGifOverlay("imgs/yakuza-ron.gif");
            currentPlayerIndex = botIndex;
            processBotTurns();
            gamePhase = PHASE_ROUND_END; // Set game phase to round end
            return true;
        }
        // Try Kong
        if (Meld.canKong(bot, lastDiscard)) {
            removePieceFromPlayerDiscardPanel(lastDiscard, originalDiscarderIndex); // Use originalDiscarderIndex
            java.util.List<Piece> meldedPieces = new java.util.ArrayList<>();
            meldedPieces.add(lastDiscard);
            // Find and remove 3 matching tiles from bot's hand
            int count = 0;
            java.util.Iterator<Piece> iterator = bot.getHand().iterator();
            while (iterator.hasNext() && count < 3) {
                Piece p = iterator.next();
                if (p.getType().equals(lastDiscard.getType()) && p.getValue().equals(lastDiscard.getValue())) {
                    meldedPieces.add(p);
                    iterator.remove();
                    count++;
                }
            }
            displayMeld(bot, meldedPieces);
            updateBotHandDisplay(botIndex); // Update bot's hand display
            bot.getMelds().add(new Meld("kong", meldedPieces)); // Record the meld
            // After Kong, bot must discard one tile
            if (!logic.drawWall.isEmpty()) {
                bot.addToHand(logic.drawWall.pop()); // Draw replacement tile
            }
            // Do NOT advance turn here. The bot needs to discard, which will be handled by botDiscardAfterMeld
            botDiscardAfterMeld(bot, botIndex); // Bot discards after Kong
            return true;
        }
        // Try Pong
        if (Meld.canPong(bot, lastDiscard)) {
            removePieceFromPlayerDiscardPanel(lastDiscard, originalDiscarderIndex); // Use originalDiscarderIndex
            java.util.List<Piece> meldedPieces = new java.util.ArrayList<>();
            meldedPieces.add(lastDiscard);
            // Find and remove 2 matching tiles from bot's hand
            int count = 0;
            java.util.Iterator<Piece> iterator = bot.getHand().iterator();
            while (iterator.hasNext() && count < 2) {
                Piece p = iterator.next();
                if (p.getType().equals(lastDiscard.getType()) && p.getValue().equals(lastDiscard.getValue())) {
                    meldedPieces.add(p);
                    iterator.remove();
                    count++;
                }
            }
            displayMeld(bot, meldedPieces);
            updateBotHandDisplay(botIndex); // Update bot's hand display
            bot.getMelds().add(new Meld("pong", meldedPieces)); // Record the meld
            // Do NOT advance turn here. The bot needs to discard, which will be handled by botDiscardAfterMeld
            botDiscardAfterMeld(bot, botIndex); // Bot discards after Pong
            return true;
        }
        // Try Chow (only for player to the left of discarder)
        // The player to the left of the discarder (relative to the board layout) is (originalDiscarderIndex + 1) % 4.
        // So, the bot's index should be (originalDiscarderIndex + 1) % 4.
        if (botIndex == (originalDiscarderIndex + 1) % 4 && Meld.canChow(bot, lastDiscard)) {
            removePieceFromPlayerDiscardPanel(lastDiscard, originalDiscarderIndex); // Use originalDiscarderIndex
            java.util.List<Piece> meldedPieces = new java.util.ArrayList<>();
            meldedPieces.add(lastDiscard);

            // Find and remove the two sequential tiles from bot's hand
            int lastDiscardValue = Integer.parseInt(lastDiscard.getValue());
            Piece piece1 = findAndRemovePiece(bot.getHand(), lastDiscard.getType(), String.valueOf(lastDiscardValue - 1));
            Piece piece2 = findAndRemovePiece(bot.getHand(), lastDiscard.getType(), String.valueOf(lastDiscardValue - 2));
            Piece piece3 = findAndRemovePiece(bot.getHand(), lastDiscardedPiece.getType(), String.valueOf(lastDiscardValue + 1));
            Piece piece4 = findAndRemovePiece(bot.getHand(), lastDiscardedPiece.getType(), String.valueOf(lastDiscardValue + 2));

            if (piece1 != null && piece2 != null) {
                meldedPieces.add(piece1);
                meldedPieces.add(piece2);
            } else if (piece1 != null && piece3 != null) {
                meldedPieces.add(piece1);
                meldedPieces.add(piece3);
            } else if (piece3 != null && piece4 != null) {
                meldedPieces.add(piece3);
                meldedPieces.add(piece4);
            }

            displayMeld(bot, meldedPieces);
            updateBotHandDisplay(botIndex); // Update bot's hand display
            bot.getMelds().add(new Meld("chow", meldedPieces)); // Record the meld
            // Do NOT advance turn here. The bot needs to discard, which will be handled by botDiscardAfterMeld
            botDiscardAfterMeld(bot, botIndex); // Bot discards after Chow
            return true;
        }
        return false;
    }

    // Helper method for bot to discard after a meld
    private void botDiscardAfterMeld(Player bot, int botIndex) {
        // Ensure the game is not in a round-ending state
        if (gamePhase.equals(PHASE_ROUND_END)) {
            return;
        }

        Piece discard = chooseBotDiscard(bot);
        if (discard != null) {
            bot.getHand().remove(discard);
            logic.getDiscards().add(discard);
            JPanel discardPanel = switch (botIndex) {
                case 1 -> rightDiscards;
                case 2 -> topDiscards;
                case 3 -> leftDiscards;
                default -> null;
            };
            if (discardPanel != null) addToDiscard(discard, bot.getDiscards(), discardPanel);
        }

        // After bot discards, check for reactions from other players for this new discard
        // The turn will advance from here if no reactions are found
        advanceTurnIfNoReactions(discard, botIndex);
    }

    // --- Add stub for checkBotRon ---
    private boolean checkBotRon(Player bot, int botIndex, Piece lastDiscard) {
        // TODO: Implement real Ron detection logic
        return false;
    }

    // --- Bot win check (Tsumo) ---
    private boolean checkBotWin(Player bot, int botIndex) {
        // TODO: Implement real win detection logic
        // For now, just return false
        return false;
    }

    // --- Ron check for all players except discarder ---
    private boolean checkRonOnDiscard(Piece discard, int discarderIndex) {
        for (int i = 0; i < 4; i++) {
            if (i == discarderIndex) continue;
            Player p = players.get(i);
            if (Meld.canRon(p, discard)) {
                showGifOverlay("imgs/yakuza-ron.gif"); // Show Ron animation
                // Handle Ron: This typically ends the round, scores, etc.
                // For now, we'll just show the GIF and prevent further turn progression
                currentPlayerIndex = i; // Set current player to the winner
                isBotTurn = (currentPlayerIndex != 0); // Update bot turn status
                updateTurnLabel();
                gamePhase = PHASE_ROUND_END; // Set game phase to round end
                return true; // Ron occurred
            }
        }
        return false; // No Ron occurred
    }

    private void updateTurnLabel() {
        // Hide all labels first
        for (int i = 0; i < 4; i++) {
            turnLabels[i].setVisible(false);
        }

        // Show only the current player's label
        String name = players.get(currentPlayerIndex).getName();
        String labelText = (currentPlayerIndex == 0) ? "Your Turn" : name + "'s Turn";
        turnLabels[currentPlayerIndex].setText(labelText);
        turnLabels[currentPlayerIndex].setVisible(true);
    }

    // Helper to update a bot's hand display after a meld or discard
    private void updateBotHandDisplay(int botIndex) {
        JPanel playerHandPanel = null;
        String side = "";
        int rotationAngle = 0;

        switch (botIndex) {
            case 1: // Right bot
                playerHandPanel = playerRightHandPanel; // Use class field directly
                side = "right";
                rotationAngle = 90;
                break;
            case 2: // Top bot
                playerHandPanel = playerTopHandPanel; // Use class field directly
                side = "top";
                rotationAngle = 0;
                break;
            case 3: // Left bot
                playerHandPanel = playerLeftHandPanel; // Use class field directly
                side = "left";
                rotationAngle = -90;
                break;
            default:
                return; // Only for bots
        }

        if (playerHandPanel != null) {
            playerHandPanel.removeAll();
            Player bot = players.get(botIndex);
            int currentHandSize = bot.getHand().size();
            Dimension tileSize = new Dimension(40, 60);

            // Re-add blank tiles based on current hand size
            for (int i = 0; i < currentHandSize; i++) {
                Tile t = new Tile(botIndex, i);
                t.setOpaque(false);
                t.setContentAreaFilled(false);
                t.setPreferredSize(tileSize);
                t.setPiece(createBlankPiece(side));
                if (rotationAngle != 0) {
                    t.setIcon(rotateIcon(t.getIcon(), rotationAngle));
                }
                playerHandPanel.add(t);
            }
            playerHandPanel.revalidate();
            playerHandPanel.repaint();
            mainPanel.revalidate(); // Revalidate the main panel
            mainPanel.repaint();    // Repaint the main panel
        }
    }

    // Helper to display a meld visually on the board
    private void displayMeld(Player meldPlayer, java.util.List<Piece> meldedPieces) {
        JPanel meldPanel = null;
        int rotationAngle = 0; // Default for top bot (no rotation)

        // Determine which bot's meld panel to use based on player name and set rotation angle
        if (meldPlayer.getName().equals("Just_Do_It_Later")) { // Top bot
            meldPanel = botTopMelds;
            rotationAngle = 0;
        } else if (meldPlayer.getName().equals("Mr. David")) { // Left bot
            meldPanel = botLeftMelds;
            rotationAngle = -90; // Rotate 90 degrees counter-clockwise for left bot
        } else if (meldPlayer.getName().equals("Mr. Biatchin")) { // Right bot
            meldPanel = botRightMelds;
            rotationAngle = 90; // Rotate 90 degrees clockwise for right bot
        } else if (meldPlayer.getName().equals("You")) { // Human player
            meldPanel = humanMeldPanel;
            rotationAngle = 0; // Human player's melds are not rotated
        }

        if (meldPanel != null) {
            meldPanel.removeAll(); // Clear existing meld display
            for (Piece p : meldedPieces) {
                ImageIcon icon = getSmallIcon(p);
                if (rotationAngle != 0) {
                    icon = rotateIcon(icon, rotationAngle);
                }
                JLabel label = new JLabel(icon);
                meldPanel.add(label);
            }
            meldPanel.revalidate();
            meldPanel.repaint();
            mainPanel.revalidate(); // Revalidate the main panel
            mainPanel.repaint();    // Repaint the main panel
        }
    }

    // Helper to visually remove a piece from a player's discard panel
    private void removePieceFromPlayerDiscardPanel(Piece pieceToRemove, int discarderIndex) {
        JPanel discardPanel = switch (discarderIndex) {
            case 0 -> bottomDiscards;
            case 1 -> rightDiscards;
            case 2 -> topDiscards;
            case 3 -> leftDiscards;
            default -> throw new IllegalStateException("Invalid player index: " + discarderIndex);
        };
        // Remove from the internal list and then update the UI
        logic.getDiscardForPlayer(discarderIndex).remove(pieceToRemove);
        updateDiscardPanel(logic.getDiscardForPlayer(discarderIndex), discardPanel);
        mainPanel.revalidate(); // Revalidate the main panel
        mainPanel.repaint();    // Repaint the main panel
    }

    // Helper to find and remove a specific piece from a list of pieces (used for Chow)
    private Piece findAndRemovePiece(java.util.List<Piece> hand, String type, String value) {
        java.util.Iterator<Piece> iterator = hand.iterator();
        while (iterator.hasNext()) {
            Piece p = iterator.next();
            if (p.getType().equals(type) && p.getValue().equals(value)) {
                iterator.remove();
                return p;
            }
        }
        return null;
    }

    // --- Centralized method to check for all possible reactions from all players ---
    private boolean checkAllPlayersForReactions(Piece lastDiscard, int discarderIndex) {
        // Prioritize Ron first (highest priority)
        if (checkRonOnDiscard(lastDiscard, discarderIndex)) {
            return true; // Ron occurred, stop further checks
        }

        // Check for other melds for all players, starting from the next player in turn order
        // The order of checking for Pon/Kong/Chow is crucial in Mahjong. Generally, Kong/Pon take precedence over Chow.
        // Here, we loop through all players, and if a meld is possible, we handle it.
        boolean meldTriggered = false; // Flag to indicate if any meld was triggered
        for (int i = 0; i < 4; i++) {
            int playerIndex = (discarderIndex + 1 + i) % 4; // Start from player after discarder
            if (playerIndex == discarderIndex) continue; // Skip the discarder themselves

            Player currentPlayer = players.get(playerIndex);

            // Check for Kong
            if (Meld.canKong(currentPlayer, lastDiscard)) {
                if (playerIndex == 0) { // Human player can Kong
                    promptHumanForMelds(lastDiscard, discarderIndex);
                    return true; // Human meld is pending
                } else { // Bot can Kong
                    if (tryBotMeld(currentPlayer, playerIndex, lastDiscard, discarderIndex)) {
                        meldTriggered = true; // Bot meld occurred
                        break; // Stop checking for other melds for this discard, bot turn continues (via botDiscardAfterMeld)
                    }
                }
            }

            // Check for Pong
            if (Meld.canPong(currentPlayer, lastDiscard)) {
                if (playerIndex == 0) { // Human player can Pong
                    promptHumanForMelds(lastDiscard, discarderIndex);
                    return true; // Human meld is pending
                } else { // Bot can Pong
                    if (tryBotMeld(currentPlayer, playerIndex, lastDiscard, discarderIndex)) {
                        meldTriggered = true; // Bot meld occurred
                        break; // Stop checking for other melds for this discard, bot turn continues (via botDiscardAfterMeld)
                    }
                }
            }

            // Check for Chow (only for the player immediately to the right of the discarder in playing order)
            if (playerIndex == (discarderIndex + 1) % 4 && Meld.canChow(currentPlayer, lastDiscard)) {
                if (playerIndex == 0) { // Human player can Chow
                    promptHumanForMelds(lastDiscard, discarderIndex);
                    return true; // Human meld is pending
                } else { // Bot can Chow
                    if (tryBotMeld(currentPlayer, playerIndex, lastDiscard, discarderIndex)) {
                        meldTriggered = true; // Bot meld occurred
                        break; // Stop checking for other melds for this discard, bot turn continues (via botDiscardAfterMeld)
                    }
                }
            }
        }
        return meldTriggered; // Return true if any meld was triggered (human or bot)
    }

    // Helper to advance turn if no reactions occurred to a discard
    private void advanceTurnIfNoReactions(Piece lastDiscardedPiece, int discarderIndex) {
        // If the round has ended, do not advance turn further.
        if (gamePhase.equals(PHASE_ROUND_END)) {
            return;
        }

        if (!checkAllPlayersForReactions(lastDiscardedPiece, discarderIndex)) {
            // If no reactions, proceed to next player's turn normally
            currentPlayerIndex = (currentPlayerIndex + 1) % 4;
            updateTurnLabel();
            if (currentPlayerIndex != 0) {
                processBotTurns();
            } else { // It's human's turn after a full cycle of no reactions
                // Human needs to draw a tile and then discard.
                Player humanPlayer = players.get(0);
                if (!logic.drawWall.isEmpty()) {
                    humanPlayer.addToHand(logic.drawWall.pop());
                }
                updateDisplay(); // Refresh human hand after drawing
                gamePhase = PHASE_DISCARD; // Human's turn to discard
            }
        }
    }
}




