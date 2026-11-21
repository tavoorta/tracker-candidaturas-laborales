package com.example.tracker;

import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        Application.launch(App.class, args);
    }
}

/*
public class Main {

    static void main(String[] args) {

        try {
            ServicioConfiguracion configuracion = new ServicioConfiguracion();
            OfertaRepository repository = new FileOfertaRepository(configuracion);

            ImportadorCsv importadorCsv = new ImportadorCsv();
            OfertaCandidatura servicio = new OfertaCandidatura(repository, importadorCsv);
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