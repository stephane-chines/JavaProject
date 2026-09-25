public enum Population {
    GRYFFINDOR("Gryffindor", Alliance.GOLDEN, 'G', 'g'),
    HUFFLEPUFF("Hufflepuff", Alliance.GOLDEN, 'H', 'h'),
    RAVENCLAW("Ravenclaw", Alliance.SILVER, 'R', 'r'),
    SLYTHERIN("Slytherin", Alliance.SILVER, 'S', 's');

    private final String displayName;
    private final Alliance alliance;
    private final char masterSymbol;
    private final char agentSymbol;

    Population(String displayName, Alliance alliance, char masterSymbol, char agentSymbol) {
        this.displayName = displayName;
        this.alliance = alliance;
        this.masterSymbol = masterSymbol;
        this.agentSymbol = agentSymbol;
    }

    public String displayName() {
        return displayName;
    }

    public Alliance alliance() {
        return alliance;
    }

    public char masterSymbol() {
        return masterSymbol;
    }

    public char agentSymbol() {
        return agentSymbol;
    }
}
