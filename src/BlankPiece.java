package src;

public class BlankPiece extends Piece {
    public BlankPiece(int row, int col, String imageFileName) {
        super();
        this.type = "Blank";
        this.value = "Hidden";
        this.row = row;
        this.col = col;
        this.imageFileName = imageFileName;
        setIcon(imageFileName);
    }

    @Override
    public void operation() {
        System.out.println("Hidden tile");
    }

    @Override
    public void operation2() {
        System.out.println("Hidden tile");
    }
} 