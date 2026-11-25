package com.example.tracker.datos.DAO;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Contrato genérico de acceso a datos: cada tabla tiene un DAO que lo implementa.
 *
 * @param <T> modelo que maneja el DAO
 */
public interface DAO<T> {

    /**
     * @param id id del registro
     * @return el registro activo con ese id, o vacío si no existe
     */
    Optional<T> findById(Long id);

    /**
     * @return todos los registros activos
     */
    List<T> findAll();

    /**
     * Inserta el registro y le asigna el id generado.
     *
     * @param entity registro a guardar
     * @return el mismo registro con su id
     */
    T save(T entity);

    /**
     * Actualiza el registro que tiene el id de la entidad.
     *
     * @param entity registro con los datos nuevos
     */
    void update(T entity);

    /**
     * Borrado lógico: marca el registro con Active = 0.
     *
     * @param id id del registro
     */
    void deleteById(Long id);

    /**
     * Borrado lógico usando la conexión de una transacción ya abierta.
     *
     * @param conn conexión de la transacción
     * @param id   id del registro
     */
    void deleteById(Connection conn, Long id);
}
