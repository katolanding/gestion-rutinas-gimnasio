package backend;
 
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
 
public class GestorEjercicios {
 
    // Singleton
    private static GestorEjercicios instancia;
 
    public static GestorEjercicios getInstance() {
        if (instancia == null) instancia = new GestorEjercicios();
        return instancia;
    }
 
    private GestorEjercicios() {}
 
    // Estado
    private List<Ejercicio> ejercicios = new ArrayList<>();
    private Rutina          rutinaActual;
    private int             semanaActual = 1;
 
    // Observer simple
    public interface Listener {
        void onEjerciciosCargados(int total, int cardio, int fuerza,
                                  int basico, int intermedio, int avanzado, int altoRend,
                                  int tiempoTotal);
        void onRutinaGenerada(Rutina rutina);
        void onError(String mensaje);
    }
 
    private final List<Listener> listeners = new ArrayList<>();
    public void agregarListener(Listener l)  { listeners.add(l); }
    public void quitarListener(Listener l)   { listeners.remove(l); }
 
    private void notificarCarga() {
        int total = ejercicios.size(), cardio = 0, fuerza = 0, tiempo = 0;
        int basico = 0, intermedio = 0, avanzado = 0, altoRend = 0;
        for (Ejercicio e : ejercicios) {
            tiempo += e.getTiempoMinutos();
            if (e.getTipo().equals("Cardiovascular")) cardio++; else fuerza++;
            switch (e.getIntensidad()) {
                case BASICO:           basico++;     break;
                case INTERMEDIO:       intermedio++; break;
                case AVANZADO:         avanzado++;   break;
                case ALTO_RENDIMIENTO: altoRend++;   break;
            }
        }
        for (Listener l : listeners)
            l.onEjerciciosCargados(total, cardio, fuerza, basico, intermedio, avanzado, altoRend, tiempo);
    }
 
