package com.example.tracker.datos.DAO;

import com.example.tracker.datos.enums.Estado;
import com.example.tracker.datos.enums.TipoActividad;
import com.example.tracker.datos.modelo.Actividad;
import com.example.tracker.datos.modelo.Oferta;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Acceso a la tabla ACTIVIDAD con JDBC. */
public class ActividadDAO implements DAO<Actividad> {

    private DataSource ds;

    /**
     * @param ds origen de las conexiones a la base de datos
     */
    public ActividadDAO(DataSource ds) {
        this.ds = ds;
    }

    private static String sqlSelect =
            "SELECT a.* FROM ACTIVIDAD a " +
                    "LEFT JOIN OFERTA o ON a.OfertaId = o.OfertaId " +
                    "WHERE a.Active = 1 AND (a.OfertaId IS NULL OR o.Active = 1)";
    private static String sqlFindById = sqlSelect + " AND a.ActividadId = ?";
    private static String sqlInsert = "INSERT INTO ACTIVIDAD (OfertaId, Tipo, Estado, Descripcion, Fecha) VALUES (?, ?, ?, ?, ?)";
    private static String sqlUpdate = "UPDATE ACTIVIDAD SET OfertaId = ?, Tipo = ?, Estado = ?, Descripcion = ?, Fecha = ? WHERE ActividadId = ?";
    private static String sqlDelete = "UPDATE ACTIVIDAD SET Active = 0 WHERE ActividadId = ?";

    @Override
    public Optional<Actividad> findById(Long id) {
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
    public List<Actividad> findAll() {

        List<Actividad> salida = new ArrayList<>();

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
    public Actividad save(Actividad entity) {
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {

            if (entity.getOferta() != null) {
                ps.setLong(1, entity.getOferta().getOfertaId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, entity.getTipo().name());
            ps.setString(3, entity.getEstado() != null ? entity.getEstado().name() : null);
            ps.setString(4, entity.getDescripcion());
            ps.setString(5, entity.getFecha().toString());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setActividadId(rs.getLong(1));
                    entity.setActive(true);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return entity;
    }

    @Override
    public void update(Actividad entity) {
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {

            if (entity.getOferta() != null) {
                ps.setLong(1, entity.getOferta().getOfertaId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, entity.getTipo().name());
            ps.setString(3, entity.getEstado() != null ? entity.getEstado().name() : null);
            ps.setString(4, entity.getDescripcion());
            ps.setString(5, entity.getFecha().toString());
            ps.setLong(6, entity.getActividadId());

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

    //region Method Mapper
    private static Actividad mapper(ResultSet rs) throws SQLException {
        Oferta oferta = null;
        long ofertaId = rs.getLong("OfertaId");
        if (!rs.wasNull()) {
            oferta = new Oferta();
            oferta.setOfertaId(ofertaId);
        }

        Actividad actividad = new Actividad();
        actividad.setActividadId(rs.getLong("ActividadId"));
        actividad.setOferta(oferta);
        actividad.setTipo(TipoActividad.valueOf(rs.getString("Tipo")));
        actividad.setEstado(rs.getString("Estado") != null ? Estado.valueOf(rs.getString("Estado")) : null);
        actividad.setDescripcion(rs.getString("Descripcion"));
        actividad.setFecha(LocalDateTime.parse(rs.getString("Fecha")));
        actividad.setActive(rs.getBoolean("Active"));
        return actividad;
    }
    //endregion Method Mapper
}
