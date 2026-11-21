package com.example.tracker.datos.DAO;

import java.util.List;

public interface DAO<T> {

    List<T> findAll();

    T save(T entity);
}
