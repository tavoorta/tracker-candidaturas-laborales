package com.tracker.datos.ayuda;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ServicioConfiguracion {

    private final Properties propiedades = new Properties();

    public ServicioConfiguracion() {
        try (var lector = new FileReader("app.properties")) {
            propiedades.load(lector);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar el fichero de configuración app.properties", e);
        }
    }

    public String obtenerPropiedad(String clave) {
        return propiedades.getProperty(clave);
    }

}
