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

    public static AppContext getInstance() {
        return INSTANCE;
    }

    public ObservableMap<String, Object> getDatos() {
        return datos;
    }

    public Object get(String key) {
        return datos.get(key);
    }

    public void put(String key, Object value) {
        datos.put(key, value);
    }
}
