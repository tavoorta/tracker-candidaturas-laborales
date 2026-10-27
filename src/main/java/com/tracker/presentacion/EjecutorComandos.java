package com.tracker.presentacion;

import com.tracker.negocio.ServicioCandidatura;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EjecutorComandos {

    private final ServicioCandidatura servicio;

    public void ejecutar(ComandoInterpretado comando) {
        switch (comando.getOperacion()) {
            case "-listar" -> listar(comando);
            case "-añadir" -> anadir(comando);
            case "-ver" -> ver(comando);
            case "-borrar" -> borrar(comando);
            case "-importar" -> importar(comando);
            default -> throw new IllegalArgumentException("Operación desconocida: " + comando.getOperacion());
        }
    }

    private void listar(ComandoInterpretado comando) {
        System.out.println("PENDIENTE DE IMPLEMENTAR");
    }

    private void anadir(ComandoInterpretado comando) {
        System.out.println("PENDIENTE DE IMPLEMENTAR");
    }

    private void ver(ComandoInterpretado comando) {
        System.out.println("PENDIENTE DE IMPLEMENTAR");
    }

    private void borrar(ComandoInterpretado comando) {
        int id = obtenerId(comando.getArgumentoPosicional());
        servicio.borrar(id);
        System.out.println("Candidatura " + id + " eliminada correctamente.");
    }

    private int obtenerId(String crudo) {
        System.out.println("PENDIENTE DE IMPLEMENTAR");
        return 0;
    }

    private String valorOGuion(String valor) {
        return (valor == null || valor.isBlank()) ? "-" : valor;
    }


    private void importar(ComandoInterpretado comando) {
        if (comando.getArgumentoPosicional() == null) {
            throw new IllegalArgumentException("Debes indicar la ruta del fichero CSV a importar.");
        }
        int importadas = servicio.importar(comando.getArgumentoPosicional());
        System.out.println(importadas + " candidatura(s) importada(s) correctamente.");
    }


}
