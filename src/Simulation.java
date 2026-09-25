import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class Simulation {
    private static final int AGENTS_PER_POPULATION = 4;
    private static final List<String> INITIAL_MESSAGES = List.of(
            "Accio", "Aguamenti", "Alohomora", "Confundo",
            "Expecto Patronum", "Expelliarmus", "Finite Incantatem", "Incendio",
            "Lumos", "Nox", "Petrificus Totalus", "Protego",
            "Reparo", "Riddikulus", "Stupefy", "Wingardium Leviosa"
    );
    private static final int MESSAGE_COUNT = INITIAL_MESSAGES.size();

    private final WorldMap map;
    private final RandomGenerator random;
    private final int maximumSteps;
    private final long delayMillis;
    private final List<MobileIndividual> agents = new ArrayList<>();
    private final Map<Population, Master> masters = new EnumMap<>(Population.class);
    private final List<String> events = new ArrayList<>();

    public Simulation(int width, int height, int maximumSteps, long delayMillis, long seed) {
        this.map = new WorldMap(width, height);
        this.random = new RandomGenerator(seed);
        this.maximumSteps = maximumSteps;
        this.delayMillis = delayMillis;
        initialize();
    }

    private void initialize() {
        createMasters();
        createPopulation(Population.GRYFFINDOR, Gryffindor::new);
        createPopulation(Population.HUFFLEPUFF, Hufflepuff::new);
        createPopulation(Population.RAVENCLAW, Ravenclaw::new);
        createPopulation(Population.SLYTHERIN, Slytherin::new);

        List<String> initialMessages = new ArrayList<>(INITIAL_MESSAGES);
        random.shuffle(initialMessages);
        for (int index = 0; index < agents.size(); index++) {
            agents.get(index).knowledge().add(initialMessages.get(index));
        }

        map.generateObstacles((map.width() * map.height()) / 16, random);
    }

    private void createMasters() {
        masters.put(Population.GRYFFINDOR,
                GryffindorMaster.getInstance(map.masterCorner(Population.GRYFFINDOR)));
        masters.put(Population.HUFFLEPUFF,
                HufflepuffMaster.getInstance(map.masterCorner(Population.HUFFLEPUFF)));
        masters.put(Population.RAVENCLAW,
                RavenclawMaster.getInstance(map.masterCorner(Population.RAVENCLAW)));
        masters.put(Population.SLYTHERIN,
                SlytherinMaster.getInstance(map.masterCorner(Population.SLYTHERIN)));
        for (Master master : masters.values()) {
            map.place(master);
        }
    }

    private void createPopulation(Population population, Function<Position, MobileIndividual> factory) {
        List<Position> startingTiles = new ArrayList<>();
        SafeZone zone = map.safeZone(population);
        for (int y = 0; y < map.height(); y++) {
            for (int x = 0; x < map.width(); x++) {
                Position position = new Position(x, y);
                if (zone.contains(position) && map.isEmpty(position)) {
                    startingTiles.add(position);
                }
            }
        }
        random.shuffle(startingTiles);
        for (int index = 0; index < AGENTS_PER_POPULATION; index++) {
            MobileIndividual agent = factory.apply(startingTiles.get(index));
            agents.add(agent);
            map.place(agent);
        }
    }

    public void run() {
        System.out.println("Hogwarts: The Battle for Lost Spells");
        System.out.printf("Seed: %d | Map: %dx%d | Step limit: %d | Messages: %d%n",
                random.seed(), map.width(), map.height(), maximumSteps, MESSAGE_COUNT);
        printInstanceCounts();
        printState(0);

        int completedSteps = 0;
        for (int step = 1; step <= maximumSteps && !hasCompleteMaster(); step++) {
            completedSteps = step;
            events.clear();
            List<MobileIndividual> turnOrder = new ArrayList<>();
            for (MobileIndividual agent : agents) {
                if (agent.isActive()) {
                    turnOrder.add(agent);
                }
            }
            random.shuffle(turnOrder);
            for (MobileIndividual agent : turnOrder) {
                if (agent.isActive()) {
                    agent.move(this);
                }
                if (hasCompleteMaster()) {
                    break;
                }
            }
            printState(step);
            pause();
        }

        printResult(completedSteps);
    }

    public void resolveEncounter(MobileIndividual mover, LivingBeing encountered) {
        if (encountered instanceof Master master) {
            if (master.population() == mover.population()) {
                deliverToMaster(mover);
            }
            return;
        }

        MobileIndividual other = (MobileIndividual) encountered;
        if (!other.isActive()) {
            log(mover.name() + " is blocked by exhausted " + other.name() + '.');
            return;
        }

        if (mover.population() == other.population()) {
            List<String> union = new ArrayList<>(mover.knowledge().view());
            union.addAll(other.knowledge().view());
            int moverLearned = mover.knowledge().addAll(union);
            int otherLearned = other.knowledge().addAll(union);
            log(mover.name() + " and " + other.name() + " unite their knowledge (+"
                    + moverLearned + "/+" + otherLearned + ").");
        } else if (mover.alliance() == other.alliance()) {
            int firstLearned = copyRandomMessages(other, mover, random.between(1, 2));
            int secondLearned = copyRandomMessages(mover, other, random.between(1, 2));
            log(mover.name() + " and ally " + other.name() + " exchange messages (+"
                    + firstLearned + "/+" + secondLearned + ").");
        } else if (map.safeZoneOwner(mover.position()) == null && map.safeZoneOwner(other.position()) == null) {
            confront(mover, other);
        }
    }

    private void confront(MobileIndividual first, MobileIndividual second) {
        int firstScore = first.combatStrength() + random.between(1, 6);
        int secondScore = second.combatStrength() + random.between(1, 6);
        MobileIndividual winner;
        MobileIndividual loser;
        if (firstScore == secondScore) {
            winner = random.choose(List.of(first, second));
            loser = winner == first ? second : first;
        } else {
            winner = firstScore > secondScore ? first : second;
            loser = winner == first ? second : first;
        }

        int transferred = transferRandomMessages(loser, winner, random.between(1, 3));
        log(first.name() + " confronts " + second.name() + ": " + winner.name()
                + " wins and takes " + transferred + " message(s).");
    }

    private int copyRandomMessages(LivingBeing source, LivingBeing target, int maximum) {
        List<String> candidates = source.knowledge().unknownTo(target.knowledge());
        random.shuffle(candidates);
        int amount = Math.min(maximum, candidates.size());
        for (int index = 0; index < amount; index++) {
            target.knowledge().add(candidates.get(index));
        }
        return amount;
    }

    private int transferRandomMessages(LivingBeing source, LivingBeing target, int maximum) {
        List<String> candidates = source.knowledge().unknownTo(target.knowledge());
        random.shuffle(candidates);
        int amount = Math.min(maximum, candidates.size());
        for (int index = 0; index < amount; index++) {
            String message = candidates.get(index);
            source.knowledge().remove(message);
            target.knowledge().add(message);
        }
        return amount;
    }

    public void deliverToMaster(MobileIndividual agent) {
        Master master = masters.get(agent.population());
        if (agent.position().equals(master.position()) || agent.position().isAdjacentTo(master.position())) {
            int received = master.receiveMessages(agent);
            if (received > 0) {
                log(agent.name() + " delivers " + received + " new message(s) to " + master.name() + '.');
            }
        }
    }

    public void log(String event) {
        events.add(event);
    }

    private boolean hasCompleteMaster() {
        for (Master master : masters.values()) {
            if (master.knowledge().size() == MESSAGE_COUNT) {
                return true;
            }
        }
        return false;
    }

    private void printState(int step) {
        System.out.println();
        System.out.println("=== STEP " + step + " ===");
        System.out.print(map.render());
        System.out.println("Legend: G/H/R/S = House Masters, g/h/r/s = students, : = Common Room,"
                + " X = exhausted, # = obstacle");
        System.out.print("Master scores: ");
        for (Population population : Population.values()) {
            System.out.printf("%s %d/%d  ", population.displayName(),
                    masters.get(population).knowledge().size(), MESSAGE_COUNT);
        }
        System.out.println();
        for (Population population : Population.values()) {
            System.out.print(population.displayName() + " agents: ");
            for (MobileIndividual agent : agents) {
                if (agent.population() == population) {
                    System.out.printf("%s[EP=%d,K=%d,%s] ", agent.name(), agent.energy(),
                            agent.knowledge().size(), agent.isActive() ? "active" : "exhausted");
                }
            }
            System.out.println();
        }
        if (!events.isEmpty()) {
            System.out.println("Events:");
            for (String event : events) {
                System.out.println(" - " + event);
            }
        }
    }

    private void printResult(int completedSteps) {
        int highestScore = -1;
        List<Population> winners = new ArrayList<>();
        for (Population population : Population.values()) {
            int score = masters.get(population).knowledge().size();
            if (score > highestScore) {
                highestScore = score;
                winners.clear();
                winners.add(population);
            } else if (score == highestScore) {
                winners.add(population);
            }
        }

        System.out.println();
        System.out.println("=== SIMULATION FINISHED AFTER " + completedSteps + " STEP(S) ===");
        if (winners.size() == 1) {
            Population winner = winners.get(0);
            System.out.println(winner.displayName() + " win with " + highestScore + '/' + MESSAGE_COUNT
                    + " distinct messages in their Master.");
        } else {
            String names = winners.stream().map(Population::displayName).reduce((a, b) -> a + ", " + b).orElse("");
            System.out.println("Draw between " + names + " with " + highestScore + '/' + MESSAGE_COUNT + " messages.");
        }
    }

    private void printInstanceCounts() {
        System.out.printf("Instances: LivingBeing=%d, Master=%d, MobileIndividual=%d, "
                        + "GoldAllianceMember=%d, SilverAllianceMember=%d, "
                        + "Gryffindor=%d, Hufflepuff=%d, Ravenclaw=%d, Slytherin=%d%n",
                LivingBeing.instanceCount(), Master.instanceCount(), MobileIndividual.instanceCount(),
                GoldAllianceMember.instanceCount(), SilverAllianceMember.instanceCount(),
                Gryffindor.instanceCount(), Hufflepuff.instanceCount(), Ravenclaw.instanceCount(),
                Slytherin.instanceCount());
        System.out.printf("Singleton House Masters: Gryffindor=%d, Hufflepuff=%d, Ravenclaw=%d, Slytherin=%d%n",
                GryffindorMaster.instanceCount(), HufflepuffMaster.instanceCount(),
                RavenclawMaster.instanceCount(), SlytherinMaster.instanceCount());
    }

    private void pause() {
        if (delayMillis == 0) {
            return;
        }
        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    public WorldMap map() {
        return map;
    }

    public RandomGenerator random() {
        return random;
    }
}
