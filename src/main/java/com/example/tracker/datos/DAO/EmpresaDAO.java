package com.example.tracker.datos.DAO;

import com.example.tracker.datos.modelo.Empresa;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmpresaDAO implements DAO<Empresa> {

    private DataSource ds;

    public EmpresaDAO(DataSource ds) {
        this.ds = ds;
    }

    private static String sqlSelect = "SELECT * FROM EMPRESA WHERE Active = 1";
    private static String sqlFindById = sqlSelect + " AND EmpresaId = ?";
    private static String sqlInsert = "INSERT INTO EMPRESA (Nombre, Sector, Web, Ubicacion, LinkedIn, Notas, FechaRegistro) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static String sqlUpdate = "UPDATE EMPRESA SET Nombre = ?, Sector = ?, Web = ?, Ubicacion = ?, LinkedIn = ?, "
            + "Notas = ?, FechaRegistro = ? WHERE EmpresaId = ?";
    private static String sqlDelete = "UPDATE EMPRESA SET Active = 0 WHERE EmpresaId = ?";
    private static String sqlCount = "SELECT COUNT(*) FROM EMPRESA";

    @Override
    public Optional<Empresa> findById(Long id) {
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlFindById)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(mapper(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<Empresa> findAll() {

        List<Empresa> salida = new ArrayList<>();

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
    public Empresa save(Empresa entity) {
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, entity.getNombre());
            ps.setString(2, entity.getSector());
            ps.setString(3, entity.getWeb());
            ps.setString(4, entity.getUbicacion());
            ps.setString(5, entity.getLinkedIn());
            ps.setString(6, entity.getNotas());
            ps.setString(7, entity.getFechaRegistro() != null ? entity.getFechaRegistro().toString() : null);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setEmpresaId(rs.getLong(1));
                    entity.setActive(true);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return entity;
    }

    @Override
    public void update(Empresa entity) {
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {

            ps.setString(1, entity.getNombre());
            ps.setString(2, entity.getSector());
            ps.setString(3, entity.getWeb());
            ps.setString(4, entity.getUbicacion());
            ps.setString(5, entity.getLinkedIn());
            ps.setString(6, entity.getNotas());
            ps.setString(7, entity.getFechaRegistro() != null ? entity.getFechaRegistro().toString() : null);
            ps.setLong(8, entity.getEmpresaId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Long id) {
        try (Connection conn = ds.getConnection()) {
            deleteById(conn, id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Connection conn, Long id) {
        try (PreparedStatement ps = conn.prepareStatement(sqlDelete)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean tablaVacia() {
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlCount);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getInt(1) == 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //region Method Mapper
    private static Empresa mapper(ResultSet rs) throws SQLException {
        Empresa empresa = new Empresa();
        empresa.setEmpresaId(rs.getLong("EmpresaId"));
        empresa.setNombre(rs.getString("Nombre"));
        empresa.setSector(rs.getString("Sector"));
        empresa.setWeb(rs.getString("Web"));
        empresa.setUbicacion(rs.getString("Ubicacion"));
        empresa.setLinkedIn(rs.getString("LinkedIn"));
        empresa.setNotas(rs.getString("Notas"));
        empresa.setFechaRegistro(rs.getString("FechaRegistro") != null ? LocalDate.parse(rs.getString("FechaRegistro")) : null);
        empresa.setActive(rs.getBoolean("Active"));
        return empresa;
    }
    //endregion Method Mapper
}
