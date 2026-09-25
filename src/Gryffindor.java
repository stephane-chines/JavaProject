public final class Gryffindor extends GoldAllianceMember {
    private static int instanceCount;

    public Gryffindor(Position position) {
        super(Population.GRYFFINDOR, position);
        instanceCount++;
    }

    @Override
    public void move(Simulation simulation) {
        performMove(simulation, Direction.ALL, 2);
    }

    @Override
    public int combatStrength() {
        return 4;
    }

    public static int instanceCount() {
        return instanceCount;
    }
}
