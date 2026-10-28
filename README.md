# Tracker de Candidaturas Laborales

Aplicación de gestión de candidaturas laborales desarrollada como Ejercicio Integrador
del 1er Trimestre (módulos de Acceso a Datos y Desarrollo de Interfaces). Permite
registrar, consultar, filtrar, eliminar e importar candidaturas a ofertas de empleo
desde línea de comandos.

Proyecto desarrollado en equipo de 2 personas, evolucionando en 3 entregas incrementales:
**v1.0** (archivos de texto + CLI + diseño en Figma), v2.0 (JDBC + JavaFX) y v3.0
(Hibernate + JavaFX avanzado + asistente IA con Ollama4j).

> Este README documenta el estado de la **v1.0**.

## Equipo

- Camila Bustelo
- Gustavo Romero

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
│   │                       (incluye la conversión línea ↔ candidatura)
│   └── ayuda/            → ServicioConfiguracion, ImportadorCsv
├── negocio/              → ServicioCandidatura (validaciones, filtrado)
└── presentacion/         → ComandoInterpretado (parseo de args) y EjecutorComandos (CLI)
```

La interfaz `CandidaturaRepository` permite que en v2.0 y v3.0 se sustituya la
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
   - **Working directory**: la raíz del proyecto (donde están `app.properties` y `candidaturas.txt`)
   - **Program arguments**: el comando a ejecutar (ver tabla de abajo)
3. Ejecutar con el botón ▶.

### Desde terminal (Maven)

Desde la raíz del proyecto (donde están `app.properties` y `candidaturas.txt`):

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
| Importar CSV | `-importar candidaturas.csv` |

Estados válidos: `pendiente`, `entrevista`, `rechazado`, `aceptado`.

Opciones de `-añadir`: `--titulo`, `--empresa`, `--estado` y `--fecha` (`dd/MM/yyyy`) son
obligatorias; `--enlace` y `--notas` son opcionales. Los textos no pueden contener el
carácter `;`, porque es el separador del fichero de datos.

## Configuración

La ruta del fichero de datos se define en `app.properties` (en la raíz del proyecto):

```properties
fichero=candidaturas.txt
```

## Datos de prueba

El fichero [`candidaturas.csv`](./candidaturas.csv) contiene 15 candidaturas de ejemplo
(generadas con IA siguiendo el prompt del enunciado): 5 empresas distintas (Indra,
Accenture, Everis, Telefonica y Santander), entre 3 y 4 candidaturas en cada estado y
fechas de postulación entre el 22/07/2026 y el 14/10/2026. Para cargarlas como datos de
trabajo:

```
-importar candidaturas.csv
```

> Si `candidaturas.txt` ya tiene datos, el import se **suma** a lo existente (no lo
> sobrescribe). Para partir de un estado limpio con exactamente esas 15, vacía
> `candidaturas.txt` antes de importar.

## Prototipo y manual de estilo

- Prototipo interactivo en Figma: `<pendiente — enlace a añadir>`
- Manual de estilo (PDF): `<pendiente — enlace o ruta a añadir>`

## Contribuciones por miembro

| Miembro | Tareas v1.0 |
|---|---|
| Camila Bustelo | Comandos `-listar`, `-borrar`, `-añadir`, `-ver` (capas `negocio` y `presentacion`) |
| Gustavo Romero | Modelo, enumerados, configuración, repositorio en fichero, importación CSV, comando `-importar`, arranque (`Main`) |
