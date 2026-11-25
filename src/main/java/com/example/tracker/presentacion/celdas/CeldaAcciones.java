package com.example.tracker.presentacion.celdas;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Celda de la columna de acciones: muestra uno, dos o tres botones con icono.
 * Al pulsar un botón selecciona su fila y ejecuta la acción recibida.
 * Sirve para cualquier tabla.
 */
public class CeldaAcciones<T> extends TableCell<T, Void> {

    private final HBox contenedor;

    /**
     * Celda con los botones ver, editar y eliminar.
     *
     * @param ver      acción del botón ver
     * @param editar   acción del botón editar
     * @param eliminar acción del botón eliminar
     */
    public CeldaAcciones(EventHandler<ActionEvent> ver, EventHandler<ActionEvent> editar,
                         EventHandler<ActionEvent> eliminar) {
        contenedor = new HBox(6.0,
                crearBoton("/images/icon-info.png", ver),
                crearBoton("/images/icon-editar.png", editar),
                crearBoton("/images/icon-eliminar.png", eliminar));
    }

    /**
     * Celda con los botones editar y eliminar.
     *
     * @param editar   acción del botón editar
     * @param eliminar acción del botón eliminar
     */
    public CeldaAcciones(EventHandler<ActionEvent> editar, EventHandler<ActionEvent> eliminar) {
        contenedor = new HBox(6.0,
                crearBoton("/images/icon-editar.png", editar),
                crearBoton("/images/icon-eliminar.png", eliminar));
    }

    /**
     * Celda con un solo botón.
     *
     * @param rutaImagen ruta del icono del botón
     * @param accion     acción al pulsarlo
     */
    public CeldaAcciones(String rutaImagen, EventHandler<ActionEvent> accion) {
        contenedor = new HBox(6.0,
                crearBoton(rutaImagen, accion));
    }

    private Button crearBoton(String rutaImagen, EventHandler<ActionEvent> accion) {
        ImageView icono = new ImageView(new Image(getClass().getResourceAsStream(rutaImagen)));
        icono.setFitWidth(18.0);
        icono.setFitHeight(18.0);

        Button boton = new Button();
        boton.setGraphic(icono);
        boton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        boton.setOnAction(event -> {
            getTableView().getSelectionModel().select(getTableRow().getItem());
            accion.handle(event);
        });
        return boton;
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        setGraphic(empty ? null : contenedor);
    }
}
