public abstract class SilverAllianceMember extends MobileIndividual {
    private static int instanceCount;

    protected SilverAllianceMember(Population population, Position position) {
        super(population, position);
        if (population.alliance() != Alliance.SILVER) {
            throw new IllegalArgumentException("A SilverAllianceMember must belong to the Silver Alliance.");
        }
        instanceCount++;
    }

    public static int instanceCount() {
        return instanceCount;
    }
}
