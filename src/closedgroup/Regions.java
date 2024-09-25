package closedgroup;

public enum Regions {
    NORTH("Norte"),
    SOUTH("Sul");

    private final String value;

    // Construtor do enum
    Regions(final String value) {
        this.value = value;
    }

    // Método para obter a descrição associada à constante enum
    public String getValue() {
        return this.value;
    }
}
