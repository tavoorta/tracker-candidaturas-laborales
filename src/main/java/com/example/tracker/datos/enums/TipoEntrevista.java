package com.example.tracker.datos.enums;

public enum TipoEntrevista {

    RRHH("Entrevista RRHH"),
    TECNICA("Entrevista técnica"),
    PRUEBA_TECNICA("Prueba técnica"),
    FINAL("Entrevista final");

    private final String etiqueta;

    TipoEntrevista(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
