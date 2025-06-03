package src;

public class Number extends Piece {
    private boolean isEven;

    public Number(String value, int number, boolean isEven, int row, int col, String imageFileName) {
        super();
        this.type = "Number";
        this.value = value;
        this.isEven = isEven;
        this.row = row;
        this.col = col;
        this.imageFileName = imageFileName;
        setIcon(imageFileName);
    }

    @Override
    public void operation() {
        System.out.println("Number value: " + value);
    }

    @Override
    public void operation2() {
        System.out.println("Is even: " + isEven);
    }
}

