import java.util.List;

public enum Direction {
    NORTH(0, -1),
    NORTH_EAST(1, -1),
    EAST(1, 0),
    SOUTH_EAST(1, 1),
    SOUTH(0, 1),
    SOUTH_WEST(-1, 1),
    WEST(-1, 0),
    NORTH_WEST(-1, -1);

    public static final List<Direction> ALL = List.of(values());
    public static final List<Direction> CARDINAL = List.of(NORTH, EAST, SOUTH, WEST);
    public static final List<Direction> DIAGONAL = List.of(NORTH_EAST, SOUTH_EAST, SOUTH_WEST, NORTH_WEST);

    private final int dx;
    private final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int dx() {
        return dx;
    }

    public int dy() {
        return dy;
    }

    public static Direction toward(Position from, Position to) {
        int dx = Integer.compare(to.x(), from.x());
        int dy = Integer.compare(to.y(), from.y());
        for (Direction direction : values()) {
            if (direction.dx == dx && direction.dy == dy) {
                return direction;
            }
        }
        return NORTH;
    }
}
