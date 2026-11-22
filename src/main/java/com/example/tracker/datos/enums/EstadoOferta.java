package com.example.tracker.datos.enums;

public enum EstadoOferta {

    PENDIENTE("Pendiente"),
    ENTREVISTA("Entrevista"),
    RECHAZADO("Rechazado"),
    ACEPTADO("Aceptado");

    private final String etiqueta;

    EstadoOferta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
