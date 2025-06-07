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
        addMenus();  // DEBUG: This method is not defined — consider commenting out

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
        for (Tile tile : bottomHandTiles) {
            tile.removePiece();
            tile.setEnabled(currentPlayerIndex == 0); // Only enable if it's the human's turn
        }
        Player currentPlayer = players.get(currentPlayerIndex);
        ArrayList<Piece> currentHand = currentPlayer.getHand();
        sortPlayerHand(currentHand);
        for (int i = 0; i < bottomHandTiles.size() && i < currentHand.size(); i++) {
            bottomHandTiles.get(i).setPiece(currentHand.get(i));
        }
        mainPanel.revalidate();
        mainPanel.repaint();
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

        // Create player hand areas
        JPanel playerBottom = new JPanel(new FlowLayout(FlowLayout.CENTER, hGap, 0));
        JPanel playerTop = new JPanel(new FlowLayout(FlowLayout.CENTER, hGap, 0));
        JPanel playerLeft = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, vGap));
        JPanel playerRight = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, vGap));

        for (JPanel panel : new JPanel[]{playerBottom, playerTop, playerLeft, playerRight})
            panel.setOpaque(false);

        int verticalSpace = tileSize.height * 13 + vGap * 12;
        playerLeft.setPreferredSize(new Dimension(60, verticalSpace));
        playerRight.setPreferredSize(new Dimension(60, verticalSpace));

        // Wrappers for positioning
        JPanel bottomWrapper = new JPanel(new BorderLayout());
        JPanel topWrapper = new JPanel(new BorderLayout());
        JPanel leftWrapper = new JPanel(new BorderLayout());
        JPanel rightWrapper = new JPanel(new BorderLayout());

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
            playerBottom.add(b);
            bottomHandTiles.add(b);

            Tile t = new Tile(1, i);  // Top bot
            t.setOpaque(false);
            t.setContentAreaFilled(false);
            t.setPreferredSize(tileSize);
            t.setPiece(createBlankPiece("top"));
            playerTop.add(t);

            Tile l = new Tile(i, 0);  // Left bot
            l.setOpaque(false);
            l.setContentAreaFilled(false);
            l.setPreferredSize(tileSize);
            l.setPiece(createBlankPiece("left"));
            if (l.getIcon() != null) l.setIcon(rotateIcon(l.getIcon(), -90));
            playerLeft.add(l);

            Tile r = new Tile(i, 1);  // Right bot
            r.setOpaque(false);
            r.setContentAreaFilled(false);
            r.setPreferredSize(tileSize);
            r.setPiece(createBlankPiece("right"));
            if (r.getIcon() != null) r.setIcon(rotateIcon(r.getIcon(), 90));
            playerRight.add(r);
        }

        // Assemble wrappers with hands and discards
        bottomWrapper.add(playerBottom, BorderLayout.CENTER);
        bottomWrapper.add(bottomDiscardFlow, BorderLayout.NORTH);

        topWrapper.add(playerTop, BorderLayout.CENTER);
        topWrapper.add(topDiscardFlow, BorderLayout.SOUTH);

        leftWrapper.add(playerLeft, BorderLayout.CENTER);
        leftWrapper.add(leftDiscards, BorderLayout.EAST);

        rightWrapper.add(playerRight, BorderLayout.CENTER);
        rightWrapper.add(rightDiscards, BorderLayout.WEST);

        // Optional: Central discard zone
        JPanel centralDiscards = new JPanel(new BorderLayout());
        centralDiscards.setOpaque(false);
        centralDiscards.add(topDiscards, BorderLayout.NORTH);
        centralDiscards.add(bottomDiscards, BorderLayout.SOUTH);
        centralDiscards.add(leftDiscards, BorderLayout.WEST);
        centralDiscards.add(rightDiscards, BorderLayout.EAST);

        JPanel tableArea = new JPanel(new BorderLayout());
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

		menuItem = new JMenuItem("Rulebook");
		menuItem.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
        		showRulebookDialog();
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

	private void showRulebookDialog() {
  	  JTextArea textArea = new JTextArea(20, 40);
    		textArea.setText("""
        		--- Mahjong Game Rulebook ---

       			 1. Each player starts with 13 tiles.
       			 2. Players take turns drawing and discarding tiles.
       			 3. Goal: form four melds and a pair.
       			 4. Melds: Chow (sequence), Pong (three of a kind), Kong (four of a kind).
       			 5. Special declarations: Riichi, Tsumo, Ron.

       			 [Insert your own house rules or additional explanations here.]

       			 Note: This rulebook is editable by modifying the textArea.setText() content.
       			 """);
    			textArea.setEditable(false);
    			textArea.setLineWrap(true);
    			textArea.setWrapStyleWord(true);

   			 JScrollPane scrollPane = new JScrollPane(textArea);
    			JOptionPane.showMessageDialog(frame, scrollPane, "Game Rulebook", JOptionPane.INFORMATION_MESSAGE);
		}

    public static void main(String[] args) {
        new Board();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (currentPlayerIndex != 0) return; // Only allow if it's the human's turn
        if (!(e.getSource() instanceof Tile clickedTile)) return;
        if (!clickedTile.hasPiece()) return;

        Piece piece = clickedTile.getPiece();
        clickedTile.removePiece();

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
        checkMeldsAfterDiscard(piece);

        // Draw a new tile
        if (!logic.drawWall.isEmpty()) {
            Piece newPiece = logic.drawWall.pop();
            currentPlayer.addToHand(newPiece);
            clickedTile.setPiece(newPiece);
        }

        // Move to next player's turn
        currentPlayerIndex = (currentPlayerIndex + 1) % 4;
        updateTurnLabel();

        // If next player is a bot, start bot turns
        if (currentPlayerIndex != 0) {
            processBotTurns();
        }
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
        });
        meldTimer.setRepeats(false);
        meldTimer.start();
    }

    // --- Helper to perform meld action ---
    private void performMeld(String meld) {
        // TODO: Implement meld logic (call GameLogic methods, update hand/discards, etc.)
        // For now, just show GIF overlay for riichi, tsumo, ron
        if (meld.equals("riichi")) {
            showGifOverlay("imgs/mahjong-all-day-riichi.gif");
        } else if (meld.equals("tsumo")) {
            showGifOverlay("imgs/tsumo-mahjong.gif");
        } else if (meld.equals("ron")) {
            showGifOverlay("imgs/yakuza-ron.gif");
        }
        // Hide meld panel after action
        meldPanel.setVisible(false);
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

    // --- After every discard, check for melds for player 0 ---
    private void checkMeldsAfterDiscard(Piece lastDiscard) {
        java.util.List<String> melds = new java.util.ArrayList<>();
        // Only for player 0 (bottom)
        Player player = players.get(0);
        int playerIndex = 0;
        // Find who discarded last
        int lastDiscarder = (currentPlayerIndex + 3) % 4;
        // Only allow Chow if last discard was from player 3 (right of player 0)
        if (Meld.canChow(player, lastDiscard) && lastDiscarder == 3) melds.add("chow");
        if (Meld.canPong(player, lastDiscard)) melds.add("pong");
        if (Meld.canKong(player, lastDiscard)) melds.add("kong");
        if (Meld.canRiichi(player)) melds.add("riichi");
        // TODO: Add tsumo/ron logic if needed
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
        if (botIndex == 0) {
            isBotTurn = false;
            updateTurnLabel();
            return;
        }

        Player bot = players.get(botIndex);
        turnLabels[botIndex].setText(bot.getName() + "'s Turn (5s)");
        for (int i = 0; i < 4; i++) turnLabels[i].setVisible(i == botIndex);
        botCountdownSeconds = 5;

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
        // Draw a tile if needed
        if (bot.getHand().size() < 14 && !logic.drawWall.isEmpty()) {
            Piece drawn = logic.drawWall.pop();
            bot.addToHand(drawn);
        }

        // Check for win (Tsumo)
        if (checkBotWin(bot, botIndex)) {
            showGifOverlay("imgs/tsumo-mahjong.gif");
            return;
        }

        // Check for melds on last discard
        Piece lastDiscard = logic.getDiscards().isEmpty() ? null : logic.getDiscards().get(logic.getDiscards().size() - 1);
        if (lastDiscard != null) {
            if (tryBotMeld(bot, botIndex, lastDiscard)) {
                // If meld performed, continue with next bot
                currentPlayerIndex = (botIndex + 1) % 4;
                processNextBotTurn(currentPlayerIndex);
                return;
            }
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
            checkRonOnDiscard(discard, botIndex);
        }

        // Move to next player
        currentPlayerIndex = (botIndex + 1) % 4;
        processNextBotTurn(currentPlayerIndex);
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
    private boolean tryBotMeld(Player bot, int botIndex, Piece lastDiscard) {
        // Try Ron (win on discard)
        if (checkBotRon(bot, botIndex, lastDiscard)) {
            showGifOverlay("imgs/yakuza-ron.gif");
            currentPlayerIndex = botIndex;
            processBotTurns();
            return true;
        }
        // Try Kong
        if (Meld.canKong(bot, lastDiscard)) {
            // TODO: Implement Kong meld logic for bot
            currentPlayerIndex = botIndex;
            processBotTurns();
            return true;
        }
        // Try Pong
        if (Meld.canPong(bot, lastDiscard)) {
            // TODO: Implement Pong meld logic for bot
            currentPlayerIndex = botIndex;
            processBotTurns();
            return true;
        }
        // Try Chow (only for player to the left of discarder)
        int discarder = (botIndex + 3) % 4;
        if (Meld.canChow(bot, lastDiscard) && discarder == ((botIndex + 3) % 4)) {
            // TODO: Implement Chow meld logic for bot
            currentPlayerIndex = botIndex;
            processBotTurns();
            return true;
        }
        return false;
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
    private void checkRonOnDiscard(Piece discard, int discarderIndex) {
        for (int i = 0; i < 4; i++) {
            if (i == discarderIndex) continue;
            Player p = players.get(i);
            // TODO: Implement Ron check for each player
        }
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
}




