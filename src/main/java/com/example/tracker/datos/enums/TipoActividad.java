package com.example.tracker.datos.enums;

/** Tipo de evento que se registra en la actividad reciente. */
public enum TipoActividad {

    CONTACTO("Nuevo contacto"),
    CAMBIO_ESTADO("Cambio de estado");

    private final String etiqueta;

    TipoActividad(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
