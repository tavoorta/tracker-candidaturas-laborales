package com.example.tracker.negocio;

import com.example.tracker.datos.DAO.DAO;
import com.example.tracker.datos.data.ImportadorCsv;
import com.example.tracker.datos.enums.EstadoOferta;
import com.example.tracker.datos.modelo.Oferta;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

/**
 * Lógica de negocio de las candidaturas: validaciones, filtrado y conversión
 * de los datos de entrada a los tipos del modelo. No sabe de dónde vienen ni
 * a dónde van los datos, eso lo resuelve {@link DAO}.
 */
@RequiredArgsConstructor
public class OfertaCandidatura {

    private final DAO<Oferta> repository;
    //private final ImportadorCsv importadorCsv;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /*
    public int importar(String rutaCsv) {
        List<Oferta> ofertas = importadorCsv.importarCsv(rutaCsv);
        for (Oferta c : ofertas) {
            c.setId(0);
            repository.guardar(c);
        }
        return ofertas.size();
    }

    public void borrar(int id) {
        Oferta oferta = repository.buscarPorId(id);
        if (oferta == null) {
            throw new RuntimeException("No existe ninguna oferta con ID " + id);
        }
        repository.eliminarPorId(id);
    }
    */

    /**
     * @param estado  nombre del estado a filtrar (ignora mayúsculas/minúsculas), o {@code null}
     * @param empresa nombre exacto de la empresa a filtrar, o {@code null}
     * @param texto   texto a buscar en el título o en el nombre de empresa, o {@code null}
     * @return las candidaturas que cumplen los filtros indicados
     */
    public List<Oferta> filtrar(String estado, String empresa, String texto) {
        String busqueda = texto == null ? "" : texto.trim().toLowerCase();

        return repository.findAll().stream()
                .filter(c -> estado == null || c.getEstado().name().equalsIgnoreCase(estado))
                .filter(c -> empresa == null || c.getEmpresa().getNombre().equalsIgnoreCase(empresa))
                .filter(c -> busqueda.isEmpty()
                        || c.getTitulo().toLowerCase().contains(busqueda)
                        || c.getEmpresa().getNombre().toLowerCase().contains(busqueda))
                .toList();
    }

    /**
     * @return los nombres de las empresas que tienen candidaturas, sin repetir y en orden alfabético
     */
    public List<String> listarEmpresas() {
        return repository.findAll().stream()
                .map(c -> c.getEmpresa().getNombre())
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    /*
    public Oferta anadir(String titulo, String empresa, String estado, String fecha,
                         String enlace, String notas) {
        validarObligatorio(titulo, "titulo");
        validarObligatorio(empresa, "empresa");
        validarObligatorio(estado, "estado");
        validarObligatorio(fecha, "fecha");
        validarSinSeparador(titulo, "titulo");
        validarSinSeparador(empresa, "empresa");
        validarSinSeparador(enlace, "enlace");
        validarSinSeparador(notas, "notas");

        EstadoOferta estadoOferta = parsearEstado(estado);
        LocalDate fechaPostulacion = parsearFecha(fecha);

        Oferta oferta = new Oferta(0, titulo, empresa, estadoOferta,
                fechaPostulacion, enlace, notas);
        return repository.guardar(oferta);
    }

    public Oferta ver(int id) {
        Oferta oferta = repository.buscarPorId(id);
        if (oferta == null) {
            throw new RuntimeException("No existe ninguna oferta con ID " + id);
        }
        return oferta;
    }
    */

    //region Metodos privados de validación y parseo

    private void validarObligatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new RuntimeException("El campo '" + campo + "' es obligatorio.");
        }
    }

    private void validarSinSeparador(String valor, String campo) {
        if (valor != null && valor.contains(";")) {
            throw new RuntimeException("El campo '" + campo + "' no puede contener el carácter ';'.");
        }
    }

    private EstadoOferta parsearEstado(String estado) {
        try {
            return EstadoOferta.valueOf(estado.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado no válido: '" + estado
                    + "'. Valores permitidos: " + Arrays.toString(EstadoOferta.values()));
        }
    }

    private LocalDate parsearFecha(String fecha) {
        try {
            return LocalDate.parse(fecha, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Formato de fecha inválido: '" + fecha + "'. Usa dd/MM/yyyy.");
        }
    }

    //endregion Metodos privados de validación y parseo

}
