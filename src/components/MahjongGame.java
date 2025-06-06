package src.components;

import java.util.Timer;
import java.util.TimerTask;
import java.util.List;
import java.util.ArrayList;

public class MahjongGame {
    private int currentPlayer;
    private Player[] players;
    private Timer turnTimer;

    public MahjongGame() {
        this.currentPlayer = 0;
        this.players = new Player[4];
        for (int i = 0; i < 4; i++) {
            players[i] = new Player();
        }
        this.turnTimer = new Timer();
    }

    public void startTurn() {
        if (currentPlayer == 0) {
            // Start a 2-second timer for AI players
            turnTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handleDiscard(players[0].getHand().get(0));
                }
            }, 2000);
        }
    }

    private void handleDiscard(int tile) {
        // Implementation of handleDiscard function
        players[currentPlayer].discardTile(tile);
        currentPlayer = (currentPlayer + 1) % 4;
    }

    public static void main(String[] args) {
        MahjongGame game = new MahjongGame();
        game.startTurn();
    }
}

class Player {
    private List<Integer> hand;

    public Player() {
        this.hand = new ArrayList<>();
    }

    public List<Integer> getHand() {
        return hand;
    }

    public void discardTile(int tile) {
        hand.remove(Integer.valueOf(tile));
    }
} 