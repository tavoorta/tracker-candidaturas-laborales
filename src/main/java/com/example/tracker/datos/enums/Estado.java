package com.example.tracker.datos.enums;

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
