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

    private JPanel bottomDiscards;
    private JPanel topDiscards;
    private JPanel leftDiscards;
    private JPanel rightDiscards;
    
    private GameLogic logic;
    private ArrayList<Player> players;
    private List<Tile> bottomHandTiles = new ArrayList<>();
    ArrayList<Piece> pieces = new ArrayList<>();
    private int currentPlayerIndex = 0;  // Track current player

    public Board() {
        frame = new JFrame("Mahjong");
        backgroundImage = new ImageIcon("imgs/mahjongboard1.png").getImage();
        
        //construct player list
        players = new ArrayList<>();
        players.add(new Player("Lil' John"));
        players.add(new Player("Bot 1"));
        players.add(new Player("Bot 2"));
        players.add(new Player("Bot 3"));

        logic = new GameLogic(players);
        logic.setupPlayers(players);
        
        //setup gui
        setup();
    }
    
    public void addToDiscard(Piece p, ArrayList<Piece> discard, JPanel discardPanel) {
        discard.add(p);
        updateDiscardPanel(discard, discardPanel);
    }

    private void updateDiscardPanel(ArrayList<Piece> discards, JPanel discardPanel) {
        discardPanel.removeAll();
        for (Piece p : discards) {
            JLabel label = new JLabel(p.getIcon());
            discardPanel.add(label);
        }
        discardPanel.revalidate();
        discardPanel.repaint();
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
        }
    }
    
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

        setupBoard(mainPanel, logic.drawWall, logic.deadWall);
        addMenus();

        frame.setContentPane(this);
        frame.setVisible(true);

        frame.revalidate();
        frame.repaint();
    }

    public void switchToNextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % 4;
        updateDisplay();
    }

    private void updateDisplay() {
        for (Tile tile : bottomHandTiles) {
            tile.removePiece();
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


    public void setupBoard(JPanel mainPanel, Stack<Piece> drawW, List<Piece> deadW) {
        newPanel = new JPanel(new BorderLayout());
        newPanel.setOpaque(false);
        newPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        Dimension tileSize = new Dimension(40, 60);
        int hGap = 4;
        int vGap = 2;

        bottomDiscards = createDiscardPanel(2, 6);
        topDiscards = createDiscardPanel(2, 6);
        leftDiscards = createDiscardPanel(6, 2);
        rightDiscards = createDiscardPanel(6, 2);

        JPanel playerBottom = new JPanel(new FlowLayout(FlowLayout.CENTER, hGap, 0));
        JPanel playerTop = new JPanel(new FlowLayout(FlowLayout.CENTER, hGap, 0));
        JPanel playerLeft = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, vGap));
        JPanel playerRight = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, vGap));

        playerBottom.setOpaque(false);
        playerTop.setOpaque(false);
        playerLeft.setOpaque(false);
        playerRight.setOpaque(false);

        int verticalSpace = tileSize.height * 13 + vGap * 12;
        playerLeft.setPreferredSize(new Dimension(60, verticalSpace));
        playerRight.setPreferredSize(new Dimension(60, verticalSpace));

        JPanel bottomWrapper = new JPanel(new BorderLayout());
        JPanel topWrapper = new JPanel(new BorderLayout());
        JPanel leftWrapper = new JPanel(new BorderLayout());
        JPanel rightWrapper = new JPanel(new BorderLayout());

        bottomWrapper.setOpaque(false);
        topWrapper.setOpaque(false);
        leftWrapper.setOpaque(false);
        rightWrapper.setOpaque(false);

        bottomWrapper.setPreferredSize(new Dimension(800, 120));
        topWrapper.setPreferredSize(new Dimension(800, 100));
        leftWrapper.setPreferredSize(new Dimension(60, verticalSpace + 40));
        rightWrapper.setPreferredSize(new Dimension(60, verticalSpace + 40));

        bottomWrapper.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));
        topWrapper.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        leftWrapper.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        rightWrapper.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        bottomHandTiles.clear();
        ArrayList<Piece> currentHand = players.get(currentPlayerIndex).getHand();
        sortPlayerHand(currentHand);

        for (int i = 0; i < 13; i++) {
            Tile b = new Tile(0, i);
            if (i < currentHand.size()) b.setPiece(currentHand.get(i));
            b.setOpaque(false);
            b.setContentAreaFilled(false);
            b.setPreferredSize(tileSize);
            b.addMouseListener(this);
            playerBottom.add(b);
            bottomHandTiles.add(b);

            Tile t = new Tile(1, i);
            t.setOpaque(false);
            t.setContentAreaFilled(false);
            t.setPreferredSize(tileSize);
            t.setPiece(createBlankPiece("top"));
            playerTop.add(t);

            Tile l = new Tile(i, 0);
            l.setOpaque(false);
            l.setContentAreaFilled(false);
            l.setPreferredSize(tileSize);
            l.setPiece(createBlankPiece("left"));
            if (l.getIcon() != null) l.setIcon(rotateIcon(l.getIcon(), -90));
            playerLeft.add(l);

            Tile r = new Tile(i, 1);
            r.setOpaque(false);
            r.setContentAreaFilled(false);
            r.setPreferredSize(tileSize);
            r.setPiece(createBlankPiece("right"));
            if (r.getIcon() != null) r.setIcon(rotateIcon(r.getIcon(), 90));
            playerRight.add(r);
        }

        bottomWrapper.add(playerBottom, BorderLayout.CENTER);
        bottomWrapper.add(bottomDiscards, BorderLayout.NORTH);

        topWrapper.add(playerTop, BorderLayout.CENTER);
        topWrapper.add(topDiscards, BorderLayout.SOUTH);

        leftWrapper.add(playerLeft, BorderLayout.CENTER);
        leftWrapper.add(leftDiscards, BorderLayout.EAST);

        rightWrapper.add(playerRight, BorderLayout.CENTER);
        rightWrapper.add(rightDiscards, BorderLayout.WEST);

        JPanel tableArea = new JPanel(new BorderLayout());
        tableArea.setOpaque(false);
        tableArea.add(topWrapper, BorderLayout.NORTH);
        tableArea.add(bottomWrapper, BorderLayout.SOUTH);
        tableArea.add(leftWrapper, BorderLayout.WEST);
        tableArea.add(rightWrapper, BorderLayout.EAST);

        newPanel.add(tableArea, BorderLayout.CENTER);
        mainPanel.add(newPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
    
    private JPanel createDiscardPanel(int rows, int cols) {
        JPanel panel = new JPanel(new GridLayout(rows, cols, 2, 2));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(cols * 5, rows * 10));

        return panel;
    }


    private Piece createBlankPiece(String side) {
        // Create a piece with the appropriate side image
        String sideImage;
        switch(side) {
            case "left":
                sideImage = "side4.png";  // Left side
                break;
            case "right":
                sideImage = "side2.png";  // Right side
                break;
            default:
                sideImage = "blank.png";  // Top/default
                break;
        }
        return new BlankPiece(-1, -1, sideImage);
    }

    private void sortPlayerHand(ArrayList<Piece> hand) {
        // Sort the hand based on type and value
        hand.sort((p1, p2) -> {
            // Define type order: Numbers -> Circles -> Bamboo -> Winds -> Dragons
            String[] typeOrder = {"Number", "Circle", "Bamboo", "Wind", "Dragon"};
            int type1Index = java.util.Arrays.asList(typeOrder).indexOf(p1.getType());
            int type2Index = java.util.Arrays.asList(typeOrder).indexOf(p2.getType());
            
            if (type1Index != type2Index) {
                return type1Index - type2Index;
            }
            
            // If same type, sort by value
            if (p1.getType().equals("Wind")) {
                // Sort winds: East, South, West, North
                String[] windOrder = {"East", "South", "West", "North"};
                return java.util.Arrays.asList(windOrder).indexOf(p1.getValue()) - 
                       java.util.Arrays.asList(windOrder).indexOf(p2.getValue());
            } else if (p1.getType().equals("Dragon")) {
                // Sort dragons: Red, Green, White
                String[] dragonOrder = {"Red", "Green", "White"};
                return java.util.Arrays.asList(dragonOrder).indexOf(p1.getValue()) - 
                       java.util.Arrays.asList(dragonOrder).indexOf(p2.getValue());
            } else {
                // For numbered tiles (Numbers, Circles, Bamboo)
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

    // Required methods, stubbed
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() instanceof Tile clickedTile && clickedTile.hasPiece()) {
            Piece piece = clickedTile.getPiece();
            clickedTile.removePiece();

            // Get current player and their discard list + panel
            Player currentPlayer = players.get(currentPlayerIndex);
            ArrayList<Piece> discard = currentPlayer.getDiscards();

            JPanel discardPanel = switch (currentPlayerIndex) {
                case 0 -> bottomDiscards;
                case 1 -> rightDiscards;
                case 2 -> topDiscards;
                case 3 -> leftDiscards;
                default -> throw new IllegalStateException("Invalid player index");
            };

            // Add to the appropriate discard panel
            addToDiscard(piece, discard, discardPanel);

            // Draw new tile
            if (!logic.drawWall.isEmpty()) {
                Piece newPiece = logic.drawWall.pop();
                clickedTile.setPiece(newPiece);
            }

            // Advance to next player
            switchToNextPlayer();
        }
    }
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void actionPerformed(ActionEvent e) {}
}




