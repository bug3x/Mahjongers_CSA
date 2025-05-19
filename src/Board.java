package src;

import java.util.ArrayList;
import java.util.List;

public class Board {
	private List<Piece> discardPile;
    private List<Meld> revealedMelds;

    public void addToDiscard(Piece p) {
        discardPile.add(p);
    }

    public Piece getLastDiscard() {
        return discardPile.get(discardPile.size() - 1);
    }

    public void removeLastDiscard() {
        if (!discardPile.isEmpty()) discardPile.remove(discardPile.size() - 1);
    }
}
