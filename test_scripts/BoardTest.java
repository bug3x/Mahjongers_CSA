package test_scripts;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import src.GameLogic;
import src.Piece;
import src.Player;
import src.Tile;

public class BoardTest {
	private JFrame frame;
    private Image backgroundImage;
    private JPanel mainPanel;
    private JPanel newPanel;
    private JPanel centerDiscards;
    
    private GameLogic logic;
    private ArrayList<Player> players;
    private List<Tile> bottomHandTiles = new ArrayList<>();

    
    public BoardTest() {
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
    
	public void setup() {
        // Setup frame
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);
        frame.setResizable(false);
        frame.getContentPane().setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Build the visual board
        setupBoard(mainPanel, logic.drawWall, logic.deadWall);
    }



    public void setupBoard(JPanel mainPanel, Stack<Piece> drawW, List<Piece> deadW) {
        newPanel = new JPanel(new BorderLayout());
        newPanel.setOpaque(false);
        newPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        Dimension tileSize1 = new Dimension(40, 60); // directly affects the scale of the top and bottom tiles
        Dimension tileSize2 = new Dimension(120, 60);
        int hGap = 0;
        int vGap = 0;

     // === CENTER DISCARD GRID ===
        int rows = 4, cols = 4;
        int spacing = 2;
        Dimension centerTileSize = new Dimension(30, 45); // smaller size

        JPanel centerDiscards = new JPanel(new GridLayout(rows, cols, spacing, spacing));
        centerDiscards.setOpaque(false);

        // Set exact preferred size: (tile width + gap) * cols, (tile height + gap) * rows
        int gridW = centerTileSize.width * cols + spacing * (cols - 1);
        int gridH = centerTileSize.height * rows + spacing * (rows - 1);
        centerDiscards.setPreferredSize(new Dimension(gridW, gridH));

        // Ensure layout respects size
        centerDiscards.setMaximumSize(new Dimension(gridW, gridH));
        centerDiscards.setMinimumSize(new Dimension(gridW, gridH));
        centerDiscards.setSize(new Dimension(gridW, gridH));

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
    }
    
    public static void main() {
    	new BoardTest();
    }
}
