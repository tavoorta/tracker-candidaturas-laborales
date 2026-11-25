package com.example.tracker.datos.DAO;

import com.example.tracker.datos.enums.Estado;
import com.example.tracker.datos.enums.TipoEntrevista;
import com.example.tracker.datos.modelo.Empresa;
import com.example.tracker.datos.modelo.Entrevista;
import com.example.tracker.datos.modelo.Oferta;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Acceso a la tabla ENTREVISTA con JDBC. */
public class EntrevistaDAO implements DAO<Entrevista> {

    private DataSource ds;

    /**
     * @param ds origen de las conexiones a la base de datos
     */
    public EntrevistaDAO(DataSource ds) {
        this.ds = ds;
    }

    private static String sqlSelect =
            "SELECT en.*, o.Titulo, e.EmpresaId, e.Nombre FROM ENTREVISTA en " +
                    "JOIN OFERTA o ON en.OfertaId = o.OfertaId " +
                    "JOIN EMPRESA e ON o.EmpresaId = e.EmpresaId " +
                    "WHERE en.Active = 1 AND o.Active = 1 AND e.Active = 1";
    private static String sqlFindById = sqlSelect + " AND en.EntrevistaId = ?";
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
            ps.setString(5, entity.getResultado().name());

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
            ps.setString(5, entity.getResultado().name());
            ps.setLong(6, entity.getEntrevistaId());

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
    private static Entrevista mapper(ResultSet rs) throws SQLException {
        Empresa empresa = new Empresa();
        empresa.setEmpresaId(rs.getLong("EmpresaId"));
        empresa.setNombre(rs.getString("Nombre"));

        Oferta oferta = new Oferta();
        oferta.setOfertaId(rs.getLong("OfertaId"));
        oferta.setTitulo(rs.getString("Titulo"));
        oferta.setEmpresa(empresa);

        Entrevista entrevista = new Entrevista();
        entrevista.setEntrevistaId(rs.getLong("EntrevistaId"));
        entrevista.setOferta(oferta);
        entrevista.setFecha(LocalDate.parse(rs.getString("Fecha")));
        entrevista.setTipo(TipoEntrevista.valueOf(rs.getString("Tipo")));
        entrevista.setNotas(rs.getString("Notas"));
        entrevista.setResultado(rs.getString("Resultado") != null ? Estado.valueOf(rs.getString("Resultado")) : Estado.PENDIENTE);
        entrevista.setActive(rs.getBoolean("Active"));
        return entrevista;
    }
    //endregion Method Mapper
}
