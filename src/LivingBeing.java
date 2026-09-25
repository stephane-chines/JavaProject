import java.util.concurrent.atomic.AtomicInteger;

public abstract class LivingBeing {
    private static final AtomicInteger NEXT_ID = new AtomicInteger(1);
    private static int instanceCount;

    private final int id;
    private final Population population;
    private final MessageStore knowledge = new MessageStore();
    private Position position;

    protected LivingBeing(Population population, Position position) {
        this.id = NEXT_ID.getAndIncrement();
        this.population = population;
        this.position = position;
        instanceCount++;
    }

    public final int id() {
        return id;
    }

    public final String name() {
        return population.name() + '-' + id;
    }

    public final Population population() {
        return population;
    }

    public final Alliance alliance() {
        return population.alliance();
    }

    public final Position position() {
        return position;
    }

    final void setPosition(Position position) {
        this.position = position;
    }

    public final MessageStore knowledge() {
        return knowledge;
    }

    public static int instanceCount() {
        return instanceCount;
    }

    public abstract char mapSymbol();
}
