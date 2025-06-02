package test_scripts;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import src.Bamboo;
import src.Board;
import src.GameLogic;
import src.Piece;

import javax.swing.*;
import java.util.ArrayList;

public class CenterDiscardsPanelTest {

    private Board board;
    private GameLogic logic;
    private ArrayList<Piece> discards;

    @BeforeEach
    public void setUp() {
        board = new Board();
        discards = new ArrayList<>();
    }

    @Test
    public void testAddToDiscardUpdatesPanel() {
        Bamboo piece = new Bamboo(String.valueOf(1), 1, false, 1, 1, "bamboo_" + 1 + ".png"); // Assuming a constructor that takes an image path
        board.addToDiscard(piece, discards);
        // Assuming centerDiscards is accessible or there's a method to retrieve it
        JPanel centerDiscards = board.getCenterDiscardsPanel();
        assertEquals(1, centerDiscards.getComponentCount());
    }
}
