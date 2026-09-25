public final class SlytherinMaster extends Master {
    private static SlytherinMaster instance;
    private static int instanceCount;

    private SlytherinMaster(Position position) {
        super(Population.SLYTHERIN, position);
        instanceCount++;
    }

    public static SlytherinMaster getInstance(Position position) {
        if (instance == null) {
            instance = new SlytherinMaster(position);
        }
        return instance;
    }

    public static int instanceCount() {
        return instanceCount;
    }
}
