package src;

public class Circles extends Piece {
    private int circleCount;
    private boolean isRedDot;

    public Circles(String value, int circleCount, boolean isRedDot, int row, int col, String imageFileName) {
        super("Circles", value, row, col, imageFileName);
        this.circleCount = circleCount;
        this.isRedDot = isRedDot;
    }

    @Override
    public void operation() {
        System.out.println("Circle count: " + circleCount);
    }

    @Override
    public void operation2() {
        System.out.println("Is red dot: " + isRedDot);
    }
}
