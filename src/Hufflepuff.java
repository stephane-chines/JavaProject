public final class Hufflepuff extends GoldAllianceMember {
    private static int instanceCount;

    public Hufflepuff(Position position) {
        super(Population.HUFFLEPUFF, position);
        instanceCount++;
    }

    @Override
    public void move(Simulation simulation) {
        performMove(simulation, Direction.CARDINAL, 3);
    }

    @Override
    public int combatStrength() {
        return 3;
    }

    public static int instanceCount() {
        return instanceCount;
    }
}
