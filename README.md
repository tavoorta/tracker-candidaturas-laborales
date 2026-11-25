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

## Contribuciones por miembro

| Miembro | Tareas v1.0 |
|---|---|
| Camila Bustelo | Comandos `-listar`, `-borrar`, `-añadir`, `-ver` (capas `negocio` y `presentacion`) |
| Gustavo Romero | Modelo, enumerados, configuración, repositorio en fichero, importación CSV, comando `-importar`, arranque (`Main`) |

## Versión 2.0 (tag `v2.0`)

La persistencia pasa de fichero de texto a una base de datos **SQLite** con **JDBC**, y la
línea de comandos se sustituye por una interfaz gráfica en **JavaFX** con vistas FXML
diseñadas en Scene Builder.

### Diseño

- Prototipo interactivo en Figma (solo lectura): [ver en Figma](https://www.figma.com/design/dVyIBwq1UQGOmn7dWINSgO/MentorIA---Camila-Bustelo--Gustavo-Romero?node-id=0-1&t=yCBB7rvncC989qwJ-1)
- Las vistas de la v2.0 siguen ese prototipo; los colores y la tipografía del manual de estilo
  se aplican con CSS en la v3.0.

### Tecnologías

- **Java 27** y **Maven**
- **Lombok** (`1.18.48`) para getters, setters y constructores
- **SQLite** (`org.xerial:sqlite-jdbc:3.47.1.0`): base de datos embebida en un único archivo
  (`tracker.db`), sin servidor
- **JDBC** (`DataSource`, `PreparedStatement`, `ResultSet`) para el acceso a datos
- **JavaFX 21** (`javafx-controls`, `javafx-fxml`) + **Scene Builder** para las vistas
- **JUnit 5** (`5.14.4`) + **Mockito** (`5.23.0`) para las pruebas unitarias
- **Figma** para el prototipo

### Arquitectura

Tres capas en `com.example.tracker`; cada capa solo usa la de abajo:

```
com.example.tracker
├── App.java / Launcher.java  → arranque: crea la conexión, los DAO y los servicios
├── SceneNavigator.java       → carga las vistas FXML y crea cada controlador con su servicio
├── common/AppContext         → datos compartidos entre pantallas (la oferta o empresa a editar)
├── presentacion/
│   ├── controllers/          → un controlador por vista (Dashboard, Candidaturas, Empresas…)
│   └── celdas/               → celdas de tabla: estado con color, botones de acción, iconos
├── negocio/                  → ServicioOferta, ServicioEmpresa, ServicioEntrevista,
│                               ServicioNota, ServicioActividad (validaciones, filtros,
│                               estadísticas y transacciones)
└── datos/
    ├── DAO/                  → interfaz genérica DAO<T> y un DAO JDBC por tabla
    ├── data/                 → DataSourceFactory, InicializadorBaseDatos, ImportadorCsv
    ├── modelo/               → Oferta, Empresa, Entrevista, Nota, Actividad
    └── enums/                → Estado, TipoEntrevista, TipoActividad
```

- Los controladores solo llaman a los **servicios**; los servicios usan la interfaz `DAO<T>`;
  solo los DAO ejecutan SQL.
- Filtrar, contar y ordenar se hace en la capa de **negocio**, en Java.

### Base de datos

- Script de creación: [`src/main/resources/database/schema.sql`](./src/main/resources/database/schema.sql)
- Tablas: `EMPRESA`, `OFERTA`, `ENTREVISTA`, `NOTA` y `ACTIVIDAD`, relacionadas con claves
  foráneas con nombre (`CONSTRAINT FK_...`), que se comprueban al activar
  `setEnforceForeignKeys(true)`.
- **Borrado lógico**: ninguna fila se elimina; se marca `Active = 0` y las consultas solo
  devuelven registros activos (y cuyos registros padre también lo están).
- **Transacciones**: al borrar una candidatura se borran también sus entrevistas y notas; al
  borrar una empresa, sus candidaturas con todo lo suyo. Si algo falla, `rollback` y no se
  borra nada.
- La URL de conexión está en `app.properties` (raíz del proyecto) y la lee `DataSourceFactory`:

```properties
db.url=jdbc:sqlite:tracker.db
```

### Carga inicial de datos

Al arrancar, `InicializadorBaseDatos` crea las tablas a partir de `schema.sql` si no existen
e `ImportadorCsv` carga [`empresas.csv`](./empresas.csv) y
[`candidaturas.csv`](./candidaturas.csv) **solo si las tablas están vacías**, para no duplicar
los datos en cada ejecución. La columna `notas` de cada candidatura se guarda como su primera
nota.

Para volver a los datos iniciales basta con cerrar la aplicación y borrar `tracker.db`; se
crea de nuevo en el siguiente arranque.

### Pantallas

| Pantalla | Qué permite | HU |
|---|---|---|
| Inicio de sesión | Entrar en la aplicación (en esta versión no valida credenciales) | — |
| Dashboard | Totales de ofertas, entrevistas, pendientes y rechazos con su variación semanal; próximas entrevistas; actividad reciente; últimas candidaturas | — |
| Candidaturas | Tabla paginada; ordenar con clic en el encabezado; buscar y filtrar por estado, empresa, salario y fechas; alta y edición en ventana modal; cambio de estado desde la tabla; borrado | HU-09, HU-12, HU-13 |
| Detalle de candidatura | Datos de la oferta; notas con fecha; historial de entrevistas en un panel expandible (fecha, tipo, resultado) con alta, edición y borrado | HU-11 |
| Empresas | Tabla paginada con buscador y nº de candidaturas; alta y edición en ventana modal; borrado | HU-10 |

### Requisitos previos

- JDK 27 (la versión que define el `pom.xml`)
- Maven (o el soporte de Maven de IntelliJ IDEA)
- Plugin de **Lombok** en el IDE con *annotation processing* habilitado
  (`Settings → Build, Execution, Deployment → Compiler → Annotation Processors →
  Enable annotation processing`)

### Cómo ejecutar (v2.0)

Desde IntelliJ IDEA:

1. Abrir la carpeta del proyecto (`File → Open`) y dejar que Maven importe las dependencias.
2. Crear una **Run Configuration** (`Run → Edit Configurations → + → Application`):
   - **Main class**: `com.example.tracker.Launcher`
   - **Working directory**: la raíz del proyecto (donde están `app.properties` y los CSV)
3. Ejecutar con el botón ▶. La primera vez se crean `tracker.db` y los datos iniciales.

> Si el repositorio está en una ruta muy larga en Windows y git muestra
> "Filename too long", ejecutar `git config core.longpaths true` en el repositorio.

### Pruebas unitarias

Las pruebas están en `src/test/java`, en los mismos paquetes que el código que prueban. Cubren
la capa de negocio (validaciones, filtros, estadísticas y la transacción de borrado) simulando
los DAO con Mockito, sin usar la base de datos real.

```bash
mvn test
```

Desde IntelliJ: clic derecho en `src/test/java` → *Run 'All Tests'*. Como el proyecto tiene
`module-info.java`, en `Run → Edit Configurations → Edit configuration templates → JUnit →
Modify options` hay que marcar **Do not use --module-path option**.

### Datos de prueba

[`candidaturas.csv`](./candidaturas.csv) contiene 15 candidaturas generadas con IA a partir del
prompt del enunciado (adaptado con descripción y salario), y [`empresas.csv`](./empresas.csv)
las 5 empresas que aparecen en ellas (Indra, Accenture, Everis, Telefonica y Santander).

### Uso de IA

Lpos prompts usados con asistentes de IA están en [`AGENTS.md`](./AGENTS.md).

### Referencias

- Implementación de SQLite con Java (vídeos de referencia):
    - [https://www.youtube.com/watch?v=m3wPdSWl-G8&t=192s](https://www.youtube.com/watch?v=m3wPdSWl-G8&t=192s)
    - [https://www.youtube.com/watch?v=jM4KnPiedK0&t=753s](https://www.youtube.com/watch?v=jM4KnPiedK0&t=753s)

### Contribuciones por miembro

| Miembro | Tareas v2.0 |
|---|---|
| Camila Bustelo | Enums y modelos de la v2; borrado en transacción y consultas de registros activos en los DAO; tabla NOTA, claves foráneas y carga de notas del CSV; servicio de actividad; vistas y controladores de Dashboard, Detalle de candidatura y Empresas; formularios de empresa y entrevista; pruebas unitarias con JUnit y Mockito |
| Gustavo Romero | Migración a SQLite y JDBC (`DataSourceFactory`, `schema.sql`, carga inicial de los CSV); migración de la CLI a JavaFX (inicio de sesión, menú principal y navegación); pantalla de Candidaturas (tabla, filtros, paginación, celdas de estado y acciones); formulario de candidatura; notas de las ofertas; javadoc; `AGENTS.md` |
