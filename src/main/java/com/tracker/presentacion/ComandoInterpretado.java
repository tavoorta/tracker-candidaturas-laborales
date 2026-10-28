package com.tracker.presentacion;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Datos de un comando ya interpretado (operación, argumento posicional y
 * opciones {@code --flag valor}) y la lógica que los extrae de {@code args}.
 */
@Getter
@AllArgsConstructor
public class ComandoInterpretado {
    private final String operacion;
    private final String argumentoPosicional;
    private final Map<String, String> opciones;

    public static ComandoInterpretado analizar(String[] argumentos) {
        if (argumentos.length == 0) {
            throw new IllegalArgumentException(
                    "Debes indicar una operación: -listar, -añadir, -ver, -borrar o -importar");
        }

        String operacion = argumentos[0];
        String argumentoPosicional = null;
        Map<String, String> opciones = new LinkedHashMap<>();

        int i = 1;
        if (i < argumentos.length && !argumentos[i].startsWith("--")) {
            argumentoPosicional = argumentos[i];
            i++;
        }

        while (i < argumentos.length) {
            String argumento = argumentos[i];
            if (!argumento.startsWith("--")) {
                throw new IllegalArgumentException("Argumento inesperado: " + argumento);
            }
            String clave = argumento.substring(2);
            if (i + 1 >= argumentos.length) {
                throw new IllegalArgumentException("Falta el valor para el parámetro --" + clave);
            }
            opciones.put(clave, argumentos[i + 1]);
            i += 2;
        }

        return new ComandoInterpretado(operacion, argumentoPosicional, opciones);
    }
}
