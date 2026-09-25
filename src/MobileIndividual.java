import java.util.ArrayList;
import java.util.List;

public abstract class MobileIndividual extends LivingBeing {
    public static final int MAX_ENERGY = 100;
    private static final int LOW_ENERGY_THRESHOLD = MAX_ENERGY / 5;
    private static int instanceCount;

    private int energy = MAX_ENERGY;
    private Direction lastDirection;
    private boolean lastMoveBlocked;
    private boolean active = true;

    protected MobileIndividual(Population population, Position position) {
        super(population, position);
        instanceCount++;
    }

    /** Polymorphic turn operation called by the simulation engine. */
    public abstract void move(Simulation simulation);

    protected final void performMove(Simulation simulation, List<Direction> movementDirections, int maximumDistance) {
        if (!active) {
            return;
        }

        WorldMap map = simulation.map();
        if (map.safeZone(population()).contains(position())) {
            simulation.deliverToMaster(this);
            if (energy < 60) {
                int recovered = recoverEnergy(25);
                simulation.log(name() + " rests in its SafeZone and recovers " + recovered + " EP.");
                return;
            }
        }

        Direction direction;
        if (energy < LOW_ENERGY_THRESHOLD) {
            direction = map.directionToSafeZone(position(), population());
        } else {
            List<Direction> choices = new ArrayList<>(movementDirections);
            if (lastMoveBlocked && choices.size() > 1) {
                choices.remove(lastDirection);
            }
            direction = simulation.random().choose(choices);
        }

        int plannedDistance = simulation.random().between(1, maximumDistance);
        int traveled = 0;
        int outsideSteps = 0;
        boolean blockedByTerrain = false;
        Position origin = position();

        for (int step = 1; step <= plannedDistance; step++) {
            int nextX = position().x() + direction.dx();
            int nextY = position().y() + direction.dy();
            if (nextX < 0 || nextY < 0 || nextX >= map.width() || nextY >= map.height()) {
                blockedByTerrain = true;
                break;
            }

            Position next = new Position(nextX, nextY);
            if (map.isObstacle(next) || !map.canEnter(next, population())) {
                blockedByTerrain = true;
                break;
            }

            LivingBeing encountered = map.occupantAt(next);
            if (encountered != null) {
                simulation.resolveEncounter(this, encountered);
                lastMoveBlocked = true;
                lastDirection = direction;
                spendEnergy(outsideSteps);
                simulation.log(name() + " stops at " + position() + " after meeting " + encountered.name() + '.');
                simulation.deliverToMaster(this);
                exhaustIfNecessary(simulation);
                return;
            }

            map.move(this, next);
            traveled++;
            if (!map.safeZone(population()).contains(next)) {
                outsideSteps++;
            }
            if (map.safeZone(population()).contains(next) && energy < LOW_ENERGY_THRESHOLD) {
                break;
            }
        }

        if (blockedByTerrain) {
            int remainingDistance = plannedDistance - traveled;
            outsideSteps += remainingDistance;
            simulation.log(name() + " is blocked; the " + remainingDistance + " untraveled tile(s) still cost EP.");
        }

        spendEnergy(outsideSteps);
        lastDirection = direction;
        lastMoveBlocked = blockedByTerrain;
        simulation.log(name() + " moves " + direction + " from " + origin + " to " + position()
                + " (" + energy + " EP).");

        if (map.safeZone(population()).contains(position())) {
            int recovered = recoverEnergy(20);
            if (recovered > 0) {
                simulation.log(name() + " recovers " + recovered + " EP in its SafeZone.");
            }
        }
        simulation.deliverToMaster(this);
        exhaustIfNecessary(simulation);
    }

    private void exhaustIfNecessary(Simulation simulation) {
        if (energy == 0 && active) {
            active = false;
            knowledge().clear();
            simulation.log(name() + " is exhausted, loses all messages, and becomes an obstacle.");
        }
    }

    private void spendEnergy(int amount) {
        energy = Math.max(0, energy - amount);
    }

    private int recoverEnergy(int amount) {
        int oldEnergy = energy;
        energy = Math.min(MAX_ENERGY, energy + amount);
        return energy - oldEnergy;
    }

    public final int energy() {
        return energy;
    }

    public final Direction lastDirection() {
        return lastDirection;
    }

    public final boolean isActive() {
        return active;
    }

    public static int instanceCount() {
        return instanceCount;
    }

    public abstract int combatStrength();

    @Override
    public final char mapSymbol() {
        return active ? population().agentSymbol() : 'X';
    }
}
