package com.example.tracker;
import com.example.tracker.datos.DAO.ActividadDAO;
import com.example.tracker.datos.DAO.EmpresaDAO;
import com.example.tracker.datos.DAO.EntrevistaDAO;
import com.example.tracker.datos.DAO.NotaDAO;
import com.example.tracker.datos.DAO.OfertaDAO;
import com.example.tracker.datos.data.DataSourceFactory;
import com.example.tracker.datos.data.ImportadorCsv;
import com.example.tracker.datos.data.InicializadorBaseDatos;
import com.example.tracker.negocio.ServicioActividad;
import com.example.tracker.negocio.ServicioEmpresa;
import com.example.tracker.negocio.ServicioEntrevista;
import com.example.tracker.negocio.ServicioNota;
import com.example.tracker.negocio.ServicioOferta;
import javafx.application.Application;
import javafx.stage.Stage;
import javax.sql.DataSource;

public class App extends Application {

    @Override
    public void start(Stage stage) {

        DataSource dataSource = DataSourceFactory.getDataSource();
        InicializadorBaseDatos.initialize(dataSource);

        OfertaDAO ofertaDAO = new OfertaDAO(dataSource);
        EmpresaDAO empresaDAO = new EmpresaDAO(dataSource);
        EntrevistaDAO entrevistaDAO = new EntrevistaDAO(dataSource);
        NotaDAO notaDAO = new NotaDAO(dataSource);
        ActividadDAO actividadDAO = new ActividadDAO(dataSource);
        new ImportadorCsv(empresaDAO, ofertaDAO, notaDAO).importar("empresas.csv", "candidaturas.csv");

        SceneNavigator.setStage(stage);
        ServicioActividad servicioActividad = new ServicioActividad(actividadDAO);
        ServicioOferta servicioOferta = new ServicioOferta(ofertaDAO, entrevistaDAO, notaDAO, dataSource, servicioActividad);
        SceneNavigator.setServicioActividad(servicioActividad);
        SceneNavigator.setServicioOferta(servicioOferta);
        SceneNavigator.setServicioEmpresa(new ServicioEmpresa(empresaDAO, servicioOferta, dataSource));
        SceneNavigator.setServicioEntrevista(new ServicioEntrevista(entrevistaDAO));
        SceneNavigator.setServicioNota(new ServicioNota(notaDAO));

        SceneNavigator.navigateToInjection("login-view.fxml", "MentorIA - Iniciar sesión");
        stage.show();
    }

}
