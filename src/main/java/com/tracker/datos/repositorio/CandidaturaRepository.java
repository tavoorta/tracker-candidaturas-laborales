package com.tracker.datos.repositorio;

import com.tracker.datos.modelo.Candidatura;

import java.util.List;
import java.util.Optional;

public interface CandidaturaRepository {

    Candidatura guardar(Candidatura candidatura);

    List<Candidatura> buscarTodas();

    Optional<Candidatura> buscarPorId(int id);

    void eliminarPorId(int id);
}
