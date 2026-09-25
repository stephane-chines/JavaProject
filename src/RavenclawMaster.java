public final class RavenclawMaster extends Master {
    private static RavenclawMaster instance;
    private static int instanceCount;

    private RavenclawMaster(Position position) {
        super(Population.RAVENCLAW, position);
        instanceCount++;
    }

    public static RavenclawMaster getInstance(Position position) {
        if (instance == null) {
            instance = new RavenclawMaster(position);
        }
        return instance;
    }

    public static int instanceCount() {
        return instanceCount;
    }
}
