package com.tracker.datos.ayuda;

import com.tracker.datos.modelo.Candidatura;
import com.tracker.datos.repositorio.FileCandidaturaRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Lee un fichero CSV externo y lo convierte en una lista de {@link Candidatura},
 * reutilizando el parseo de {@link FileCandidaturaRepository}.
 */
public class ImportadorCsv {

    /**
     * @param rutaCsv ruta del fichero CSV a importar
     * @return las candidaturas leídas del CSV (sin cabecera)
     * @throws RuntimeException si el fichero no existe o no se puede leer
     */
    public List<Candidatura> importarCsv(String rutaCsv) {
        Path ruta = Path.of(rutaCsv);
        if (!Files.exists(ruta)) {
            throw new RuntimeException("No se encontró el fichero CSV: " + rutaCsv);
        }

        try {
            List<String> lineas = Files.readAllLines(ruta, StandardCharsets.UTF_8);
            List<Candidatura> candidaturas = new ArrayList<>();

            for (String linea : lineas) {
                if (!linea.isBlank() && !esCabecera(linea)) {
                    candidaturas.add(FileCandidaturaRepository.crearCandidaturaDeString(linea));
                }
            }
            return candidaturas;
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el fichero CSV: " + rutaCsv, e);
        }
    }

    private boolean esCabecera(String linea) {
        return linea.toLowerCase().startsWith("id;titulo");
    }
}
