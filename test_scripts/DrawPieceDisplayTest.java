package test_scripts;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import src.Bamboo;
import src.Board;
import src.GameLogic;
import src.Piece;
import src.Player;

import java.util.Stack;

public class DrawPieceDisplayTest {

    private Board board;
    private Player player;
    private Stack<Piece> drawWall;
    private GameLogic logic;

    @BeforeEach
    public void setUp() {
        board = new Board();
        player = new Player("Test Player");
        drawWall = new Stack<>();
        drawWall.push(new Bamboo(String.valueOf(1), 1, false, 1, 1, "bamboo_" + 1 + ".png")); // Assuming a constructor that takes an image path

        logic.setDrawWall(drawWall); // Assuming a setter method
    }

    @Test
    public void testDisplayDrawPiece() {
        board.displayDrawPiece(player);
        System.out.println(player.getHand().size());
        assertEquals(1, player.getHand().size());
    }
}
