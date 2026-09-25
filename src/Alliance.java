public enum Alliance {
    GOLDEN("Golden Alliance"),
    SILVER("Silver Alliance");

    private final String displayName;

    Alliance(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
