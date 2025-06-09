package src;

public class DragonPiece extends Piece {
    public DragonPiece(String dragon) {
        super();
        this.type = "Dragon";
        this.value = dragon;
        String fileName = switch (dragon) {
            case "Red" -> "zhong.png";
            case "Green" -> "fa.png";
            case "White" -> "blank.png";
            default -> "blank.png";
        };
        setIcon(fileName);
    }

    @Override
    public void operation() {
        // No special operation needed for dragon pieces
    }

    @Override
    public void operation2() {
        // No special operation needed for dragon pieces
    }
} 
