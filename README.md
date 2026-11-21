# Tracker de Candidaturas Laborales

Aplicación de gestión de ofertas laborales desarrollada como Ejercicio Integrador
del 1er Trimestre (módulos de Acceso a Datos y Desarrollo de Interfaces). Permite
registrar, consultar, filtrar, eliminar e importar ofertas a ofertas de empleo
desde línea de comandos.

Proyecto desarrollado en equipo de 2 personas, evolucionando en 3 entregas incrementales:
**v1.0** (archivos de texto + CLI + diseño en Figma), v2.0 (JDBC + JavaFX) y v3.0
(Hibernate + JavaFX avanzado + asistente IA con Ollama4j).

## Equipo

- Camila Bustelo
- Gustavo Romero

## Versión 1.0 (tag `v1.0`, commit `5459771`)

## Tecnologías utilizadas

- **Java 27**
- **Maven** como gestor de dependencias
- **Lombok** (`1.18.48`) para reducir código repetitivo (getters/setters/constructores)
- Persistencia en **fichero de texto plano** (sin base de datos en esta entrega)
- **Figma** para el prototipo interactivo y el manual de estilo

## Arquitectura

El código está organizado en 3 capas, separadas en paquetes bajo `com.tracker`:

```
com.tracker
├── Main.java            → punto de entrada, conecta las capas entre sí
├── datos/                → acceso a datos
│   ├── modelo/           → entidad Candidatura
│   ├── enums/            → EstadoCandidatura
│   ├── repositorio/      → interfaz CandidaturaRepository + implementación en fichero
│   │                       (incluye la conversión línea ↔ oferta)
│   └── ayuda/            → ServicioConfiguracion, ImportadorCsv
├── negocio/              → ServicioCandidatura (validaciones, filtrado)
└── presentacion/         → ComandoInterpretado (parseo de args) y EjecutorComandos (CLI)
```

La interfaz `OfertaRepository` permite que en v2.0 y v3.0 se sustituya la
implementación en fichero por JDBC o Hibernate sin tocar las capas `negocio` ni
`presentacion`.

## Requisitos previos

- JDK 27 instalado (es la versión que define el `pom.xml`)
- Maven (o usar el soporte integrado de Maven de IntelliJ IDEA)
- Plugin de **Lombok** instalado en el IDE, con *annotation processing* habilitado
  (`Settings → Build, Execution, Deployment → Compiler → Annotation Processors →
  Enable annotation processing`)

## Cómo compilar y ejecutar (v1.0)

El programa se ejecuta por línea de comandos, pasando la operación y sus parámetros
como argumentos de `main(String[] args)`. No tiene menú interactivo ni usa `Scanner`.

### Desde IntelliJ IDEA

1. Abrir la carpeta del proyecto (`File → Open`), dejar que Maven importe las dependencias.
2. Crear una **Run Configuration** (`Run → Edit Configurations → +  → Application`):
   - **Main class**: `com.tracker.Main`
   - **Working directory**: la raíz del proyecto (donde están `app.properties` y `ofertas.txt`)
   - **Program arguments**: el comando a ejecutar (ver tabla de abajo)
3. Ejecutar con el botón ▶.

### Desde terminal (Maven)

Desde la raíz del proyecto (donde están `app.properties` y `ofertas.txt`):

```bash
mvn compile
java -cp target/classes com.tracker.Main -listar
```

### Comandos disponibles

| Comando | Ejemplo |
|---|---|
| Listar todas | `-listar` |
| Listar filtrado | `-listar --estado pendiente --empresa Indra` |
| Añadir | `-añadir --titulo "Dev Java" --empresa "Indra" --estado pendiente --fecha 15/09/2026` |
| Ver detalle | `-ver 1` |
| Borrar | `-borrar 1` |
| Importar CSV | `-importar ofertas.csv` |

Estados válidos: `pendiente`, `entrevista`, `rechazado`, `aceptado`.

