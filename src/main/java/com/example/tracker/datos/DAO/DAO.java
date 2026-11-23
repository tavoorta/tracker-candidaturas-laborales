package com.example.tracker.datos.DAO;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface DAO<T> {

    Optional<T> findById(Long id);

    List<T> findAll();

    T save(T entity);

    void update(T entity);

    void deleteById(Long id);

    void deleteById(Connection conn, Long id);
}
