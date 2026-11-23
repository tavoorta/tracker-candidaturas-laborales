package com.example.tracker.negocio;

import com.example.tracker.datos.DAO.DAO;
import com.example.tracker.datos.modelo.Empresa;
import com.example.tracker.datos.modelo.Oferta;
import lombok.RequiredArgsConstructor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Lógica de negocio de las empresas. No sabe de dónde vienen los datos,
 * eso lo resuelve {@link DAO}.
 */
@RequiredArgsConstructor
public class ServicioEmpresa {

    private final DAO<Empresa> repository;
    private final ServicioOferta servicioOferta;
    private final DataSource dataSource;

    /**
     * @param texto texto a buscar en el nombre, la ubicación o el sector, o {@code null}
     * @return las empresas que lo contienen, en orden alfabético
     */
    public List<Empresa> listar(String texto) {
        String busqueda = texto == null ? "" : texto.trim().toLowerCase();

        return repository.findAll().stream()
                .filter(e -> busqueda.isEmpty()
                        || e.getNombre().toLowerCase().contains(busqueda)
                        || (e.getUbicacion() != null && e.getUbicacion().toLowerCase().contains(busqueda))
                        || (e.getSector() != null && e.getSector().toLowerCase().contains(busqueda)))
                .sorted((a, b) -> a.getNombre().compareToIgnoreCase(b.getNombre()))
                .toList();
    }

    /**
     * Ordena las empresas por el campo indicado.
     *
     * @param empresas   empresas a ordenar (no se modifica la lista recibida)
     * @param campo      "nombre", "ubicacion" o "candidaturas"
     * @param ascendente {@code true} de menor a mayor, {@code false} al revés
     * @return una lista nueva con las empresas ordenadas
     */
    public List<Empresa> ordenar(List<Empresa> empresas, String campo, boolean ascendente) {
        List<Empresa> ordenadas = new ArrayList<>(empresas);

        switch (campo) {
            case "nombre" -> ordenadas.sort((a, b) -> a.getNombre().compareToIgnoreCase(b.getNombre()));
            case "ubicacion" -> ordenadas.sort((a, b) -> textoOVacio(a.getUbicacion()).compareToIgnoreCase(textoOVacio(b.getUbicacion())));
            case "candidaturas" -> ordenadas.sort((a, b) -> Integer.compare(contarCandidaturas(a), contarCandidaturas(b)));
        }

        if (!ascendente) {
            Collections.reverse(ordenadas);
        }
        return ordenadas;
    }

    private String textoOVacio(String texto) {
        return texto == null ? "" : texto;
    }

    /**
     * @return los nombres de todas las empresas en orden alfabético
     */
    public List<String> listarNombres() {
        return repository.findAll().stream()
                .map(Empresa::getNombre)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    /**
     * @param nombre nombre de la empresa (ignora mayúsculas/minúsculas)
     * @return la empresa con ese nombre, o {@code null} si no existe
     */
    public Empresa buscarPorNombre(String nombre) {
        for (Empresa empresa : repository.findAll()) {
            if (empresa.getNombre().equalsIgnoreCase(nombre)) {
                return empresa;
            }
        }
        return null;
    }

    /**
     * @param empresa empresa a consultar
     * @return cuántas candidaturas tiene la empresa
     */
    public int contarCandidaturas(Empresa empresa) {
        return servicioOferta.listarPorEmpresa(empresa).size();
    }

    /**
     * Valida la empresa y la guarda: si no tiene id la crea (con la fecha de hoy como
     * fecha de registro), si tiene id la actualiza.
     *
     * @param empresa empresa con los datos del formulario
     * @throws RuntimeException si falta el nombre o ya existe otra empresa con ese nombre
     */
    public void guardar(Empresa empresa) {
        if (empresa.getNombre() == null || empresa.getNombre().isBlank()) {
            throw new RuntimeException("El nombre es obligatorio.");
        }

        Empresa mismoNombre = buscarPorNombre(empresa.getNombre().trim());
        if (mismoNombre != null && !mismoNombre.getEmpresaId().equals(empresa.getEmpresaId())) {
            throw new RuntimeException("Ya existe una empresa con el nombre " + mismoNombre.getNombre() + ".");
        }

        empresa.setNombre(empresa.getNombre().trim());
        if (empresa.getEmpresaId() == null) {
            empresa.setFechaRegistro(LocalDate.now());
            repository.save(empresa);
        } else {
            repository.update(empresa);
        }
    }

    /**
     * Borra una empresa junto con sus candidaturas (y las entrevistas y notas de cada una)
     * en una sola transacción: o se borra todo, o no se borra nada (borrado lógico, Active = 0).
     *
     * @param empresa empresa a borrar
     * @throws RuntimeException si no se indica la empresa, no tiene id o falla el borrado
     */
    public void borrar(Empresa empresa) {
        if (empresa == null || empresa.getEmpresaId() == null) {
            throw new RuntimeException("Debe indicar la empresa a borrar.");
        }

        List<Oferta> ofertasDeEmpresa = servicioOferta.listarPorEmpresa(empresa);

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                for (Oferta oferta : ofertasDeEmpresa) {
                    servicioOferta.borrarConDependencias(conn, oferta);
                }
                repository.deleteById(conn, empresa.getEmpresaId());

                conn.commit();
            } catch (RuntimeException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
