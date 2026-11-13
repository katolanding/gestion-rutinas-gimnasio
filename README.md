# Sistema de Gestión de Rutinas de Entrenamiento

Aplicación de escritorio en **Java Swing** que carga ejercicios desde **MySQL** (o desde un CSV) y genera rutinas personalizadas para clientes de un gimnasio.

> Proyecto académico en equipo (Paradigmas de Programación, Universidad Andrés Bello, 2026). Mi parte: backend (lógica, patrón Observer y conexión MySQL) y la ventana de generación de rutinas; el equipo diseñó las demás ventanas.

## Qué hace

- Carga ejercicios desde MySQL o desde un archivo CSV y muestra estadísticas por tipo (cardiovascular / fuerza) e intensidad.
- Genera una rutina según cliente, semana, cantidad e intensidad de cada tipo de ejercicio.
- Un ejercicio no se repite si se usó en la semana actual o en la anterior (diferencia de semanas ≤ 1).
- Guarda cliente, rutina y ejercicios en la base de datos, y muestra un resumen final.

## Arquitectura

- **Patrón Observer:** `GestorEjercicios` (Singleton) notifica a las ventanas mediante la interfaz `Listener` (`onEjerciciosCargados`, `onRutinaGenerada`, `onError`).
- **Herencia:** `Ejercicio` (abstracta) con `EjercicioCardiovascular` y `EjercicioFuerza`.
- **Capas:** `src/backend` (lógica y acceso a datos) y `src/frontend` (ventanas Swing).

## Requisitos

- JDK 11 o superior
- Apache NetBeans 12 o superior
- MySQL Server 8.0 o superior
- MySQL Connector/J (ver `lib/README.md`)

## Puesta en marcha

1. Crea la base de datos y carga los ejercicios de ejemplo:
   ```bash
   mysql -u <usuario> -p < sql/gimnasio_db.sql
   ```
2. Copia el driver en `lib/mysql-connector-j.jar`.
3. Define las credenciales con variables de entorno **(no se guardan en el código)**:

   | Variable | Valor por defecto |
   | --- | --- |
   | `GYM_DB_URL` | `jdbc:mysql://localhost:3306/gimnasio_db?...` |
   | `GYM_DB_USER` | `root` |
   | `GYM_DB_PASSWORD` | *(vacía)* |

   Windows (PowerShell):
   ```powershell
   $env:GYM_DB_PASSWORD = "tu_contraseña"
   ```
   Linux / macOS:
   ```bash
   export GYM_DB_PASSWORD="tu_contraseña"
   ```
   Si usas NetBeans, defínelas antes de abrirlo, o en *Properties → Run → Environment*.
4. Abre el proyecto en NetBeans, verifica que la clase principal sea `frontend.VentanaPrincipal` y ejecuta (F6).

Sin base de datos, también puedes usar el botón **Seleccionar CSV** con `data/ejercicios_ejemplo.csv`. En ese modo la rutina se genera igual, pero no se guarda en la base de datos.

## Flujo de uso

1. **Ventana principal:** "Cargar Ejercicios".
2. **Carga:** "Conectar BD MySQL" (o "Seleccionar CSV") y luego "Iniciar generación".
3. **Generación:** cliente, semana, cantidad e intensidad → "Generar Rutina".
4. **Revisión:** navega ejercicio por ejercicio y pulsa "Resumen".
5. **Resumen:** estadísticas finales de la rutina.

## Modelo de datos

```
clientes (id PK, nombre)
   └── rutinas (id PK, cliente_id FK, semana, tiempo_total, fecha)
          └── rutina_ejercicios (id PK, rutina_id FK, ejercicio_codigo FK)
                                                         └── ejercicios (codigo PK, nombre, tipo, intensidad,
                                                                         tiempo_minutos, descripcion, semana_ultimo_uso)
estadisticas_ejercicios → vista de solo lectura (conteos por tipo e intensidad)
```

## Limitaciones

- La semana actual se ingresa manualmente.
- No hay autenticación de usuarios.
- Sin pruebas automatizadas.
- Proyecto académico: no está pensado para uso en producción.

## Logo (opcional)

La ventana principal muestra `src/frontend/logo.png`. Si reemplazas o eliminas el archivo, la aplicación funciona igual (sin logo).
