import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class WorldMap {
    private static final int SAFE_ZONE_SIZE = 3;

    private final int width;
    private final int height;
    private final boolean[][] obstacles;
    private final LivingBeing[][] occupants;
    private final Map<Population, SafeZone> safeZones = new EnumMap<>(Population.class);

    public WorldMap(int width, int height) {
        if (width < SAFE_ZONE_SIZE * 2 + 3 || height < SAFE_ZONE_SIZE * 2 + 3) {
            throw new IllegalArgumentException("The map is too small for four SafeZones.");
        }
        this.width = width;
        this.height = height;
        obstacles = new boolean[height][width];
        occupants = new LivingBeing[height][width];
        createSafeZones();
    }

    private void createSafeZones() {
        safeZones.put(Population.GRYFFINDOR, new SafeZone(Population.GRYFFINDOR, 0, 2, 0, 2));
        safeZones.put(Population.HUFFLEPUFF,
                new SafeZone(Population.HUFFLEPUFF, width - 3, width - 1, 0, 2));
        safeZones.put(Population.RAVENCLAW,
                new SafeZone(Population.RAVENCLAW, 0, 2, height - 3, height - 1));
        safeZones.put(Population.SLYTHERIN,
                new SafeZone(Population.SLYTHERIN, width - 3, width - 1, height - 3, height - 1));
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public SafeZone safeZone(Population population) {
        return safeZones.get(population);
    }

    public boolean isInside(Position position) {
        return position.x() >= 0 && position.x() < width && position.y() >= 0 && position.y() < height;
    }

    public Population safeZoneOwner(Position position) {
        for (SafeZone safeZone : safeZones.values()) {
            if (safeZone.contains(position)) {
                return safeZone.owner();
            }
        }
        return null;
    }

    public boolean canEnter(Position position, Population population) {
        if (!isInside(position)) {
            return false;
        }
        Population owner = safeZoneOwner(position);
        return owner == null || owner == population;
    }

    public boolean isObstacle(Position position) {
        return isInside(position) && obstacles[position.y()][position.x()];
    }

    public LivingBeing occupantAt(Position position) {
        return isInside(position) ? occupants[position.y()][position.x()] : null;
    }

    public boolean isEmpty(Position position) {
        return isInside(position) && !isObstacle(position) && occupantAt(position) == null;
    }

    public void place(LivingBeing being) {
        if (!isEmpty(being.position())) {
            throw new IllegalStateException("Tile " + being.position() + " is not empty.");
        }
        occupants[being.position().y()][being.position().x()] = being;
    }

    public void move(LivingBeing being, Position destination) {
        if (occupantAt(being.position()) != being || !isEmpty(destination)) {
            throw new IllegalStateException("Invalid move from " + being.position() + " to " + destination + '.');
        }
        occupants[being.position().y()][being.position().x()] = null;
        occupants[destination.y()][destination.x()] = being;
        being.setPosition(destination);
    }

    /** Returns 3 positions at a corner, 5 at an edge, and 8 elsewhere. */
    public List<Position> neighborPositions(Position center) {
        List<Position> neighbors = new ArrayList<>();
        for (Direction direction : Direction.ALL) {
            int x = center.x() + direction.dx();
            int y = center.y() + direction.dy();
            if (x >= 0 && x < width && y >= 0 && y < height) {
                neighbors.add(new Position(x, y));
            }
        }
        return neighbors;
    }

    public List<Position> availableNeighbors(Position center, Population population) {
        List<Position> available = new ArrayList<>();
        for (Position position : neighborPositions(center)) {
            if (isEmpty(position) && canEnter(position, population)) {
                available.add(position);
            }
        }
        return available;
    }

    public Direction directionToSafeZone(Position from, Population population) {
        return Direction.toward(from, masterCorner(population));
    }

    public Position masterCorner(Population population) {
        return switch (population) {
            case GRYFFINDOR -> new Position(0, 0);
            case HUFFLEPUFF -> new Position(width - 1, 0);
            case RAVENCLAW -> new Position(0, height - 1);
            case SLYTHERIN -> new Position(width - 1, height - 1);
        };
    }

    public void generateObstacles(int amount, RandomGenerator random) {
        int created = 0;
        while (created < amount) {
            Position candidate = new Position(random.between(0, width - 1), random.between(0, height - 1));
            if (safeZoneOwner(candidate) == null && isEmpty(candidate)) {
                obstacles[candidate.y()][candidate.x()] = true;
                created++;
            }
        }
    }

    public String render() {
        StringBuilder output = new StringBuilder();
        output.append("   ");
        for (int x = 0; x < width; x++) {
            output.append(x % 10);
        }
        output.append(System.lineSeparator());
        for (int y = 0; y < height; y++) {
            output.append(String.format("%2d ", y));
            for (int x = 0; x < width; x++) {
                Position position = new Position(x, y);
                LivingBeing occupant = occupantAt(position);
                if (occupant != null) {
                    output.append(occupant.mapSymbol());
                } else if (isObstacle(position)) {
                    output.append('#');
                } else {
                    Population zoneOwner = safeZoneOwner(position);
                    output.append(zoneOwner == null ? '.' : ':');
                }
            }
            output.append(System.lineSeparator());
        }
        return output.toString();
    }
}
