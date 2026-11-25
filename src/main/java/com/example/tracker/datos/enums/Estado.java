package com.example.tracker.datos.enums;

/**
 * Estado de una candidatura. También se usa como resultado de una entrevista y
 * en la actividad reciente.
 */
public enum Estado {

    PENDIENTE("Pendiente"),
    ENTREVISTA("Entrevista"),
    RECHAZADO("Rechazado"),
    ACEPTADO("Aceptado");

    private final String etiqueta;

    Estado(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
