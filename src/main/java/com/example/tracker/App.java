package com.example.tracker;
import com.example.tracker.datos.DAO.EmpresaDAO;
import com.example.tracker.datos.DAO.OfertaDAO;
import com.example.tracker.datos.data.DataSourceFactory;
import com.example.tracker.datos.data.ImportadorCsv;
import com.example.tracker.datos.data.InicializadorBaseDatos;
import com.example.tracker.negocio.OfertaCandidatura;
import javafx.application.Application;
import javafx.stage.Stage;
import javax.sql.DataSource;

public class App extends Application {

    @Override
    public void start(Stage stage) {

        DataSource dataSource = DataSourceFactory.getDataSource();
        InicializadorBaseDatos.initialize(dataSource);

        OfertaDAO ofertaDAO = new OfertaDAO(dataSource);
        new ImportadorCsv(new EmpresaDAO(dataSource), ofertaDAO).importar("empresas.csv", "candidaturas.csv");

        SceneNavigator.setStage(stage);
        SceneNavigator.setServicioOferta(new OfertaCandidatura(ofertaDAO));

        SceneNavigator.navigateToInjection("login-view.fxml", "MentorIA - Iniciar sesión");
        stage.show();
    }

}
