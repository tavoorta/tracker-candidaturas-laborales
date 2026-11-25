package com.example.tracker.datos.data;

import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Crea y guarda el único {@link DataSource} de la aplicación, apuntando al
 * archivo SQLite indicado en la propiedad {@code db.url} de {@code app.properties}.
 */
public class DataSourceFactory {

    private static final String archivoConfiguracion = "app.properties";

    private static DataSource dataSource = null;

    /**
     * @return el DataSource de la aplicación (se crea la primera vez que se pide)
     */
    public static DataSource getDataSource() {
        if (dataSource == null) {
            String url = leerUrl();

            SQLiteDataSource ds = new SQLiteDataSource();
            ds.setUrl(url);
            ds.setEnforceForeignKeys(true);
            dataSource = ds;
        }
        return dataSource;
    }

    private static String leerUrl() {
        Properties propiedades = new Properties();

        try (var lector = new FileReader(archivoConfiguracion)) {
            propiedades.load(lector);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar el fichero de configuración " + archivoConfiguracion, e);
        }

        String url = propiedades.getProperty("db.url");
        if (url == null || url.isBlank()) {
            throw new RuntimeException("Falta la propiedad 'db.url' en " + archivoConfiguracion);
        }
        return url;
    }
}
