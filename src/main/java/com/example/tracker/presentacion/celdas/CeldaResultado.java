package com.example.tracker.presentacion.celdas;

import com.example.tracker.datos.enums.Estado;
import com.example.tracker.datos.modelo.Entrevista;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Celda de la columna "Resultado" del historial de entrevistas: un icono
 * según el resultado y su texto.
 */
public class CeldaResultado extends TableCell<Entrevista, Estado> {

    private final ImageView icono;

    public CeldaResultado() {
        icono = new ImageView();
        icono.setFitWidth(18.0);
        icono.setFitHeight(18.0);
    }

    @Override
    protected void updateItem(Estado resultado, boolean empty) {
        super.updateItem(resultado, empty);
        if (empty || resultado == null) {
            setText(null);
            setGraphic(null);
            return;
        }
        icono.setImage(new Image(getClass().getResourceAsStream(rutaIcono(resultado))));
        setText(resultado.toString());
        setGraphic(icono);
    }

    private String rutaIcono(Estado resultado) {
        return switch (resultado) {
            case ACEPTADO -> "/images/icon-entrevistas-circle-check.png";
            case RECHAZADO -> "/images/icon-actividad-rechazo.png";
            case PENDIENTE, ENTREVISTA -> "/images/icon-entrevistas-circle-clock.png";
        };
    }
}
