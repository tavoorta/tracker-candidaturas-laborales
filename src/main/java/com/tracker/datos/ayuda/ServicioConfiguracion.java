package com.tracker.datos.ayuda;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/** Lee la configuración de la aplicación desde {@code app.properties}. */
public class ServicioConfiguracion {

    private final Properties propiedades = new Properties();

    /** @throws RuntimeException si no se encuentra o no se puede leer app.properties */
    public ServicioConfiguracion() {
        try (var lector = new FileReader("app.properties")) {
            propiedades.load(lector);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar el fichero de configuración app.properties", e);
        }
    }

    /**
     * @param clave nombre de la propiedad
     * @return el valor de la propiedad, o {@code null} si no existe
     */
    public String obtenerPropiedad(String clave) {
        return propiedades.getProperty(clave);
    }

}
