package com.example.tracker.presentacion.celdas;

import com.example.tracker.datos.enums.EstadoOferta;
import com.example.tracker.datos.modelo.Oferta;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * Crea la celda de la columna "Estado": un ComboBox con forma de badge de
 * color, reutilizable por cualquier tabla de ofertas (listado, dashboard...).
 * Una celda de tabla cambia por fila según el dato y el FXML es estático,
 * por eso el cellFactory se construye en Java.
 */
public class FabricaCeldaEstado implements Callback<TableColumn<Oferta, EstadoOferta>, TableCell<Oferta, EstadoOferta>> {

    @Override
    public TableCell<Oferta, EstadoOferta> call(TableColumn<Oferta, EstadoOferta> column) {
        return new TableCell<>() {
            private final ComboBox<EstadoOferta> comboEstadoFila = new ComboBox<>(
                    FXCollections.observableArrayList(EstadoOferta.values()));

            {
                comboEstadoFila.setOnAction(event -> {
                    Oferta fila = getTableView().getItems().get(getIndex());
                    fila.setEstado(comboEstadoFila.getValue());
                    aplicarEstiloCombo(comboEstadoFila.getValue());
                });
            }

            @Override
            protected void updateItem(EstadoOferta estado, boolean empty) {
                super.updateItem(estado, empty);
                if (empty || estado == null) {
                    setGraphic(null);
                    return;
                }
                comboEstadoFila.setValue(estado);
                aplicarEstiloCombo(estado);
                setGraphic(comboEstadoFila);
            }

            private void aplicarEstiloCombo(EstadoOferta estado) {
                comboEstadoFila.setStyle(
                        "-fx-background-color: " + colorFondo(estado) + ";"
                        + "-fx-background-radius: 12;"
                        + "-fx-mark-color: " + colorTexto(estado) + ";");
                comboEstadoFila.setButtonCell(new ListCell<>() {
                    @Override
                    protected void updateItem(EstadoOferta item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty || item == null ? null : etiqueta(item));
                        setStyle("-fx-text-fill: " + colorTexto(estado) + "; -fx-font-weight: bold;");
                    }
                });
            }
        };
    }

    //region Colores y etiqueta de cada estado (paleta del manual de estilo)
    private static String etiqueta(EstadoOferta estado) {
        String nombre = estado.name().toLowerCase();
        return Character.toUpperCase(nombre.charAt(0)) + nombre.substring(1);
    }

    private static String colorFondo(EstadoOferta estado) {
        switch (estado) {
            case ENTREVISTA: return "#0F766E";
            case RECHAZADO:  return "#FF8B78";
            case PENDIENTE:  return "#F5B027";
            case ACEPTADO:   return "#DCFCE7";
        }
        throw new IllegalArgumentException("Estado no soportado: " + estado);
    }

    private static String colorTexto(EstadoOferta estado) {
        switch (estado) {
            case ENTREVISTA: return "white";
            case RECHAZADO:  return "#7A1D0F";
            case PENDIENTE:  return "#4A3200";
            case ACEPTADO:   return "#166534";
        }
        throw new IllegalArgumentException("Estado no soportado: " + estado);
    }
    //endregion
}
