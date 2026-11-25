package com.example.tracker.negocio;

import com.example.tracker.datos.DAO.DAO;
import com.example.tracker.datos.enums.Estado;
import com.example.tracker.datos.enums.TipoActividad;
import com.example.tracker.datos.modelo.Empresa;
import com.example.tracker.datos.modelo.Entrevista;
import com.example.tracker.datos.modelo.Nota;
import com.example.tracker.datos.modelo.Oferta;
import lombok.RequiredArgsConstructor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Lógica de negocio de las candidaturas: validaciones, filtros, orden, estadísticas y
 * borrado en transacción. No sabe de dónde vienen los datos, eso lo resuelve {@link DAO}.
 */
@RequiredArgsConstructor
public class ServicioOferta {

    private final DAO<Oferta> repository;
    private final DAO<Entrevista> entrevistaRepository;
    private final DAO<Nota> notaRepository;
    private final DataSource dataSource;
    private final ServicioActividad servicioActividad;

    /**
     * Valida la candidatura y la guarda: si no tiene id la crea, si tiene id la actualiza.
     *
     * @param oferta candidatura con los datos del formulario
     * @throws RuntimeException si falta un campo obligatorio o el salario es negativo
     */
    public void guardar(Oferta oferta) {
        if (oferta.getTitulo() == null || oferta.getTitulo().isBlank()) {
            throw new RuntimeException("El título es obligatorio.");
        }
        if (oferta.getEmpresa() == null) {
            throw new RuntimeException("La empresa es obligatoria.");
        }
        if (oferta.getEstado() == null) {
            throw new RuntimeException("El estado es obligatorio.");
        }
        if (oferta.getFechaPostulacion() == null) {
            throw new RuntimeException("La fecha de aplicación es obligatoria.");
        }
        if (oferta.getSalario() < 0) {
            throw new RuntimeException("El salario no puede ser negativo.");
        }

        if (oferta.getOfertaId() == null) {
            repository.save(oferta);
        } else {
            Oferta guardada = buscarPorId(oferta.getOfertaId());
            Estado anterior = guardada == null ? null : guardada.getEstado();
            repository.update(oferta);
            registrarCambioDeEstado(oferta, anterior);
        }
    }

    /** Si la candidatura cambió de estado, lo deja en la actividad reciente. */
    private void registrarCambioDeEstado(Oferta oferta, Estado anterior) {
        if (anterior != null && oferta.getEstado() != anterior) {
            servicioActividad.registrar(TipoActividad.CAMBIO_ESTADO, oferta.getEstado(),
                    oferta.getTitulo() + " en " + oferta.getEmpresa().getNombre() + " pasó a " + oferta.getEstado(), oferta);
        }
    }

