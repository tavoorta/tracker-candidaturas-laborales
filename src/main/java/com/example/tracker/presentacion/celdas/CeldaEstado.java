package com.example.tracker.presentacion.celdas;

import com.example.tracker.datos.enums.EstadoOferta;
import com.example.tracker.datos.modelo.Oferta;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;

/**
 * Celda de la columna "Estado": un ComboBox con forma de etiqueta de color.
 * Al elegir otro estado lo asigna a la fila, la selecciona y ejecuta la acción recibida.
 * Reutilizable en cualquier tabla de ofertas (listado, dashboard...).
 */
public class CeldaEstado extends TableCell<Oferta, EstadoOferta> {

    private final ComboBox<EstadoOferta> comboEstado;

    public CeldaEstado(EventHandler<ActionEvent> cambiarEstado) {
        comboEstado = new ComboBox<>(FXCollections.observableArrayList(EstadoOferta.values()));

        comboEstado.setOnAction(event -> {
            Oferta fila = getTableRow().getItem();
            EstadoOferta nuevoEstado = comboEstado.getValue();

            if (fila != null && nuevoEstado != null && nuevoEstado != fila.getEstado()) {
                fila.setEstado(nuevoEstado);
                getTableView().getSelectionModel().select(fila);
                cambiarEstado.handle(event);
                aplicarEstilo(nuevoEstado);
            }
        });
    }

    @Override
    protected void updateItem(EstadoOferta estado, boolean empty) {
        super.updateItem(estado, empty);
        if (empty || estado == null) {
            setGraphic(null);
            return;
        }
        comboEstado.setValue(estado);
        aplicarEstilo(estado);
        setGraphic(comboEstado);
    }

    private void aplicarEstilo(EstadoOferta estado) {
        comboEstado.setStyle(
                "-fx-background-color: " + colorFondo(estado) + ";"
                + "-fx-background-radius: 12;"
                + "-fx-mark-color: " + colorTexto(estado) + ";");
        comboEstado.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(EstadoOferta item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : etiqueta(item));
                setStyle("-fx-text-fill: " + colorTexto(estado) + "; -fx-font-weight: bold;");
            }
        });
    }

    //region Colores y etiqueta de cada estado
    private String etiqueta(EstadoOferta estado) {
        String nombre = estado.name().toLowerCase();
        return Character.toUpperCase(nombre.charAt(0)) + nombre.substring(1);
    }

    private String colorFondo(EstadoOferta estado) {
        return switch (estado) {
            case ENTREVISTA -> "#0F766E";
            case RECHAZADO -> "#FF8B78";
            case PENDIENTE -> "#F5B027";
            case ACEPTADO -> "#DCFCE7";
        };
    }

    private String colorTexto(EstadoOferta estado) {
        return switch (estado) {
            case ENTREVISTA -> "white";
            case RECHAZADO -> "#7A1D0F";
            case PENDIENTE -> "#4A3200";
            case ACEPTADO -> "#166534";
        };
    }
    //endregion Colores y etiqueta de cada estado
}
