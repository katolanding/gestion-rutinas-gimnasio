-- gimnasio_db.sql
-- Crea la base de datos, las tablas y carga ejercicios de EJEMPLO (datos ficticios).
-- Uso:  mysql -u <usuario> -p < sql/gimnasio_db.sql

CREATE DATABASE IF NOT EXISTS gimnasio_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE gimnasio_db;

CREATE TABLE IF NOT EXISTS ejercicios (
  codigo            VARCHAR(10)  PRIMARY KEY,
  nombre            VARCHAR(100) NOT NULL,
  tipo              ENUM('cardiovascular','fuerza') NOT NULL,
  intensidad        ENUM('basico','intermedio','avanzado','alto_rendimiento') NOT NULL,
  tiempo_minutos    INT NOT NULL,
  descripcion       TEXT NOT NULL,
  semana_ultimo_uso INT DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS clientes (
  id     INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS rutinas (
  id           INT AUTO_INCREMENT PRIMARY KEY,
  cliente_id   INT NOT NULL,
  semana       INT,
  tiempo_total INT,
  fecha        DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (cliente_id) REFERENCES clientes(id)
);

CREATE TABLE IF NOT EXISTS rutina_ejercicios (
  id               INT AUTO_INCREMENT PRIMARY KEY,
  rutina_id        INT NOT NULL,
  ejercicio_codigo VARCHAR(10) NOT NULL,
  FOREIGN KEY (rutina_id)        REFERENCES rutinas(id),
  FOREIGN KEY (ejercicio_codigo) REFERENCES ejercicios(codigo)
);

-- Vista de solo lectura: conteos por tipo e intensidad
CREATE OR REPLACE VIEW estadisticas_ejercicios AS
  SELECT tipo, intensidad, COUNT(*) AS total, SUM(tiempo_minutos) AS tiempo_total
  FROM ejercicios
  GROUP BY tipo, intensidad;

-- Ejercicios de ejemplo (4 por cada combinación tipo/intensidad)
INSERT INTO ejercicios (codigo, nombre, tipo, intensidad, tiempo_minutos, descripcion, semana_ultimo_uso) VALUES
  ('CB01','Caminata en cinta','cardiovascular','basico',20,'Caminata a ritmo suave en cinta',NULL),
  ('CB02','Bicicleta estática suave','cardiovascular','basico',20,'Pedaleo continuo con resistencia baja',NULL),
  ('CB03','Elíptica ligera','cardiovascular','basico',15,'Movimiento fluido sin impacto',NULL),
  ('CB04','Remo suave','cardiovascular','basico',15,'Remo en máquina a ritmo constante',NULL),
  ('CI01','Trote continuo','cardiovascular','intermedio',25,'Trote constante en cinta',NULL),
  ('CI02','Bicicleta con intervalos','cardiovascular','intermedio',25,'Alterna ritmo moderado y rápido',NULL),
  ('CI03','Remo moderado','cardiovascular','intermedio',20,'Remo con ritmo sostenido',NULL),
  ('CI04','Escaladora','cardiovascular','intermedio',20,'Subida continua en máquina escaladora',NULL),
  ('CA01','Carrera en cuesta','cardiovascular','avanzado',30,'Carrera con inclinación en cinta',NULL),
  ('CA02','Intervalos de bicicleta','cardiovascular','avanzado',30,'Series cortas de alta resistencia',NULL),
  ('CA03','Remo por intervalos','cardiovascular','avanzado',25,'Series de remo con pausas cortas',NULL),
  ('CA04','Cuerda de saltar','cardiovascular','avanzado',20,'Saltos continuos por bloques',NULL),
  ('CR01','Sprints en cinta','cardiovascular','alto_rendimiento',30,'Series de velocidad máxima con pausas',NULL),
  ('CR02','Circuito HIIT','cardiovascular','alto_rendimiento',35,'Circuito de alta intensidad por estaciones',NULL),
  ('CR03','Remo de potencia','cardiovascular','alto_rendimiento',30,'Remo de máxima potencia por series',NULL),
  ('CR04','Burpees por tiempo','cardiovascular','alto_rendimiento',25,'Burpees en bloques cronometrados',NULL),
  ('FB01','Sentadilla con peso corporal','fuerza','basico',15,'Sentadillas sin carga adicional',NULL),
  ('FB02','Flexiones de rodillas','fuerza','basico',15,'Flexiones apoyando las rodillas',NULL),
  ('FB03','Plancha frontal','fuerza','basico',10,'Isométrico de core en el suelo',NULL),
  ('FB04','Remo con banda','fuerza','basico',15,'Remo con banda elástica',NULL),
  ('FI01','Sentadilla con mancuernas','fuerza','intermedio',20,'Sentadilla con carga moderada',NULL),
  ('FI02','Press de pecho con mancuernas','fuerza','intermedio',20,'Press en banco con mancuernas',NULL),
  ('FI03','Peso muerto rumano','fuerza','intermedio',20,'Bisagra de cadera con barra ligera',NULL),
  ('FI04','Remo con mancuerna','fuerza','intermedio',20,'Remo unilateral apoyado en banco',NULL),
  ('FA01','Sentadilla con barra','fuerza','avanzado',30,'Sentadilla con barra y carga alta',NULL),
  ('FA02','Press de banca','fuerza','avanzado',30,'Press con barra en banco plano',NULL),
  ('FA03','Peso muerto convencional','fuerza','avanzado',30,'Levantamiento con barra desde el suelo',NULL),
  ('FA04','Dominadas','fuerza','avanzado',25,'Dominadas con peso corporal',NULL),
  ('FR01','Sentadilla frontal pesada','fuerza','alto_rendimiento',35,'Series pesadas con pocas repeticiones',NULL),
  ('FR02','Press militar con barra','fuerza','alto_rendimiento',30,'Press de hombros con carga alta',NULL),
  ('FR03','Clean and press','fuerza','alto_rendimiento',35,'Movimiento olímpico combinado',NULL),
  ('FR04','Dominadas lastradas','fuerza','alto_rendimiento',30,'Dominadas con peso adicional',NULL);
