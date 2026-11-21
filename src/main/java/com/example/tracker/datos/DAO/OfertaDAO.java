package com.example.tracker.datos.DAO;

import com.example.tracker.datos.enums.EstadoOferta;
import com.example.tracker.datos.modelo.Empresa;
import com.example.tracker.datos.modelo.Oferta;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OfertaDAO implements DAO<Oferta> {

    private DataSource ds;

    public OfertaDAO(DataSource ds) {
        this.ds = ds;
    }

    private static String sqlSelect =
            "SELECT o.*, e.Nombre, e.Sector, e.Web, e.FechaRegistro " +
                    "FROM OFERTA o " +
                    "JOIN EMPRESA e ON o.EmpresaId = e.EmpresaId " +
                    "WHERE o.Active = 1";


    private static String sqlInsert = "INSERT INTO OFERTA (EmpresaId, Titulo, Descripcion, Estado, FechaPostulacion, Enlace, Salario, Notas) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    @Override
    public List<Oferta> findAll() {

        List<Oferta> salida = new ArrayList<>();

        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlSelect);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                salida.add(mapper(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return salida;
    }

    @Override
    public Oferta save(Oferta entity) {
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, entity.getEmpresa().getEmpresaId());
            ps.setString(2, entity.getTitulo());
            ps.setString(3, entity.getDescripcion());
            ps.setString(4, entity.getEstado() != null ? entity.getEstado().name() : null);
            ps.setString(5, entity.getFechaPostulacion() != null ? entity.getFechaPostulacion().toString() : null);
            ps.setString(6, entity.getEnlace());
            ps.setDouble(7, entity.getSalario());
            ps.setString(8, entity.getNotas());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setOfertaId(rs.getLong(1));
                    entity.setActive(true);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return entity;
    }

    //region Method Mapper
    private static Oferta mapper(ResultSet rs) throws SQLException {
        Empresa empresa = new Empresa();
        empresa.setEmpresaId(rs.getLong("EmpresaId"));
        empresa.setNombre(rs.getString("Nombre"));
        empresa.setSector(rs.getString("Sector"));
        empresa.setWeb(rs.getString("Web"));
        empresa.setFechaRegistro(rs.getString("FechaRegistro") != null ? LocalDate.parse(rs.getString("FechaRegistro")) : null);
        empresa.setActive(true);

        Oferta oferta = new Oferta();
        oferta.setOfertaId(rs.getLong("OfertaId"));
        oferta.setEmpresa(empresa);
        oferta.setTitulo(rs.getString("Titulo"));
        oferta.setDescripcion(rs.getString("Descripcion"));
        oferta.setEstado(rs.getString("Estado") != null ? EstadoOferta.valueOf(rs.getString("Estado")) : EstadoOferta.PENDIENTE);
        oferta.setFechaPostulacion(rs.getString("FechaPostulacion") != null ? LocalDate.parse(rs.getString("FechaPostulacion")) : null);
        oferta.setEnlace(rs.getString("Enlace"));
        oferta.setSalario(rs.getDouble("Salario"));
        oferta.setNotas(rs.getString("Notas"));
        oferta.setActive(rs.getBoolean("Active"));
        return oferta;
    }
    //endregion Method Mapper
}
