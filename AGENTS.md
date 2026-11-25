# AGENTS.md — Uso de IA en el proyecto

**Proyecto:** Tracker de Candidaturas Laborales (MentorIA)
**Equipo:** Camila Bustelo, Gustavo Romero

---

## Prompt: casos de prueba unitarios (JUnit 5 + Mockito)

Prompt usado para generar las pruebas de la capa de negocio. Se adjuntan al asistente el
`pom.xml` y el código de los servicios y modelos.

```text
Actúa como desarrollador Java con experiencia en pruebas unitarias.

## Contexto
Tengo una aplicación JavaFX de seguimiento de candidaturas laborales con persistencia JDBC
sobre SQLite y arquitectura en tres capas (presentacion → negocio → datos).
- Java 27, Maven, Lombok, JUnit 5 (junit-jupiter 5.14.4) y Mockito 5.23
  (mockito-core y mockito-junit-jupiter, scope test).
- El proyecto tiene module-info.java; Surefire está configurado con useModulePath=false.
- Los servicios están en com.example.tracker.negocio y reciben sus dependencias por
  constructor (@RequiredArgsConstructor de Lombok), en este orden:
  - ServicioOferta(DAO<Oferta>, DAO<Entrevista>, DAO<Nota>, DataSource, ServicioActividad)
  - ServicioEmpresa(DAO<Empresa>, ServicioOferta, DataSource)
  - ServicioEntrevista(DAO<Entrevista>)
  - ServicioNota(DAO<Nota>)
  - ServicioActividad(DAO<Actividad>)
- DAO<T> es una interfaz con: findById, findAll, save, update, deleteById(Long) y
  deleteById(Connection, Long) (este último para transacciones).
- Los modelos usan Lombok (@Data, @NoArgsConstructor, @AllArgsConstructor).

Te adjunto el código de los servicios y de los modelos.

## Qué necesito
Una clase de prueba por servicio que compruebe la lógica de negocio SIN usar la base de
datos: los DAO, el DataSource y la Connection se simulan con Mockito.

Casos a cubrir:
1. Reglas de negocio de guardar: que lance excepción y NO llame a save cuando falta un dato
   obligatorio (título de la oferta, nombre de la empresa, tipo de la entrevista, texto de la
   nota) o cuando el nombre de la empresa ya existe; y que llame a save cuando los datos son
   correctos.
2. Filtrado: por estado y combinando empresa + salario mínimo.
3. Estadísticas: contar candidaturas por estado, próximas entrevistas (no incluir las
   pasadas) y actividad reciente (la más reciente primero).
4. Listar por oferta: que devuelva solo las entrevistas o notas de esa oferta.
5. Transacción: al borrar una oferta, que use la conexión de la transacción en
   deleteById(conexion, id) y termine con commit().

## Reglas de estilo (obligatorias)
- Arquitectura espejo: src/test/java/com/example/tracker/negocio/<Servicio>Test.java.
- @ExtendWith(MockitoExtension.class) y @Mock para cada dependencia.
- Crear el servicio con new en un @BeforeEach. NO usar @InjectMocks (hay varios DAO<...>
  del mismo tipo y Mockito podría inyectarlos cruzados).
- Usar solo: when(...).thenReturn(...), assertEquals, assertThrows, verify(...) y
  verify(..., never()). No usar @DisplayName, ArgumentCaptor, matchers como eq o anyString,
  ni streams dentro de los assert.
- Un método auxiliar privado para crear los datos de prueba (por ejemplo crearOferta(...)).
- Nombres de test en español que digan qué comprueban, por ejemplo
  guardarSinTituloLanzaExcepcion o filtrarPorEstado.
- Pocos casos y sencillos: entre 1 y 6 por clase, solo lo importante.
- No stubbear nada que el test no use (MockitoExtension falla con stubs innecesarios).
- No modificar el código de producción.

## Formato de la respuesta
Devuelve cada clase completa, con su package e imports, precedida de la ruta del archivo.
Sin explicaciones adicionales.
```

### Resultado

Se generaron y revisaron 15 pruebas en `src/test/java/com/example/tracker/negocio/`:

| Clase | Pruebas |
|---|---|
| `ServicioOfertaTest` | 6 (validación, alta, filtro por estado, filtro empresa + salario, conteo, transacción) |
| `ServicioEmpresaTest` | 3 (nombre obligatorio, nombre repetido, alta) |
| `ServicioEntrevistaTest` | 3 (tipo obligatorio, listar por oferta, próximas entrevistas) |
| `ServicioNotaTest` | 2 (nota vacía, listar por oferta) |
| `ServicioActividadTest` | 1 (actividad reciente ordenada) |

Para ejecutarlas: `mvn test`, o desde IntelliJ con clic derecho en `src/test/java` →
*Run 'All Tests'* (en la plantilla de JUnit debe estar marcada la opción
*Do not use --module-path option*).
