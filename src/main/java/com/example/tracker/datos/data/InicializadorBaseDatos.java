package com.example.tracker.datos.data;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/** Crea las tablas de la base de datos a partir de {@code /database/schema.sql}. */
public class InicializadorBaseDatos {

    /**
     * Ejecuta el script de creación de tablas.
     *
     * @param dataSource origen de las conexiones a la base de datos
     */
    public static void initialize(DataSource dataSource) {
        executeScript(dataSource, "/database/schema.sql");
    }

    private static void executeScript(DataSource dataSource, String resourcePath) {
        InputStream inputStream = InicializadorBaseDatos.class.getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new RuntimeException("No se encontró el recurso " + resourcePath);
        }

        StringBuilder script = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                    continue;
                }
                script.append(line).append('\n');
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (Connection conn = dataSource.getConnection();
             Statement st = conn.createStatement()) {
            for (String statement : script.toString().split(";")) {
                if (!statement.isBlank()) {
                    st.execute(statement);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
