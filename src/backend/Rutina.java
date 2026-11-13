package backend;

import java.util.ArrayList;
import java.util.List;

// ============================================================
//  Rutina.java
//  Agrupa los ejercicios seleccionados para un cliente
// ============================================================
public class Rutina {

    private String         nombreCliente;
    private int            semana;
    private List<Ejercicio> ejercicios;

    public Rutina(String nombreCliente, int semana) {
        this.nombreCliente = nombreCliente;
        this.semana        = semana;
        this.ejercicios    = new ArrayList<>();
    }

    public void agregarEjercicio(Ejercicio e) { ejercicios.add(e); }

    public List<Ejercicio> getEjercicios()    { return ejercicios; }
    public int getTotalEjercicios()           { return ejercicios.size(); }
    public String getNombreCliente()          { return nombreCliente; }
    public int getSemana()                    { return semana; }

    public int getTiempoTotal() {
        int total = 0;
        for (Ejercicio e : ejercicios) total += e.getTiempoMinutos();
        return total;
    }

    public long contarPorTipo(String tipo) {
        long count = 0;
        for (Ejercicio e : ejercicios)
            if (e.getTipo().equalsIgnoreCase(tipo)) count++;
        return count;
    }

    public long contarPorIntensidad(NivelIntensidad nivel) {
        long count = 0;
        for (Ejercicio e : ejercicios)
            if (e.getIntensidad() == nivel) count++;
        return count;
    }
}