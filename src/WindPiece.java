package src;

public class WindPiece extends Piece {
    public WindPiece(String wind) {
        super();
        this.type = "Wind";
        this.value = wind;
        String fileName = switch (wind) {
            case "East" -> "dong.png";
            case "South" -> "nan.png";
            case "West" -> "xi.png";
            case "North" -> "bei.png";
            default -> "blank.png";
        };
        setIcon(fileName);
    }

    @Override
    public void operation() {
        // No special operation needed for wind pieces
    }

    @Override
    public void operation2() {
        // No special operation needed for wind pieces
    }
} 
