package com.tracker.presentacion;

import com.tracker.datos.modelo.Candidatura;
import com.tracker.negocio.ServicioCandidatura;
import lombok.RequiredArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class EjecutorComandos {

    private final ServicioCandidatura servicio;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
        Map<String, String> opciones = comando.getOpciones();
        List<Candidatura> resultado = servicio.filtrar(opciones.get("estado"), opciones.get("empresa"));

        if (resultado.isEmpty()) {
            System.out.println("No hay candidaturas que coincidan con los filtros indicados.");
            return;
        }

        System.out.println("ID    TITULO    EMPRESA    ESTADO    FECHA");
        for (Candidatura c : resultado) {
            System.out.println(c.getId() + "  " + c.getTitulo() + "  " + c.getEmpresa() + "  "
                    + c.getEstado().name().toLowerCase() + "  " + c.getFechaPostulacion().format(DATE_FORMAT));
        }
        System.out.println();
        System.out.println("Total de candidaturas encontradas: " + resultado.size());
    }

    private void anadir(ComandoInterpretado comando) {
        Map<String, String> opciones = comando.getOpciones();
        Candidatura nueva = servicio.anadir(
                opciones.get("titulo"),
                opciones.get("empresa"),
                opciones.get("estado"),
                opciones.get("fecha"),
                opciones.get("enlace"),
                opciones.get("notas")
        );
        System.out.println("Candidatura añadida correctamente con ID " + nueva.getId());
    }

    private void ver(ComandoInterpretado comando) {
        int id = obtenerId(comando.getArgumentoPosicional());
        Candidatura c = servicio.ver(id);
        System.out.println("ID:                " + c.getId());
        System.out.println("Titulo:            " + c.getTitulo());
        System.out.println("Empresa:           " + c.getEmpresa());
        System.out.println("Estado:            " + c.getEstado().name().toLowerCase());
        System.out.println("Fecha postulacion: " + c.getFechaPostulacion().format(DATE_FORMAT));
        System.out.println("Enlace:            " + valorOGuion(c.getEnlace()));
        System.out.println("Notas:             " + valorOGuion(c.getNotas()));
    }


    private void borrar(ComandoInterpretado comando) {
        int id = obtenerId(comando.getArgumentoPosicional());
        servicio.borrar(id);
        System.out.println("Candidatura " + id + " eliminada correctamente.");
    }

    private int obtenerId(String crudo) {
        if (crudo == null) {
            throw new IllegalArgumentException("Debes indicar el ID de la candidatura.");
        }
        try {
            return Integer.parseInt(crudo);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El ID debe ser un número entero: " + crudo);
        }
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
