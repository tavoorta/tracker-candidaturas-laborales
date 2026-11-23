package com.example.tracker.negocio;

import com.example.tracker.datos.DAO.DAO;
import com.example.tracker.datos.enums.Estado;
import com.example.tracker.datos.enums.TipoActividad;
import com.example.tracker.datos.modelo.Actividad;
import com.example.tracker.datos.modelo.Oferta;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Registro de lo que va pasando en la aplicación (cambios de estado y, en v3, contactos nuevos),
 * para mostrarlo en "Actividad reciente" del Dashboard.
 */
@RequiredArgsConstructor
public class ServicioActividad {

    private final DAO<Actividad> repository;

    /**
     * Guarda una actividad con la fecha y hora actuales.
     *
     * @param tipo        tipo de actividad
     * @param estado      estado al que pasó la candidatura, o {@code null} si no es un cambio de estado
     * @param descripcion texto que se muestra en el Dashboard
     * @param oferta      candidatura relacionada, o {@code null} si no tiene
     */
    public void registrar(TipoActividad tipo, Estado estado, String descripcion, Oferta oferta) {
        Actividad actividad = new Actividad();
        actividad.setTipo(tipo);
        actividad.setEstado(estado);
        actividad.setDescripcion(descripcion);
        actividad.setOferta(oferta);
        actividad.setFecha(LocalDateTime.now());
        repository.save(actividad);
    }

    /**
     * @param cantidad cuántas actividades devolver como máximo
     * @return las últimas actividades, la más reciente primero
     */
    public List<Actividad> listarRecientes(int cantidad) {
        return repository.findAll().stream()
                .sorted((a, b) -> b.getFecha().compareTo(a.getFecha()))
                .limit(cantidad)
                .toList();
    }
}
