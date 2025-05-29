package src;

public class Dragon extends Piece {
    private String color;      // "Red", "Green", "White"
    private boolean isPowerTile;

    public Dragon(String value, String color, boolean isPowerTile, int row, int col, String imageFileName) {
        super("Dragons", value, row, col, imageFileName);
        this.color = color;
        this.isPowerTile = isPowerTile;
    }

    @Override
    public void operation() {
        System.out.println("Dragon's color: " + color);
    }

    @Override
    public void operation2() {
        System.out.println("Is power tile: " + isPowerTile);
    }
}
