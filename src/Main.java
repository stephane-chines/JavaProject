public final class Main {
    private static final int DEFAULT_MAX_STEPS = 120;

    private Main() {
    }

    public static void main(String[] args) {
        long seed = System.currentTimeMillis();
        int maxSteps = DEFAULT_MAX_STEPS;
        long delayMillis = 0;

        try {
            for (String argument : args) {
                if (argument.startsWith("--seed=")) {
                    seed = Long.parseLong(argument.substring("--seed=".length()));
                } else if (argument.startsWith("--steps=")) {
                    maxSteps = Integer.parseInt(argument.substring("--steps=".length()));
                } else if (argument.startsWith("--delay=")) {
                    delayMillis = Long.parseLong(argument.substring("--delay=".length()));
                } else if (argument.equals("--help")) {
                    printUsage();
                    return;
                } else {
                    throw new IllegalArgumentException("Unknown option: " + argument);
                }
            }

            if (maxSteps < 1 || delayMillis < 0) {
                throw new IllegalArgumentException("Steps must be positive and delay cannot be negative.");
            }

            Simulation simulation = new Simulation(18, 12, maxSteps, delayMillis, seed);
            simulation.run();
        } catch (NumberFormatException exception) {
            System.err.println("Seed, steps, and delay must be whole numbers.");
            printUsage();
        } catch (IllegalArgumentException exception) {
            System.err.println(exception.getMessage());
            printUsage();
        }
    }

    private static void printUsage() {
        System.out.println("Usage: java Main [--seed=N] [--steps=N] [--delay=MILLISECONDS]");
    }
}
