public abstract class GoldAllianceMember extends MobileIndividual {
    private static int instanceCount;

    protected GoldAllianceMember(Population population, Position position) {
        super(population, position);
        if (population.alliance() != Alliance.GOLDEN) {
            throw new IllegalArgumentException("A GoldAllianceMember must belong to the Golden Alliance.");
        }
        instanceCount++;
    }

    public static int instanceCount() {
        return instanceCount;
    }
}
