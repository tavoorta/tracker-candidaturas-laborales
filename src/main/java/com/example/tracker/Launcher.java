package com.example.tracker;

import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        Application.launch(HelloApplication.class, args);
    }
}

/*
public class Main {

    static void main(String[] args) {

        try {
            ServicioConfiguracion configuracion = new ServicioConfiguracion();
            CandidaturaRepository repository = new FileCandidaturaRepository(configuracion);

            ImportadorCsv importadorCsv = new ImportadorCsv();
            ServicioCandidatura servicio = new ServicioCandidatura(repository, importadorCsv);
            EjecutorComandos ejecutor = new EjecutorComandos(servicio);

            ComandoInterpretado comando = ComandoInterpretado.analizar(args);
            ejecutor.ejecutar(comando);

        } catch (RuntimeException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}
*/