package com.example.tracker.datos.DAO;

import com.example.tracker.datos.enums.TipoEntrevista;
import com.example.tracker.datos.modelo.Entrevista;
import com.example.tracker.datos.modelo.Oferta;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EntrevistaDAO implements DAO<Entrevista> {

    private DataSource ds;

    public EntrevistaDAO(DataSource ds) {
        this.ds = ds;
    }

    private static String sqlSelect = "SELECT * FROM ENTREVISTA WHERE Active = 1";
    private static String sqlFindById = sqlSelect + " AND EntrevistaId = ?";
    private static String sqlInsert = "INSERT INTO ENTREVISTA (OfertaId, Fecha, Tipo, Notas, Resultado) VALUES (?, ?, ?, ?, ?)";
    private static String sqlUpdate = "UPDATE ENTREVISTA SET OfertaId = ?, Fecha = ?, Tipo = ?, Notas = ?, Resultado = ? WHERE EntrevistaId = ?";
    private static String sqlDelete = "UPDATE ENTREVISTA SET Active = 0 WHERE EntrevistaId = ?";

    @Override
    public Optional<Entrevista> findById(Long id) {
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
    public List<Entrevista> findAll() {

        List<Entrevista> salida = new ArrayList<>();

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
    public Entrevista save(Entrevista entity) {
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, entity.getOferta().getOfertaId());
            ps.setString(2, entity.getFecha().toString());
            ps.setString(3, entity.getTipo().name());
            ps.setString(4, entity.getNotas());
            ps.setString(5, entity.getResultado());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setEntrevistaId(rs.getLong(1));
                    entity.setActive(true);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return entity;
    }

    @Override
    public void update(Entrevista entity) {
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {

            ps.setLong(1, entity.getOferta().getOfertaId());
            ps.setString(2, entity.getFecha().toString());
            ps.setString(3, entity.getTipo().name());
            ps.setString(4, entity.getNotas());
            ps.setString(5, entity.getResultado());
            ps.setLong(6, entity.getEntrevistaId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Long id) {
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlDelete)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //region Method Mapper
    private static Entrevista mapper(ResultSet rs) throws SQLException {
        Oferta oferta = new Oferta();
        oferta.setOfertaId(rs.getLong("OfertaId"));

        Entrevista entrevista = new Entrevista();
        entrevista.setEntrevistaId(rs.getLong("EntrevistaId"));
        entrevista.setOferta(oferta);
        entrevista.setFecha(LocalDate.parse(rs.getString("Fecha")));
        entrevista.setTipo(TipoEntrevista.valueOf(rs.getString("Tipo")));
        entrevista.setNotas(rs.getString("Notas"));
        entrevista.setResultado(rs.getString("Resultado"));
        entrevista.setActive(rs.getBoolean("Active"));
        return entrevista;
    }
    //endregion Method Mapper
}
