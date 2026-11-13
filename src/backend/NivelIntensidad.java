package backend;

// ── Enum de niveles de intensidad ───────────────────────────
public enum NivelIntensidad {
    BASICO("Básico"),
    INTERMEDIO("Intermedio"),
    AVANZADO("Avanzado"),
    ALTO_RENDIMIENTO("Alto rendimiento");

    private final String descripcion;

    NivelIntensidad(String descripcion) {
        this.descripcion = descripcion;
    }

    public static NivelIntensidad fromString(String valor) {
        for (NivelIntensidad n : values()) {
            if (n.name().equalsIgnoreCase(valor) ||
                n.descripcion.equalsIgnoreCase(valor)) {
                return n;
            }
        }
        throw new IllegalArgumentException("Intensidad inválida: " + valor);
    }

    @Override
    public String toString() { return descripcion; }
}