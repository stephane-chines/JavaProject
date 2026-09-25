public final class HufflepuffMaster extends Master {
    private static HufflepuffMaster instance;
    private static int instanceCount;

    private HufflepuffMaster(Position position) {
        super(Population.HUFFLEPUFF, position);
        instanceCount++;
    }

    public static HufflepuffMaster getInstance(Position position) {
        if (instance == null) {
            instance = new HufflepuffMaster(position);
        }
        return instance;
    }

    public static int instanceCount() {
        return instanceCount;
    }
}
