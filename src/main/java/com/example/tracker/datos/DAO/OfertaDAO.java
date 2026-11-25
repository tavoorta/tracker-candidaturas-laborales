package com.example.tracker.datos.DAO;

import com.example.tracker.datos.enums.Estado;
import com.example.tracker.datos.modelo.Empresa;
import com.example.tracker.datos.modelo.Oferta;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Acceso a la tabla OFERTA (con los datos de su empresa) con JDBC. */
public class OfertaDAO implements DAO<Oferta> {

    private DataSource ds;

    /**
     * @param ds origen de las conexiones a la base de datos
     */
    public OfertaDAO(DataSource ds) {
        this.ds = ds;
    }

    private static String sqlSelect =
            "SELECT o.*, e.Nombre, e.Sector, e.Web, e.FechaRegistro " +
                    "FROM OFERTA o " +
                    "JOIN EMPRESA e ON o.EmpresaId = e.EmpresaId " +
                    "WHERE o.Active = 1 AND e.Active = 1";

    private static String sqlFindById = sqlSelect + " AND o.OfertaId = ?";

    private static String sqlInsert = "INSERT INTO OFERTA (EmpresaId, Titulo, Descripcion, Estado, FechaPostulacion, Enlace, Salario) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static String sqlUpdate = "UPDATE OFERTA SET EmpresaId = ?, Titulo = ?, Descripcion = ?, Estado = ?, "
            + "FechaPostulacion = ?, Enlace = ?, Salario = ? WHERE OfertaId = ?";

    private static String sqlDelete = "UPDATE OFERTA SET Active = 0 WHERE OfertaId = ?";
    private static String sqlCount = "SELECT COUNT(*) FROM OFERTA";

    @Override
    public Optional<Oferta> findById(Long id) {
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

    @Override
    public void update(Oferta entity) {
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {

            ps.setLong(1, entity.getEmpresa().getEmpresaId());
            ps.setString(2, entity.getTitulo());
            ps.setString(3, entity.getDescripcion());
            ps.setString(4, entity.getEstado() != null ? entity.getEstado().name() : null);
            ps.setString(5, entity.getFechaPostulacion() != null ? entity.getFechaPostulacion().toString() : null);
            ps.setString(6, entity.getEnlace());
            ps.setDouble(7, entity.getSalario());
            ps.setLong(8, entity.getOfertaId());

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

    /**
     * Indica si la tabla no tiene ningún registro, ni activo ni borrado. Se usa para
     * cargar los CSV solo la primera vez.
     *
     * @return {@code true} si la tabla está vacía
     */
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
        oferta.setEstado(rs.getString("Estado") != null ? Estado.valueOf(rs.getString("Estado")) : Estado.PENDIENTE);
        oferta.setFechaPostulacion(rs.getString("FechaPostulacion") != null ? LocalDate.parse(rs.getString("FechaPostulacion")) : null);
        oferta.setEnlace(rs.getString("Enlace"));
        oferta.setSalario(rs.getDouble("Salario"));
        oferta.setActive(rs.getBoolean("Active"));
        return oferta;
    }
    //endregion Method Mapper
}