    /**
     * Borra una candidatura junto con sus entrevistas y sus notas en una sola
     * transacción: o se borra todo, o no se borra nada (borrado lógico, Active = 0).
     *
     * @param oferta candidatura a borrar
     * @throws RuntimeException si no se indica la candidatura, no tiene id o falla el borrado
     */
    public void borrar(Oferta oferta) {
        if (oferta == null || oferta.getOfertaId() == null) {
            throw new RuntimeException("Debe indicar la candidatura a borrar.");
        }

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                borrarConDependencias(conn, oferta);
                conn.commit();
            } catch (RuntimeException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Marca como borradas la candidatura, sus entrevistas y sus notas usando la conexión
     * de una transacción ya abierta (no hace commit: lo hace quien abrió la transacción).
     *
     * @param conn   conexión de la transacción
     * @param oferta candidatura a borrar
     */
    public void borrarConDependencias(Connection conn, Oferta oferta) {
        for (Entrevista entrevista : entrevistaRepository.findAll()) {
            if (entrevista.getOferta().getOfertaId().equals(oferta.getOfertaId())) {
                entrevistaRepository.deleteById(conn, entrevista.getEntrevistaId());
            }
        }
        for (Nota nota : notaRepository.findAll()) {
            if (nota.getOferta().getOfertaId().equals(oferta.getOfertaId())) {
                notaRepository.deleteById(conn, nota.getNotaId());
            }
        }
        repository.deleteById(conn, oferta.getOfertaId());
    }

    /**
     * @param empresa empresa de la que se quieren las candidaturas
     * @return las candidaturas de esa empresa
     */
    public List<Oferta> listarPorEmpresa(Empresa empresa) {
        List<Oferta> ofertasDeEmpresa = new ArrayList<>();
        for (Oferta oferta : repository.findAll()) {
            if (oferta.getEmpresa().getEmpresaId().equals(empresa.getEmpresaId())) {
                ofertasDeEmpresa.add(oferta);
            }
        }
        return ofertasDeEmpresa;
    }

    /**
     * @param estado        texto del estado a filtrar (por ejemplo "Pendiente"), o {@code null}
     * @param empresa       nombre exacto de la empresa a filtrar, o {@code null}
     * @param texto         texto a buscar en el título o en el nombre de empresa, o {@code null}
     * @param salarioMinimo salario mínimo (incluido), o {@code null}
     * @param salarioMaximo salario máximo (incluido), o {@code null}
     * @param desde         fecha de postulación desde (incluida), o {@code null}
     * @param hasta         fecha de postulación hasta (incluida), o {@code null}
     * @return las candidaturas que cumplen los filtros indicados, la más reciente primero
     */
    public List<Oferta> filtrar(String estado, String empresa, String texto,
                                Double salarioMinimo, Double salarioMaximo,
                                LocalDate desde, LocalDate hasta) {
        String busqueda = texto == null ? "" : texto.trim().toLowerCase();

        return repository.findAll().stream()
                .filter(c -> estado == null || c.getEstado().toString().equals(estado))
                .filter(c -> empresa == null || c.getEmpresa().getNombre().equalsIgnoreCase(empresa))
                .filter(c -> busqueda.isEmpty()
                        || c.getTitulo().toLowerCase().contains(busqueda)
                        || c.getEmpresa().getNombre().toLowerCase().contains(busqueda))
                .filter(c -> salarioMinimo == null || c.getSalario() >= salarioMinimo)
                .filter(c -> salarioMaximo == null || c.getSalario() <= salarioMaximo)
                .filter(c -> desde == null || !c.getFechaPostulacion().isBefore(desde))
                .filter(c -> hasta == null || !c.getFechaPostulacion().isAfter(hasta))
                .sorted((a, b) -> b.getFechaPostulacion().compareTo(a.getFechaPostulacion()))
                .toList();
    }

    /**
     * Cambia el estado de una candidatura y lo guarda en la base de datos.
     *
     * @param oferta      candidatura a modificar
     * @param nuevoEstado estado nuevo
     * @throws RuntimeException si no se indica la candidatura o el estado
     */
    public void cambiarEstado(Oferta oferta, Estado nuevoEstado) {
        if (oferta == null || nuevoEstado == null) {
            throw new RuntimeException("Debe indicar la candidatura y el nuevo estado.");
        }
        Estado anterior = oferta.getEstado();
        oferta.setEstado(nuevoEstado);
        try {
            repository.update(oferta);
        } catch (RuntimeException e) {
            oferta.setEstado(anterior);
            throw e;
        }
        registrarCambioDeEstado(oferta, anterior);
    }

    /**
     * @param id id de la candidatura
     * @return la candidatura con ese id, o {@code null} si no existe o está borrada
     */
    public Oferta buscarPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    /**
     * Cuenta las candidaturas de un estado postuladas hasta una fecha.
     *
     * @param estado estado a contar, o {@code null} para todas
     * @param hasta  fecha de postulación máxima (incluida), o {@code null} para no limitar
     * @return cuántas candidaturas cumplen las dos condiciones
     */
    public int contar(Estado estado, LocalDate hasta) {
        int total = 0;
        for (Oferta oferta : repository.findAll()) {
            boolean mismoEstado = estado == null || oferta.getEstado() == estado;
            boolean dentroDeFecha = hasta == null || !oferta.getFechaPostulacion().isAfter(hasta);
            if (mismoEstado && dentroDeFecha) {
                total++;
            }
        }
        return total;
    }

    /**
     * Compara cuántas candidaturas hay hoy con cuántas había hace una semana.
     * Si hace una semana no había ninguna, se considera un 100 % de variación.
     *
     * @param estado estado a comparar, o {@code null} para todas
     * @return el porcentaje de variación (negativo si hay menos que la semana anterior)
     */
    public int variacionSemanal(Estado estado) {
        int actual = contar(estado, null);
        int semanaAnterior = contar(estado, LocalDate.now().minusWeeks(1));

        if (semanaAnterior == 0) {
            return actual == 0 ? 0 : 100;
        }
        return Math.round((actual - semanaAnterior) * 100f / semanaAnterior);
    }

    /**
     * @param cantidad cuántas candidaturas devolver como máximo
     * @return las últimas candidaturas por fecha de postulación, la más reciente primero
     */
    public List<Oferta> listarUltimas(int cantidad) {
        return repository.findAll().stream()
                .sorted((a, b) -> b.getFechaPostulacion().compareTo(a.getFechaPostulacion()))
                .limit(cantidad)
                .toList();
    }

    /**
     * Ordena las candidaturas por el campo indicado.
     *
     * @param ofertas    candidaturas a ordenar (no se modifica la lista recibida)
     * @param campo      "empresa", "titulo", "fecha", "estado" o "salario"
     * @param ascendente {@code true} de menor a mayor, {@code false} al revés
     * @return una lista nueva con las candidaturas ordenadas
     */
    public List<Oferta> ordenar(List<Oferta> ofertas, String campo, boolean ascendente) {
        List<Oferta> ordenadas = new ArrayList<>(ofertas);

        switch (campo) {
            case "empresa" -> ordenadas.sort((a, b) -> a.getEmpresa().getNombre().compareToIgnoreCase(b.getEmpresa().getNombre()));
            case "titulo" -> ordenadas.sort((a, b) -> a.getTitulo().compareToIgnoreCase(b.getTitulo()));
            case "fecha" -> ordenadas.sort((a, b) -> a.getFechaPostulacion().compareTo(b.getFechaPostulacion()));
            case "estado" -> ordenadas.sort((a, b) -> a.getEstado().toString().compareTo(b.getEstado().toString()));
            case "salario" -> ordenadas.sort((a, b) -> Double.compare(a.getSalario(), b.getSalario()));
        }

        if (!ascendente) {
            Collections.reverse(ordenadas);
        }
        return ordenadas;
    }

    /**
     * @return los nombres de las empresas que tienen candidaturas
     */
    public List<String> listarEmpresas() {
        return repository.findAll().stream()
                .map(c -> c.getEmpresa().getNombre())
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }
}
