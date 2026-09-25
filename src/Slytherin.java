public final class Slytherin extends SilverAllianceMember {
    private static int instanceCount;

    public Slytherin(Position position) {
        super(Population.SLYTHERIN, position);
        instanceCount++;
    }

    @Override
    public void move(Simulation simulation) {
        performMove(simulation, Direction.ALL, 3);
    }

    @Override
    public int combatStrength() {
        return 2;
    }

    public static int instanceCount() {
        return instanceCount;
    }
}
