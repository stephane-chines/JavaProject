import java.util.Collections;
import java.util.List;
import java.util.Random;

/** The only class that produces pseudo-random values for the simulation. */
public final class RandomGenerator {
    private final long seed;
    private final Random random;

    public RandomGenerator(long seed) {
        this.seed = seed;
        this.random = new Random(seed);
    }

    public long seed() {
        return seed;
    }

    public int between(int minimumInclusive, int maximumInclusive) {
        if (minimumInclusive > maximumInclusive) {
            throw new IllegalArgumentException("Invalid random range.");
        }
        return random.nextInt(maximumInclusive - minimumInclusive + 1) + minimumInclusive;
    }

    public <T> T choose(List<T> values) {
        if (values.isEmpty()) {
            throw new IllegalArgumentException("Cannot choose from an empty list.");
        }
        return values.get(random.nextInt(values.size()));
    }

    public <T> void shuffle(List<T> values) {
        Collections.shuffle(values, random);
    }
}
