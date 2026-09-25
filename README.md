# Hogwarts: The Battle for Lost Spells

This Java project implements the required (non-bonus) version of the practical lab in the Harry Potter universe. Gryffindor and Hufflepuff form the Golden Alliance, while Ravenclaw and Slytherin form the Silver Alliance. Each Hogwarts house has four mobile students and one stationary Singleton House Master. The messages collected during the simulation are sixteen distinct spells.

## Run

From the project root:

```text
javac -d out src/*.java
java -cp out Main
```

Optional arguments make a run reproducible or slow the console display:

```text
java -cp out Main --seed=42 --steps=120 --delay=200
```

- `--seed`: pseudo-random seed; the current time is used when omitted.
- `--steps`: maximum number of simulation steps; default is 120.
- `--delay`: pause in milliseconds after each displayed step; default is 0.

## Implemented rules

- An 18 x 12 map with one 3 x 3 Common Room/SafeZone in each corner and one student or obstacle per tile.
- A freshly shuffled turn order at each constant-duration simulation step.
- A polymorphic `move()` implementation for each of the four populations.
- Random movement while EP is at least 20%, movement toward the home SafeZone below 20%, recovery in the SafeZone, and exhaustion at 0 EP.
- Same-population knowledge union, limited allied exchange, and opposing-alliance confrontation with message transfer.
- Delivery to the population's Master from an adjacent tile.
- Unique messages through `MessageStore`, so a Master's score always counts distinct messages.
- Four concrete Singleton House Master classes and static instance counters.
- A dedicated `RandomGenerator` used by every random operation.
- ASCII map, agent state, encounter log, Master scores, stopping condition, and draw handling.

The items under “To go further (Bonus)” are intentionally absent. A six-page report source with the required UML, state-machine, and activity diagrams is in [docs/REPORT_SOURCE.md](docs/REPORT_SOURCE.md). Replace its team placeholders before exporting it to PDF.
