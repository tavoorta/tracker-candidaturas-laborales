package com.tracker;

import com.tracker.datos.ayuda.ImportadorCsv;
import com.tracker.datos.ayuda.ServicioConfiguracion;
import com.tracker.datos.repositorio.CandidaturaRepository;
import com.tracker.datos.repositorio.FileCandidaturaRepository;
import com.tracker.negocio.ServicioCandidatura;
import com.tracker.presentacion.ComandoInterpretado;
import com.tracker.presentacion.EjecutorComandos;

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
