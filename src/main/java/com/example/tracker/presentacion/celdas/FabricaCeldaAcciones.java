package com.example.tracker.presentacion.celdas;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

/**
 * Crea la celda de "Acciones" con tres botones tipo ícono (ver/editar/eliminar),
 * reutilizable por cualquier tabla (candidaturas, empresas...).
 * Una celda de tabla cambia por fila según el dato y el FXML es estático,
 * por eso el cellFactory se construye en Java.
 */
public class FabricaCeldaAcciones<T> implements Callback<TableColumn<T, Void>, TableCell<T, Void>> {

    private final EventHandler<ActionEvent> manejador;

    public FabricaCeldaAcciones(EventHandler<ActionEvent> manejador) {
        this.manejador = manejador;
    }

    @Override
    public TableCell<T, Void> call(TableColumn<T, Void> column) {
        return new TableCell<>() {
            private final Button botonVer = crearBotonIcono("/images/icon-info.png");
            private final Button botonEditar = crearBotonIcono("/images/icon-editar.png");
            private final Button botonEliminar = crearBotonIcono("/images/icon-eliminar.png");
            private final HBox contenedor = new HBox(6.0, botonVer, botonEditar, botonEliminar);

            {
                botonVer.setOnAction(manejador);
                botonEditar.setOnAction(manejador);
                botonEliminar.setOnAction(manejador);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : contenedor);
            }
        };
    }

    private Button crearBotonIcono(String rutaImagen) {
        ImageView icono = new ImageView(new Image(getClass().getResourceAsStream(rutaImagen)));
        icono.setFitWidth(18.0);
        icono.setFitHeight(18.0);
        Button boton = new Button();
        boton.setGraphic(icono);
        boton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        return boton;
    }
}
