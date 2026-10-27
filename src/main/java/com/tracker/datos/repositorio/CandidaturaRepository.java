package com.tracker.datos.repositorio;

import com.tracker.datos.modelo.Candidatura;

import java.util.List;

public interface CandidaturaRepository {

    Candidatura guardar(Candidatura candidatura);

    List<Candidatura> buscarTodas();

    Candidatura buscarPorId(int id);

    void eliminarPorId(int id);
}
