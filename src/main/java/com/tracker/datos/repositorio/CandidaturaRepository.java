package com.tracker.datos.repositorio;

import com.tracker.datos.modelo.Candidatura;

import java.util.List;

/**
 * Contrato de acceso a datos para {@link Candidatura}. Permite que en v2.0/v3.0
 * se sustituya la implementación en fichero por JDBC o Hibernate sin tocar
 * las capas {@code negocio} ni {@code presentacion}.
 */
public interface CandidaturaRepository {

    Candidatura guardar(Candidatura candidatura);

    List<Candidatura> buscarTodas();

    Candidatura buscarPorId(int id);

    void eliminarPorId(int id);
}
