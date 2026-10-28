package com.tracker.negocio;

import com.tracker.datos.ayuda.ImportadorCsv;
import com.tracker.datos.enums.EstadoCandidatura;
import com.tracker.datos.modelo.Candidatura;
import com.tracker.datos.repositorio.CandidaturaRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class ServicioCandidatura {

    private final CandidaturaRepository repository;
    private final ImportadorCsv importadorCsv;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public int importar(String rutaCsv) {
        List<Candidatura> candidaturas = importadorCsv.importarCsv(rutaCsv);
        for (Candidatura c : candidaturas) {
            c.setId(0);
            repository.guardar(c);
        }
        return candidaturas.size();
    }

    public void borrar(int id) {
        Candidatura candidatura = repository.buscarPorId(id);
        if (candidatura == null) {
            throw new RuntimeException("No existe ninguna candidatura con ID " + id);
        }
        repository.eliminarPorId(id);
    }

    public List<Candidatura> filtrar(String estado, String empresa) {
        return repository.buscarTodas().stream()
                .filter(c -> estado == null || c.getEstado().name().equalsIgnoreCase(estado))
                .filter(c -> empresa == null || c.getEmpresa().toLowerCase().contains(empresa.toLowerCase()))
                .toList();
    }

    public Candidatura anadir(String titulo, String empresa, String estado, String fecha,
                              String enlace, String notas) {
        validarObligatorio(titulo, "titulo");
        validarObligatorio(empresa, "empresa");
        validarObligatorio(estado, "estado");
        validarObligatorio(fecha, "fecha");
        validarSinSeparador(titulo, "titulo");
        validarSinSeparador(empresa, "empresa");
        validarSinSeparador(enlace, "enlace");
        validarSinSeparador(notas, "notas");

        EstadoCandidatura estadoCandidatura = parsearEstado(estado);
        LocalDate fechaPostulacion = parsearFecha(fecha);

        Candidatura candidatura = new Candidatura(0, titulo, empresa, estadoCandidatura,
                fechaPostulacion, enlace, notas);
        return repository.guardar(candidatura);
    }

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

    private EstadoCandidatura parsearEstado(String estado) {
        try {
            return EstadoCandidatura.valueOf(estado.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado no válido: '" + estado
                    + "'. Valores permitidos: " + Arrays.toString(EstadoCandidatura.values()));
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
