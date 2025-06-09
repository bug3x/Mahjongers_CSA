package src;

public class BambooPiece extends Piece {
    public BambooPiece(int value) {
        super();
        this.type = "Bamboo";
        this.value = String.valueOf(value);
        setIcon("bamboo_" + value + ".png");
    }

    @Override
    public void operation() {
        // No special operation needed for bamboo pieces
    }

    @Override
    public void operation2() {
        // No special operation needed for bamboo pieces
    }
} 