    // Carga desde CSV
    public void cargarDesdeCSV(String ruta) {
        ejercicios.clear();
        File archivo = new File(ruta);
        if (!archivo.exists()) {
            for (Listener l : listeners) l.onError("Archivo no encontrado: " + ruta);
            return;
        }
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(archivo), "UTF-8"))) {
            String linea;
            int fila = 0;
            while ((linea = br.readLine()) != null) {
                fila++;
                if (fila == 1 || linea.trim().isEmpty()) continue;
                String[] p = linea.split(",", 7);
                if (p.length < 6) {
                    for (Listener l : listeners) l.onError("Fila " + fila + ": faltan columnas.");
                    return;
                }
                NivelIntensidad intensidad = NivelIntensidad.fromString(p[3].trim());
                Integer semana = (p.length > 6 && !p[6].trim().isEmpty())
                                 ? Integer.parseInt(p[6].trim()) : null;
                ejercicios.add(crearEjercicio(p[2].trim(), p[0].trim(), p[1].trim(),
                               intensidad, Integer.parseInt(p[4].trim()), p[5].trim(), semana));
            }
            notificarCarga();
        } catch (Exception e) {
            for (Listener l : listeners) l.onError("Error al leer: " + e.getMessage());
        }
    }
 
    // Carga desde BD
    public void cargarDesdeBD() {
        ejercicios.clear();
        try {
            Connection conn = ConexionBD.getConexion();
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT codigo, nombre, tipo, intensidad, tiempo_minutos, descripcion, semana_ultimo_uso FROM ejercicios");
            while (rs.next()) {
                NivelIntensidad intensidad = NivelIntensidad.fromString(rs.getString("intensidad"));
                Integer semana = rs.getObject("semana_ultimo_uso") != null ? rs.getInt("semana_ultimo_uso") : null;
                ejercicios.add(crearEjercicio(
                    rs.getString("tipo"), rs.getString("codigo"), rs.getString("nombre"),
                    intensidad, rs.getInt("tiempo_minutos"), rs.getString("descripcion"), semana));
            }
            notificarCarga();
        } catch (SQLException e) {
            for (Listener l : listeners) l.onError("Error BD: " + e.getMessage());
        }
    }
 
    // Generar rutina
    public void generarRutina(int cantCardio, NivelIntensidad intensCardio,
                              int cantFuerza,  NivelIntensidad intensFuerza,
                              String cliente) {
        List<Ejercicio> cardios = filtrar("Cardiovascular", intensCardio);
        List<Ejercicio> fuerzas = filtrar("Fuerza", intensFuerza);
 
        if (cardios.size() < cantCardio) {
            for (Listener l : listeners)
                l.onError("No hay suficientes ejercicios cardiovasculares de nivel " + intensCardio
                          + ". Disponibles: " + cardios.size());
            return;
        }
        if (fuerzas.size() < cantFuerza) {
            for (Listener l : listeners)
                l.onError("No hay suficientes ejercicios de fuerza de nivel " + intensFuerza
                          + ". Disponibles: " + fuerzas.size());
            return;
        }
 
        Rutina nueva = new Rutina(cliente, semanaActual);
        for (int i = 0; i < cantCardio; i++) {
            cardios.get(i).marcarUso(semanaActual);
            nueva.agregarEjercicio(cardios.get(i));
        }
        for (int i = 0; i < cantFuerza; i++) {
            fuerzas.get(i).marcarUso(semanaActual);
            nueva.agregarEjercicio(fuerzas.get(i));
        }
 
        rutinaActual = nueva;
        guardarRutina(cliente, nueva);
        for (Listener l : listeners) l.onRutinaGenerada(rutinaActual);
    }
 
    // Guardar cliente, rutina y ejercicios en BD
    private void guardarRutina(String nombreCliente, Rutina rutina) {
        try {
            Connection conn = ConexionBD.getConexion();
 
            // 1. Guardar cliente
            PreparedStatement psCliente = conn.prepareStatement(
                "INSERT INTO clientes (nombre) VALUES (?)",
                Statement.RETURN_GENERATED_KEYS);
            psCliente.setString(1, nombreCliente);
            psCliente.executeUpdate();
            ResultSet rsCliente = psCliente.getGeneratedKeys();
            int clienteId = rsCliente.next() ? rsCliente.getInt(1) : -1;
 
            // 2. Guardar rutina
            PreparedStatement psRutina = conn.prepareStatement(
                "INSERT INTO rutinas (cliente_id, semana, tiempo_total) VALUES (?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            psRutina.setInt(1, clienteId);
            psRutina.setInt(2, semanaActual);
            psRutina.setInt(3, rutina.getTiempoTotal());
            psRutina.executeUpdate();
            ResultSet rsRutina = psRutina.getGeneratedKeys();
            int rutinaId = rsRutina.next() ? rsRutina.getInt(1) : -1;
 
            // 3. Guardar ejercicios de la rutina
            PreparedStatement psEj = conn.prepareStatement(
                "INSERT INTO rutina_ejercicios (rutina_id, ejercicio_codigo) VALUES (?,?)");
            for (Ejercicio e : rutina.getEjercicios()) {
                psEj.setInt(1, rutinaId);
                psEj.setString(2, e.getCodigo());
                psEj.executeUpdate();
            }
 
        } catch (SQLException e) {
            System.err.println("Error al guardar rutina: " + e.getMessage());
        }
    }
 
    // Helpers
    private List<Ejercicio> filtrar(String tipo, NivelIntensidad nivel) {
        List<Ejercicio> result = new ArrayList<>();
        for (Ejercicio e : ejercicios)
            if (e.getTipo().equals(tipo) && e.getIntensidad() == nivel && e.estaDisponible(semanaActual))
                result.add(e);
        return result;
    }
 
    private Ejercicio crearEjercicio(String tipo, String codigo, String nombre,
                                     NivelIntensidad intensidad, int tiempo,
                                     String descripcion, Integer semana) {
        if (tipo.equalsIgnoreCase("cardiovascular"))
            return new EjercicioCardiovascular(codigo, nombre, intensidad, tiempo, descripcion, semana);
        return new EjercicioFuerza(codigo, nombre, intensidad, tiempo, descripcion, semana);
    }
 
    public Rutina getRutinaActual()      { return rutinaActual; }
    public int    getCantidadCargada()   { return ejercicios.size(); }
    public void   setSemanaActual(int s) { this.semanaActual = s; }
    public int    getSemanaActual()      { return semanaActual; }
}