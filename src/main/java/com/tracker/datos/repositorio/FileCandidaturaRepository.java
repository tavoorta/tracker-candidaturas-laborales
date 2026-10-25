package com.tracker.datos.repositorio;

import com.tracker.datos.ayuda.ServicioConfiguracion;
import com.tracker.datos.enums.EstadoCandidatura;
import com.tracker.datos.modelo.Candidatura;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileCandidaturaRepository implements CandidaturaRepository {

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

    @Override
    public Candidatura guardar(Candidatura candidatura) {
        List<Candidatura> todas = buscarTodas();

        if (candidatura.getId() <= 0) {
            candidatura.setId(obtenerSiguienteId(todas));
        }

        todas.add(candidatura);
        escribirTodas(todas);
        return candidatura;
    }

    @Override
    public List<Candidatura> buscarTodas() {
        if (!Files.exists(rutaFichero)) {
            return new ArrayList<>();
        }
        try {
            List<Candidatura> candidaturas = new ArrayList<>();
            for (String linea : Files.readAllLines(rutaFichero, StandardCharsets.UTF_8)) {
                if (!linea.isBlank()) {
                    candidaturas.add(crearCandidaturaDeString(linea));
                }
            }
            return candidaturas;
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el fichero de candidaturas: " + rutaFichero, e);
        }
    }

    @Override
    public Optional<Candidatura> buscarPorId(int id) {
        // pediente logica para Eliminar
        return null;
    }

    @Override
    public void eliminarPorId(int id) {
        // pediente logica para Eliminar

    }

    public static String crearStringDeCandidatura(Candidatura c) {
        return c.getId() + ";" +
                c.getTitulo() + ";" +
                c.getEmpresa() + ";" +
                c.getEstado().name().toLowerCase() + ";" +
                c.getFechaPostulacion().format(DATE_FORMAT) + ";" +
                (c.getEnlace() == null ? "" : c.getEnlace()) + ";" +
                (c.getNotas() == null ? "" : c.getNotas());
    }

    //region Metodos privados

    private void escribirTodas(List<Candidatura> candidaturas) {
        try {
            List<String> lineas = candidaturas.stream()
                    .map(FileCandidaturaRepository::crearStringDeCandidatura)
                    .toList();
            Files.write(rutaFichero, lineas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo escribir en el fichero de candidaturas: " + rutaFichero, e);
        }
    }

    private int obtenerSiguienteId(List<Candidatura> candidaturas) {
        int maxId = 0;
        for (Candidatura c : candidaturas) {
            if (c.getId() > maxId) {
                maxId = c.getId();
            }
        }
        return maxId + 1;
    }

    //endregion Metodos privados
}
