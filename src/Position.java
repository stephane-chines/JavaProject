import java.util.Objects;

public record Position(int x, int y) {
    public Position {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Position coordinates cannot be negative.");
        }
    }

    public Position move(Direction direction) {
        Objects.requireNonNull(direction, "direction");
        return new Position(x + direction.dx(), y + direction.dy());
    }

    public boolean isAdjacentTo(Position other) {
        int xDistance = Math.abs(x - other.x);
        int yDistance = Math.abs(y - other.y);
        return Math.max(xDistance, yDistance) == 1;
    }
}
