package com.tracker.datos.ayuda;

import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Crea y guarda el único {@link DataSource} de la aplicación, apuntando al
 * archivo SQLite indicado en {@code configDB.properties}.
 */
public class ConexionBaseDatos {

    private static DataSource dataSource;

    public static DataSource getDataSource() {

        if (dataSource == null) {
            String url = leerUrlDesdeConfig();

            SQLiteDataSource ds = new SQLiteDataSource();
            ds.setUrl(url);
            dataSource = ds;
        }

        return dataSource;
    }

    private static String leerUrlDesdeConfig() {

        Properties propiedades = new Properties();

        try (var lector = new FileReader("configDB.properties")) {
            propiedades.load(lector);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar el fichero de configuración configDB.properties", e);
        }

        String url = propiedades.getProperty("db.url");
        if (url == null || url.isBlank()) {
            throw new RuntimeException("Falta la propiedad 'db.url' en configDB.properties");
        }
        return url;
    }
}
