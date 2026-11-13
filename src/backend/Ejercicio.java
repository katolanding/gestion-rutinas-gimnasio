package backend;

// ── Clase abstracta base ─────────────────────────────────
public abstract class Ejercicio {

    private String codigo;
    private String nombre;
    private NivelIntensidad intensidad;
    private int tiempoMinutos;
    private String descripcion;
    private Integer semanaUltimoUso;

    public Ejercicio(String codigo, String nombre, NivelIntensidad intensidad,
                     int tiempoMinutos, String descripcion, Integer semanaUltimoUso) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.intensidad = intensidad;
        this.tiempoMinutos = tiempoMinutos;
        this.descripcion = descripcion;
        this.semanaUltimoUso = semanaUltimoUso;
    }

    public abstract String getTipo();

    public boolean estaDisponible(int semanaActual) {
        if (semanaUltimoUso == null) return true;
        return Math.abs(semanaActual - semanaUltimoUso) > 1;
    }

    public void marcarUso(int semana) { this.semanaUltimoUso = semana; }

    public String getCodigo()              { return codigo; }
    public String getNombre()              { return nombre; }
    public NivelIntensidad getIntensidad() { return intensidad; }
    public int getTiempoMinutos()          { return tiempoMinutos; }
    public String getDescripcion()         { return descripcion; }
    public Integer getSemanaUltimoUso()    { return semanaUltimoUso; }
}

// ── Subclase cardiovascular ──────────────────────────────
class EjercicioCardiovascular extends Ejercicio {

    public EjercicioCardiovascular(String codigo, String nombre,
                                   NivelIntensidad intensidad,
                                   int tiempoMinutos, String descripcion,
                                   Integer semanaUltimoUso) {
        super(codigo, nombre, intensidad, tiempoMinutos, descripcion, semanaUltimoUso);
    }

    @Override
    public String getTipo() { return "Cardiovascular"; }
}

// ── Subclase fuerza ──────────────────────────────────────
class EjercicioFuerza extends Ejercicio {

    public EjercicioFuerza(String codigo, String nombre,
                           NivelIntensidad intensidad,
                           int tiempoMinutos, String descripcion,
                           Integer semanaUltimoUso) {
        super(codigo, nombre, intensidad, tiempoMinutos, descripcion, semanaUltimoUso);
    }

    @Override
    public String getTipo() { return "Fuerza"; }
}