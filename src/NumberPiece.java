package src;

public class NumberPiece extends Piece {
    public NumberPiece(int value) {
        super();
        this.type = "Number";
        this.value = String.valueOf(value);
        setIcon(value + "_wan.png");
    }

    @Override
    public void operation() {
        // No special operation needed for number pieces
    }

    @Override
    public void operation2() {
        // No special operation needed for number pieces
    }
} 
