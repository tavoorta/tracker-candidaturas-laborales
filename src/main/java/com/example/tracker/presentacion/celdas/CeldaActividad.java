package com.example.tracker.presentacion.celdas;

import com.example.tracker.datos.enums.TipoActividad;
import com.example.tracker.datos.modelo.Actividad;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Celda de la columna de icono de "Actividad reciente": un contacto nuevo lleva el
 * icono de persona y un cambio de estado, el icono del estado al que pasó la candidatura.
 */
public class CeldaActividad extends TableCell<Actividad, Actividad> {

    private final ImageView icono;

    /** Crea la celda con el icono vacío. */
    public CeldaActividad() {
        icono = new ImageView();
        icono.setFitWidth(20.0);
        icono.setFitHeight(20.0);
    }

    @Override
    protected void updateItem(Actividad actividad, boolean empty) {
        super.updateItem(actividad, empty);
        if (empty || actividad == null) {
            setGraphic(null);
            return;
        }
        icono.setImage(new Image(getClass().getResourceAsStream(rutaIcono(actividad))));
        setGraphic(icono);
    }

    private String rutaIcono(Actividad actividad) {
        if (actividad.getTipo() == TipoActividad.CONTACTO) {
            return "/images/icon-actividad-contacto.png";
        }
        return switch (actividad.getEstado()) {
            case PENDIENTE -> "/images/icon-pendientes.png";
            case ENTREVISTA -> "/images/icon-entrevistas.png";
            case RECHAZADO -> "/images/icon-actividad-rechazo.png";
            case ACEPTADO -> "/images/icon-entrevistas-circle-check.png";
        };
    }
}
