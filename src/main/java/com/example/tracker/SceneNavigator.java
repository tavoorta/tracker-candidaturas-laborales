package com.example.tracker;

import com.example.tracker.presentacion.controllers.CandidaturasController;
import com.example.tracker.presentacion.controllers.LoginController;
import com.example.tracker.presentacion.controllers.ShellController;
import com.example.tracker.negocio.ServicioOferta;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneNavigator {

    private static Stage stage;

    private static ServicioOferta servicioOferta;

    public static void setStage(Stage stage) {
        SceneNavigator.stage = stage;
    }

    public static void setServicioOferta(ServicioOferta servicioOferta) {
        SceneNavigator.servicioOferta = servicioOferta;
    }

    /** Utilizado para navegar a una vista específica. */
    public static void navigateToInjection(String fxml, String title) {

        FXMLLoader fxmlLoader = crearLoader(fxml);

        try {
            stage.setTitle(title);
            if (stage.getScene() == null) {
                stage.setScene(new Scene(fxmlLoader.load(), 1200, 720));
            } else {
                stage.getScene().setRoot(fxmlLoader.load());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Utilizado para rellenar el contenido del shell. */
    public static void navigateToContenido(StackPane contenido, String fxml) {

        FXMLLoader fxmlLoader = crearLoader(fxml);

        try {
            contenido.getChildren().clear();
            contenido.getChildren().add(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** utilizado para crear un FXMLLoader con la ruta y el controllerFactory que crea cada controlador con su servicio. */
    private static FXMLLoader crearLoader(String fxml) {

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/views/" + fxml));

        fxmlLoader.setControllerFactory(type -> {
            return switch (fxml) {
                case "login-view.fxml" -> new LoginController();
                case "shell-view.fxml" -> new ShellController();
                case "candidaturas-view.fxml" -> new CandidaturasController(servicioOferta);
                default -> throw new IllegalArgumentException("Error con fxml: " + fxml);
            };
        });
        return fxmlLoader;
    }
}