Opciones de `-añadir`: `--titulo`, `--empresa`, `--estado` y `--fecha` (`dd/MM/yyyy`) son
obligatorias; `--enlace` y `--notas` son opcionales. Los textos no pueden contener el
carácter `;`, porque es el separador del fichero de datos.

## Configuración

La ruta del fichero de datos se define en `app.properties` (en la raíz del proyecto):

```properties
fichero=ofertas.txt
```

## Datos de prueba

El fichero [`ofertas.csv`](./ofertas.csv) contiene 15 ofertas de ejemplo
(generadas con IA siguiendo el prompt del enunciado): 5 empresas distintas (Indra,
Accenture, Everis, Telefonica y Santander), entre 3 y 4 ofertas en cada estado y
fechas de postulación entre el 22/07/2026 y el 14/10/2026. Para cargarlas como datos de
trabajo:

```
-importar ofertas.csv
```

> Si `ofertas.txt` ya tiene datos, el import se **suma** a lo existente (no lo
> sobrescribe). Para partir de un estado limpio con exactamente esas 15, vacía
> `ofertas.txt` antes de importar.

## Versión 2.0 (tag `v2.0`, commit `84a0e17`)

> En desarrollo. La persistencia pasa de fichero de texto a base de datos y la CLI se
> sustituye por una interfaz gráfica en JavaFX.

### Tecnologías añadidas

- **SQLite** (`org.xerial:sqlite-jdbc:3.47.1.0`) como base de datos embebida: se guarda
  en un único archivo (`tracker.db`) y no necesita servidor ni Docker
- **JDBC** (`DataSource`, `PreparedStatement`, `ResultSet`) para el acceso a datos
- **JavaFX 21** (`javafx-controls`, `javafx-fxml`) + **Scene Builder** para las vistas

### Base de datos

- Script de creación: [`src/main/resources/database/schema.sql`](./src/main/resources/database/schema.sql)
- Tablas: `EMPRESA`, `OFERTA`, `ENTREVISTA` y `ACTIVIDAD`, relacionadas con claves
  foráneas con nombre (`CONSTRAINT FK_...`) y columna `Active` para borrado lógico
- La URL de conexión se define en `app.properties` (raíz del proyecto) y la lee
  `DataSourceFactory`, que crea la conexión:

```properties
db.url=jdbc:sqlite:tracker.db
```

### Carga inicial de datos

Al arrancar la aplicación, `InicializadorBaseDatos` crea las tablas a partir de `schema.sql`
si no existen y `ImportadorCsv` carga [`empresas.csv`](./empresas.csv) y
[`candidaturas.csv`](./candidaturas.csv) **solo si las tablas están vacías**, para no
duplicar los datos en cada ejecución. Primero se insertan las empresas y después las
ofertas, que guardan el `EmpresaId` de su empresa.

### Interfaz gráfica

- `login-view.fxml` → inicio de sesión
- `shell-view.fxml` → ventana principal con menú lateral fijo; las secciones
  (Dashboard, Candidaturas, Empresas) se cargan dentro de su zona central

### Referencias

- Implementación de SQLite con Java, Video de referencia:
    - [https://www.youtube.com/watch?v=m3wPdSWl-G8&t=192s](https://www.youtube.com/watch?v=m3wPdSWl-G8&t=192s)
    - [https://www.youtube.com/watch?v=jM4KnPiedK0&t=753s](https://www.youtube.com/watch?v=jM4KnPiedK0&t=753s)

## Contribuciones por miembro

| Miembro | Tareas v1.0 |
|---|---|
| Camila Bustelo | Comandos `-listar`, `-borrar`, `-añadir`, `-ver` (capas `negocio` y `presentacion`) |
| Gustavo Romero | Modelo, enumerados, configuración, repositorio en fichero, importación CSV, comando `-importar`, arranque (`Main`) |
