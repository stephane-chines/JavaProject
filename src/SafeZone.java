public final class SafeZone {
    private final Population owner;
    private final int minimumX;
    private final int maximumX;
    private final int minimumY;
    private final int maximumY;

    public SafeZone(Population owner, int minimumX, int maximumX, int minimumY, int maximumY) {
        this.owner = owner;
        this.minimumX = minimumX;
        this.maximumX = maximumX;
        this.minimumY = minimumY;
        this.maximumY = maximumY;
    }

    public Population owner() {
        return owner;
    }

    public boolean contains(Position position) {
        return position.x() >= minimumX && position.x() <= maximumX
                && position.y() >= minimumY && position.y() <= maximumY;
    }
}
