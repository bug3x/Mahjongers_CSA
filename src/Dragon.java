package src;

public class Dragon extends Piece {
    private String color;      // "Red", "Green", "White"
    private boolean isPowerTile;

    public Dragon(String value, String color, boolean isPowerTile, int row, int col, String imageFileName) {
        super();
        this.type = "Dragon";
        this.value = value;
        this.color = color;
        this.isPowerTile = isPowerTile;
        this.row = row;
        this.col = col;
        this.imageFileName = imageFileName;
        setIcon(imageFileName);
    }

    @Override
    public void operation() {
        System.out.println("Dragon color: " + color);
    }

    @Override
    public void operation2() {
        System.out.println("Is power tile: " + isPowerTile);
    }
}
