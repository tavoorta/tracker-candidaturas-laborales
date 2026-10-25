package com.tracker.datos.repositorio;

import com.tracker.datos.ayuda.ServicioConfiguracion;
import com.tracker.datos.enums.EstadoCandidatura;
import com.tracker.datos.modelo.Candidatura;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileCandidaturaRepository {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Path rutaFichero;

    public FileCandidaturaRepository(ServicioConfiguracion configuracion) {
        this.rutaFichero = Path.of(configuracion.obtenerPropiedad("fichero"));
    }

    public static Candidatura crearCandidaturaDeString(String linea) {
        String[] campos = linea.split(";", -1);
        if (campos.length < 7) {
            throw new RuntimeException("Línea con formato inválido, se esperan 7 campos: " + linea);
        }
        try {
            return new Candidatura(
                    Integer.parseInt(campos[0].trim()),
                    campos[1].trim(),
                    campos[2].trim(),
                    EstadoCandidatura.valueOf(campos[3].trim().toUpperCase()),
                    LocalDate.parse(campos[4].trim(), DATE_FORMAT),
                    campos[5].trim(),
                    campos[6].trim()
            );
        } catch (IllegalArgumentException | DateTimeParseException e) {
            throw new RuntimeException("Línea con datos inválidos (id, estado o fecha dd/MM/yyyy): " + linea);
        }
    }

    public Candidatura guardar(Candidatura candidatura) {
        // pediente logica para guardar
        return candidatura;
    }

    public List<Candidatura> buscarTodas() {

        // pediente logica para buscar todas
        List<Candidatura> candidaturas = new ArrayList<>();
        return candidaturas;
    }

    public Optional<Candidatura> buscarPorId(int id) {
        // pediente logica para Eliminar
        return null;
    }

    public void eliminarPorId(int id) {
        // pediente logica para Eliminar

    }

}
