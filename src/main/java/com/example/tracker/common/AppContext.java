package com.example.tracker.common;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * Datos compartidos entre pantallas (singleton). Por ejemplo, la oferta que se
 * va a editar en el formulario.
 */
public class AppContext {

    private static final AppContext INSTANCE = new AppContext();

    private final ObservableMap<String, Object> datos = FXCollections.observableHashMap();

    private AppContext() {
    }

    /**
     * @return la única instancia del contexto
     */
    public static AppContext getInstance() {
        return INSTANCE;
    }

    /**
     * @return el mapa observable con los datos compartidos
     */
    public ObservableMap<String, Object> getDatos() {
        return datos;
    }

    /**
     * @param key clave del dato
     * @return el valor guardado, o {@code null} si no existe
     */
    public Object get(String key) {
        return datos.get(key);
    }

    /**
     * @param key   clave del dato
     * @param value valor a guardar
     */
    public void put(String key, Object value) {
        datos.put(key, value);
    }
}
