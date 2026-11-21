package com.example.tracker;
import com.example.tracker.datos.DAO.OfertaDAO;
import com.example.tracker.datos.data.DataSourceFactory;
import com.example.tracker.datos.data.InicializadorBaseDatos;
import com.example.tracker.negocio.OfertaCandidatura;
import javafx.application.Application;
import javafx.stage.Stage;
import javax.sql.DataSource;

public class App extends Application {

    @Override
    public void start(Stage stage) {

        DataSource ds = DataSourceFactory.getDataSource();
        InicializadorBaseDatos.initialize(ds);

        SceneNavigator.setStage(stage);
        SceneNavigator.setServicioOferta(new OfertaCandidatura(new OfertaDAO(ds)));

        SceneNavigator.navigateToInjection("login-view.fxml", "MentorIA - Iniciar sesión");
        stage.show();
    }

}
