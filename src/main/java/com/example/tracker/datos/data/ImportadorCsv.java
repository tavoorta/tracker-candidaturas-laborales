package com.example.tracker.datos.data;

import com.example.tracker.datos.DAO.EmpresaDAO;
import com.example.tracker.datos.DAO.NotaDAO;
import com.example.tracker.datos.DAO.OfertaDAO;
import com.example.tracker.datos.enums.Estado;
import com.example.tracker.datos.modelo.Empresa;
import com.example.tracker.datos.modelo.Nota;
import com.example.tracker.datos.modelo.Oferta;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Carga los datos iniciales de los CSV en la base de datos, solo si las tablas están vacías.
 * - empresas.csv:     nombre;sector;web;ubicacion;linkedin;notas
 * - candidaturas.csv: titulo;empresa;descripcion;estado;fechaPostulacion;enlace;salario;notas
 */
public class ImportadorCsv {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final EmpresaDAO empresaDAO;
    private final OfertaDAO ofertaDAO;
    private final NotaDAO notaDAO;

    /**
     * @param empresaDAO acceso a la tabla EMPRESA
     * @param ofertaDAO  acceso a la tabla OFERTA
     * @param notaDAO   acceso a la tabla NOTA
     */
    public ImportadorCsv(EmpresaDAO empresaDAO, OfertaDAO ofertaDAO, NotaDAO notaDAO) {
        this.empresaDAO = empresaDAO;
        this.ofertaDAO = ofertaDAO;
        this.notaDAO = notaDAO;
    }

    /**
     * Importa primero las empresas y después las ofertas, para que cada oferta
     * encuentre su empresa ya guardada. Si una tabla ya tiene datos, no la toca.
     *
     * @param rutaEmpresas ruta del CSV de empresas
     * @param rutaOfertas  ruta del CSV de candidaturas
     */
    public void importar(String rutaEmpresas, String rutaOfertas) {

        if (empresaDAO.tablaVacia()) {
            for (Empresa empresa : leerEmpresas(rutaEmpresas)) {
                empresaDAO.save(empresa);
            }
        }

        if (ofertaDAO.tablaVacia()) {
            List<Empresa> empresas = empresaDAO.findAll();

            for (String linea : leerLineas(rutaOfertas, "titulo;")) {
                Oferta oferta = crearOfertaDeString(linea);
                Empresa empresa = buscarEmpresa(empresas, oferta.getEmpresa().getNombre());

                if (empresa == null) {
                    empresa = oferta.getEmpresa();
                    empresa.setFechaRegistro(LocalDate.now());
                    empresaDAO.save(empresa);
                    empresas.add(empresa);
                }

                oferta.setEmpresa(empresa);
                ofertaDAO.save(oferta);

                String textoNota = linea.split(";", -1)[7];
                if (!textoNota.isBlank()) {
                    notaDAO.save(new Nota(null, oferta, oferta.getFechaPostulacion(), textoNota, true));
                }
            }
        }
    }

    private Empresa buscarEmpresa(List<Empresa> empresas, String nombre) {
        for (Empresa empresa : empresas) {
            if (empresa.getNombre().equalsIgnoreCase(nombre)) {
                return empresa;
            }
        }
        return null;
    }

    private List<Empresa> leerEmpresas(String rutaCsv) {
        List<Empresa> empresas = new ArrayList<>();
        for (String linea : leerLineas(rutaCsv, "nombre;")) {
            empresas.add(crearEmpresaDeString(linea));
        }
        return empresas;
    }

    /** Devuelve las líneas del fichero sin la cabecera ni las líneas vacías. */
    private List<String> leerLineas(String rutaCsv, String inicioCabecera) {
        Path ruta = Path.of(rutaCsv);
        if (!Files.exists(ruta)) {
            throw new RuntimeException("No se encontró el fichero CSV: " + rutaCsv);
        }

        try {
            List<String> salida = new ArrayList<>();
            for (String linea : Files.readAllLines(ruta, StandardCharsets.UTF_8)) {
                if (!linea.isBlank() && !linea.toLowerCase().startsWith(inicioCabecera)) {
                    salida.add(linea);
                }
            }
            return salida;
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el fichero CSV: " + rutaCsv, e);
        }
    }

    private Empresa crearEmpresaDeString(String linea) {
        String[] campos = linea.split(";", -1);

        Empresa empresa = new Empresa();
        empresa.setNombre(campos[0]);
        empresa.setSector(campos[1]);
        empresa.setWeb(campos[2]);
        empresa.setUbicacion(campos[3]);
        empresa.setLinkedIn(campos[4]);
        empresa.setNotas(campos[5]);
        empresa.setFechaRegistro(LocalDate.now());
        return empresa;
    }

    private Oferta crearOfertaDeString(String linea) {
        String[] campos = linea.split(";", -1);

        Empresa empresa = new Empresa();
        empresa.setNombre(campos[1]);

        Oferta oferta = new Oferta();
        oferta.setTitulo(campos[0]);
        oferta.setEmpresa(empresa);
        oferta.setDescripcion(campos[2]);
        oferta.setEstado(Estado.valueOf(campos[3].toUpperCase()));
        oferta.setFechaPostulacion(LocalDate.parse(campos[4], FORMATO_FECHA));
        oferta.setEnlace(campos[5]);
        oferta.setSalario(campos[6].isBlank() ? 0 : Double.parseDouble(campos[6]));
        return oferta;
    }
}
