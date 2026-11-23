package com.example.tracker.datos.DAO;

import com.example.tracker.datos.modelo.Nota;
import com.example.tracker.datos.modelo.Oferta;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NotaDAO implements DAO<Nota> {

    private DataSource ds;

    public NotaDAO(DataSource ds) {
        this.ds = ds;
    }

    private static String sqlSelect =
            "SELECT n.* FROM NOTA n " +
                    "JOIN OFERTA o ON n.OfertaId = o.OfertaId " +
                    "JOIN EMPRESA e ON o.EmpresaId = e.EmpresaId " +
                    "WHERE n.Active = 1 AND o.Active = 1 AND e.Active = 1";
    private static String sqlFindById = sqlSelect + " AND n.NotaId = ?";
    private static String sqlInsert = "INSERT INTO NOTA (OfertaId, Fecha, Texto) VALUES (?, ?, ?)";
    private static String sqlUpdate = "UPDATE NOTA SET OfertaId = ?, Fecha = ?, Texto = ? WHERE NotaId = ?";
    private static String sqlDelete = "UPDATE NOTA SET Active = 0 WHERE NotaId = ?";

    @Override
    public Optional<Nota> findById(Long id) {
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
    public List<Nota> findAll() {

        List<Nota> salida = new ArrayList<>();

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
    public Nota save(Nota entity) {
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, entity.getOferta().getOfertaId());
            ps.setString(2, entity.getFecha().toString());
            ps.setString(3, entity.getTexto());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setNotaId(rs.getLong(1));
                    entity.setActive(true);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return entity;
    }

    @Override
    public void update(Nota entity) {
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {

            ps.setLong(1, entity.getOferta().getOfertaId());
            ps.setString(2, entity.getFecha().toString());
            ps.setString(3, entity.getTexto());
            ps.setLong(4, entity.getNotaId());

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
    private static Nota mapper(ResultSet rs) throws SQLException {
        Oferta oferta = new Oferta();
        oferta.setOfertaId(rs.getLong("OfertaId"));

        Nota nota = new Nota();
        nota.setNotaId(rs.getLong("NotaId"));
        nota.setOferta(oferta);
        nota.setFecha(LocalDate.parse(rs.getString("Fecha")));
        nota.setTexto(rs.getString("Texto"));
        nota.setActive(rs.getBoolean("Active"));
        return nota;
    }
    //endregion Method Mapper
}
