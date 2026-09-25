public final class GryffindorMaster extends Master {
    private static GryffindorMaster instance;
    private static int instanceCount;

    private GryffindorMaster(Position position) {
        super(Population.GRYFFINDOR, position);
        instanceCount++;
    }

    public static GryffindorMaster getInstance(Position position) {
        if (instance == null) {
            instance = new GryffindorMaster(position);
        }
        return instance;
    }

    public static int instanceCount() {
        return instanceCount;
    }
}
