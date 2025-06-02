package src;

public class Bamboo extends Piece {
    private int bambooCount;
    private boolean isGreen;

    public Bamboo(String value, int bambooCount, boolean isGreen, int row, int col, String imageFileName) {
        super();
        this.bambooCount = bambooCount;
        this.isGreen = isGreen;
    }

    @Override
    public void operation() {
        System.out.println("Bamboo count: " + bambooCount);
    }

    @Override
    public void operation2() {
        System.out.println("Is green bamboo: " + isGreen);
    }
}