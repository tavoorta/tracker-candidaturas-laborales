package com.tracker.negocio;

import com.tracker.datos.ayuda.ImportadorCsv;
import com.tracker.datos.modelo.Candidatura;
import com.tracker.datos.repositorio.CandidaturaRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ServicioCandidatura {

    private final CandidaturaRepository repository;
    private final ImportadorCsv importadorCsv;

    public int importar(String rutaCsv) {
        List<Candidatura> candidaturas = importadorCsv.importarCsv(rutaCsv);
        for (Candidatura c : candidaturas) {
            c.setId(0);
            repository.guardar(c);
        }
        return candidaturas.size();
    }

    public void borrar(int id) {
        Candidatura candidatura = repository.buscarPorId(id);
        if (candidatura == null) {
            throw new RuntimeException("No existe ninguna candidatura con ID " + id);
        }
        repository.eliminarPorId(id);
    }

}
