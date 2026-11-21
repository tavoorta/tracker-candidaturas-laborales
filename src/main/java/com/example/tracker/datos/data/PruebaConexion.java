package com.example.tracker.datos.data;

import com.example.tracker.datos.DAO.EmpresaDAO;
import com.example.tracker.datos.DAO.OfertaDAO;
import com.example.tracker.datos.modelo.Empresa;
import com.example.tracker.datos.modelo.Oferta;

import javax.sql.DataSource;
import java.io.File;
import java.time.LocalDate;
import java.util.List;

/**
 * Prueba manual de la base de datos usando las clases del proyecto:
 * InicializadorBaseDatos crea las tablas, ImportadorCsv lee el CSV y
 * los DAO guardan y consultan. El CSV solo se importa si OFERTA está vacía.
 */
public class PruebaConexion {

    public static void main(String[] args) {

        File archivo = new File("tracker.db");
        if (!archivo.exists()) {
            System.out.println("La BD no existe, creándola...");
        } else {
            System.out.println("La BD ya existe, mostrando datos...");
        }

        DataSource ds = DataSourceFactory.getDataSource();
        InicializadorBaseDatos.initialize(ds);

        EmpresaDAO empresaDAO = new EmpresaDAO(ds);
        OfertaDAO ofertaDAO = new OfertaDAO(ds);

        ImportadorCsv importador = new ImportadorCsv();

        if (empresaDAO.findAll().isEmpty()) {
            System.out.println("Tabla EMPRESA vacía, importando empresas.csv...");

            List<Empresa> empresasCsv = importador.importarEmpresas("empresas.csv");
            for (Empresa empresa : empresasCsv) {
                empresaDAO.save(empresa);
            }
            System.out.println("Empresas importadas: " + empresasCsv.size());
        }

        if (ofertaDAO.findAll().isEmpty()) {
            System.out.println("Tabla OFERTA vacía, importando candidaturas.csv...");

            List<Oferta> ofertas = importador.importarCsv("candidaturas.csv");
            List<Empresa> empresas = empresaDAO.findAll();

            for (Oferta oferta : ofertas) {

                Empresa empresa = buscarEmpresa(empresas, oferta.getEmpresa().getNombre());

                if (empresa == null) {
                    empresa = oferta.getEmpresa();
                    empresa.setFechaRegistro(LocalDate.now());
                    empresaDAO.save(empresa);
                    empresas.add(empresa);
                }

                oferta.setEmpresa(empresa);
                ofertaDAO.save(oferta);
            }
            System.out.println("Ofertas importadas: " + ofertas.size());
        }

        System.out.println("\n--- Ofertas en la BD ---");
        for (Oferta oferta : ofertaDAO.findAll()) {
            System.out.println(oferta.getOfertaId() + " | " + oferta.getTitulo() + " | "
                    + oferta.getEmpresa().getNombre() + " | " + oferta.getEstado() + " | "
                    + oferta.getFechaPostulacion());
        }
    }

    private static Empresa buscarEmpresa(List<Empresa> empresas, String nombre) {
        for (Empresa empresa : empresas) {
            if (empresa.getNombre().equalsIgnoreCase(nombre)) {
                return empresa;
            }
        }
        return null;
    }
}
