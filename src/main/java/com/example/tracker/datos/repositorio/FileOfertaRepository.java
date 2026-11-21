/*
package com.example.tracker.datos.repositorio;

import com.example.tracker.datos.data.ServicioConfiguracion;
import com.example.tracker.datos.enums.EstadoOferta;
import com.example.tracker.datos.modelo.Oferta;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

*/
/**
 * Implementación de {@link OfertaRepository} que persiste las candidaturas
 * en un fichero de texto plano, con los campos separados por {@code ;}.
 *//*

public class FileOfertaRepository implements OfertaRepository {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Path rutaFichero;

    */
/** @param configuracion servicio del que se lee la propiedad {@code fichero} *//*

    public FileOfertaRepository(ServicioConfiguracion configuracion) {
        this.rutaFichero = Path.of(configuracion.obtenerPropiedad("fichero"));
    }

    public static Oferta crearCandidaturaDeString(String linea) {
        String[] campos = linea.split(";", -1);
        if (campos.length < 7) {
            throw new RuntimeException("Línea con formato inválido, se esperan 7 campos: " + linea);
        }
        try {
            return new Oferta(
                    Integer.parseInt(campos[0].trim()),
                    campos[1].trim(),
                    campos[2].trim(),
                    EstadoOferta.valueOf(campos[3].trim().toUpperCase()),
                    LocalDate.parse(campos[4].trim(), DATE_FORMAT),
                    campos[5].trim(),
                    campos[6].trim()
            );
        } catch (IllegalArgumentException | DateTimeParseException e) {
            throw new RuntimeException("Línea con datos inválidos (id, estado o fecha dd/MM/yyyy): " + linea);
        }
    }

    @Override
    public Oferta guardar(Oferta oferta) {
        List<Oferta> todas = buscarTodas();

        if (oferta.getId() <= 0) {
            oferta.setId(obtenerSiguienteId(todas));
        }

        todas.add(oferta);
        escribirTodas(todas);
        return oferta;
    }

    @Override
    public List<Oferta> buscarTodas() {
        if (!Files.exists(rutaFichero)) {
            return new ArrayList<>();
        }
        try {
            List<Oferta> ofertas = new ArrayList<>();
            for (String linea : Files.readAllLines(rutaFichero, StandardCharsets.UTF_8)) {
                if (!linea.isBlank()) {
                    ofertas.add(crearCandidaturaDeString(linea));
                }
            }
            return ofertas;
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el fichero de candidaturas: " + rutaFichero, e);
        }
    }

    @Override
    public Oferta buscarPorId(int id) {
        for (Oferta c : buscarTodas()) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    @Override
    public void eliminarPorId(int id) {
        List<Oferta> todas = buscarTodas();
        boolean eliminado = todas.removeIf(c -> c.getId() == id);
        if (!eliminado) {
            throw new RuntimeException("No existe ninguna candidatura con ID " + id);
        }
        escribirTodas(todas);
    }

    public static String crearStringDeCandidatura(Oferta c) {
        return c.getId() + ";" +
                c.getTitulo() + ";" +
                c.getEmpresa() + ";" +
                c.getEstado().name().toLowerCase() + ";" +
                c.getFechaPostulacion().format(DATE_FORMAT) + ";" +
                (c.getEnlace() == null ? "" : c.getEnlace()) + ";" +
                (c.getNotas() == null ? "" : c.getNotas());
    }

    //region Metodos privados

    private void escribirTodas(List<Oferta> ofertas) {
        try {
            List<String> lineas = ofertas.stream()
                    .map(FileOfertaRepository::crearStringDeCandidatura)
                    .toList();
            Files.write(rutaFichero, lineas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo escribir en el fichero de ofertas: " + rutaFichero, e);
        }
    }

    private int obtenerSiguienteId(List<Oferta> ofertas) {
        int maxId = 0;
        for (Oferta c : ofertas) {
            if (c.getId() > maxId) {
                maxId = c.getId();
            }
        }
        return maxId + 1;
    }

    //endregion Metodos privados
}
*/
