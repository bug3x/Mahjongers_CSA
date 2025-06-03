package src;

public class Bamboo extends Piece {
    private boolean isGreen;

    public Bamboo(String value, int number, boolean isGreen, int row, int col, String imageFileName) {
        super();
        this.type = "Bamboo";
        this.value = value;
        this.isGreen = isGreen;
        this.row = row;
        this.col = col;
        this.imageFileName = imageFileName;
        setIcon(imageFileName);
    }

    @Override
    public void operation() {
        System.out.println("Bamboo value: " + value);
    }

    @Override
    public void operation2() {
        System.out.println("Is green: " + isGreen);
    }
}