package src;

public class CirclePiece extends Piece {
    public CirclePiece(int value) {
        super();
        this.type = "Circle";
        this.value = String.valueOf(value);
        setIcon("circle_" + value + ".png");
    }

    @Override
    public void operation() {
        // No special operation needed for circle pieces
    }

    @Override
    public void operation2() {
        // No special operation needed for circle pieces
    }
} 
