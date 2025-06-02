package src;

public class Wind extends Piece {
    private String direction; // "East", "South" etc.
    private boolean isMajorWind;

    public Wind(String value, String direction, boolean isMajorWind, int row, int col, String imageFileName) {
        super();
        this.direction = direction;
        this.isMajorWind = isMajorWind;
    }

    @Override
    public void operation() {
        System.out.println("Wind direction: " + direction);
    }

    @Override
    public void operation2() {
        System.out.println("Is major wind: " + isMajorWind);
    }
}
