package src;

public class Number extends Piece {
    private int numberValue;
    private boolean isEven;

    public Number(String value, int numberValue, boolean isEven, int row, int col, String imageFileName) {
        super("Numbers", value, row, col, imageFileName);
        this.numberValue = numberValue;
        this.isEven = isEven;
    }

    @Override
    public void operation() {
        System.out.println("Number value: " + numberValue);
    }

    @Override
    public void operation2() {
        System.out.println("Is even: " + isEven);
    }
}

