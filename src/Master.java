public abstract class Master extends LivingBeing {
    private static int instanceCount;

    protected Master(Population population, Position position) {
        super(population, position);
        instanceCount++;
    }

    @Override
    public final char mapSymbol() {
        return population().masterSymbol();
    }

    public final int receiveMessages(MobileIndividual messenger) {
        if (messenger.population() != population()) {
            throw new IllegalArgumentException("A Master only receives messages from its own population.");
        }
        return knowledge().addAll(messenger.knowledge().view());
    }

    public static int instanceCount() {
        return instanceCount;
    }
}
