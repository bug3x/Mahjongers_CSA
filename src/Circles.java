package src;

public class Circles extends Piece {
    private boolean isRedDot;

    public Circles(String value, int number, boolean isRedDot, int row, int col, String imageFileName) {
        super();
        this.type = "Circle";
        this.value = value;
        this.isRedDot = isRedDot;
        this.row = row;
        this.col = col;
        this.imageFileName = imageFileName;
        setIcon(imageFileName);
    }

    @Override
    public void operation() {
        System.out.println("Circle value: " + value);
    }

    @Override
    public void operation2() {
        System.out.println("Is red dot: " + isRedDot);
    }
}
