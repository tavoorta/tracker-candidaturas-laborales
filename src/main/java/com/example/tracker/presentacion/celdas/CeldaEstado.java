package com.example.tracker.presentacion.celdas;

import com.example.tracker.datos.enums.Estado;
import com.example.tracker.datos.modelo.Oferta;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;

/**
 * Celda de la columna "Estado": un ComboBox con forma de etiqueta de color.
 * Al elegir otro estado selecciona la fila y ejecuta la acción recibida, que es la que
 * guarda el cambio. Sirve para cualquier tabla de ofertas.
 */
public class CeldaEstado extends TableCell<Oferta, Estado> {

    private final ComboBox<Estado> comboEstado;

    /**
     * @param cambiarEstado acción que se ejecuta al elegir otro estado, con la fila ya seleccionada
     */
    public CeldaEstado(EventHandler<ActionEvent> cambiarEstado) {
        comboEstado = new ComboBox<>(FXCollections.observableArrayList(Estado.values()));

        comboEstado.setOnAction(event -> {
            Oferta fila = getTableRow().getItem();
            Estado nuevoEstado = comboEstado.getValue();

            if (fila != null && nuevoEstado != null && nuevoEstado != fila.getEstado()) {
                getTableView().getSelectionModel().select(fila);
                cambiarEstado.handle(event);
            }
        });
    }

    @Override
    protected void updateItem(Estado estado, boolean empty) {
        super.updateItem(estado, empty);
        if (empty || estado == null) {
            setGraphic(null);
            return;
        }
        comboEstado.setValue(estado);
        aplicarEstilo(estado);
        setGraphic(comboEstado);
    }

    private void aplicarEstilo(Estado estado) {
        comboEstado.setStyle(
                "-fx-background-color: " + colorFondo(estado) + ";"
                + "-fx-background-radius: 12;"
                + "-fx-mark-color: " + colorTexto(estado) + ";");
        comboEstado.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Estado item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
                setStyle("-fx-text-fill: " + colorTexto(estado) + "; -fx-font-weight: bold;");
            }
        });
    }

    //region Colores de cada estado
    /**
     * @param estado estado de la candidatura
     * @return color de fondo de la etiqueta del estado
     */
    public static String colorFondo(Estado estado) {
        return switch (estado) {
            case ENTREVISTA -> "#0F766E";
            case RECHAZADO -> "#FF8B78";
            case PENDIENTE -> "#F5B027";
            case ACEPTADO -> "#DCFCE7";
        };
    }

    /**
     * @param estado estado de la candidatura
     * @return color del texto de la etiqueta del estado
     */
    public static String colorTexto(Estado estado) {
        return switch (estado) {
            case ENTREVISTA -> "white";
            case RECHAZADO -> "#7A1D0F";
            case PENDIENTE -> "#4A3200";
            case ACEPTADO -> "#166534";
        };
    }
    //endregion Colores de cada estado
}
