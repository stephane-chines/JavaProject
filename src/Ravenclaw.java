public final class Ravenclaw extends SilverAllianceMember {
    private static int instanceCount;

    public Ravenclaw(Position position) {
        super(Population.RAVENCLAW, position);
        instanceCount++;
    }

    @Override
    public void move(Simulation simulation) {
        performMove(simulation, Direction.DIAGONAL, 3);
    }

    @Override
    public int combatStrength() {
        return 2;
    }

    public static int instanceCount() {
        return instanceCount;
    }
}
