package com.example.tracker.negocio;

import com.example.tracker.datos.DAO.DAO;
import com.example.tracker.datos.enums.Estado;
import com.example.tracker.datos.modelo.Empresa;
import com.example.tracker.datos.modelo.Entrevista;
import com.example.tracker.datos.modelo.Nota;
import com.example.tracker.datos.modelo.Oferta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicioOfertaTest {

    @Mock
    private DAO<Oferta> ofertaDAO;
    @Mock
    private DAO<Entrevista> entrevistaDAO;
    @Mock
    private DAO<Nota> notaDAO;
    @Mock
    private DataSource dataSource;
    @Mock
    private Connection conexion;
    @Mock
    private ServicioActividad servicioActividad;

    private ServicioOferta servicio;

    @BeforeEach
    void setUp() {
        servicio = new ServicioOferta(ofertaDAO, entrevistaDAO, notaDAO, dataSource, servicioActividad);
    }

    private Oferta crearOferta(Long id, String titulo, String empresa, Estado estado, double salario) {
        Empresa e = new Empresa();
        e.setEmpresaId(1L);
        e.setNombre(empresa);

        Oferta oferta = new Oferta();
        oferta.setOfertaId(id);
        oferta.setTitulo(titulo);
        oferta.setEmpresa(e);
        oferta.setEstado(estado);
        oferta.setFechaPostulacion(LocalDate.of(2026, 10, 1));
        oferta.setSalario(salario);
        return oferta;
    }

    @Test
    void guardarSinTituloLanzaExcepcion() {
        Oferta oferta = crearOferta(null, "", "Indra", Estado.PENDIENTE, 24000);

        assertThrows(RuntimeException.class, () -> servicio.guardar(oferta));
        verify(ofertaDAO, never()).save(any());
    }

    @Test
    void guardarOfertaNuevaLlamaASave() {
        Oferta oferta = crearOferta(null, "Desarrollador Java", "Indra", Estado.PENDIENTE, 24000);

        servicio.guardar(oferta);

        verify(ofertaDAO).save(oferta);
    }

    @Test
    void filtrarPorEstado() {
        when(ofertaDAO.findAll()).thenReturn(List.of(
                crearOferta(1L, "Desarrollador Java", "Indra", Estado.PENDIENTE, 24000),
                crearOferta(2L, "Backend Developer", "Accenture", Estado.ENTREVISTA, 30000)));

        List<Oferta> resultado = servicio.filtrar("Pendiente", null, null, null, null, null, null);

        assertEquals(1, resultado.size());
        assertEquals("Desarrollador Java", resultado.get(0).getTitulo());
    }

    @Test
    void filtrarPorEmpresaYSalarioMinimo() {
        when(ofertaDAO.findAll()).thenReturn(List.of(
                crearOferta(1L, "Desarrollador Java", "Indra", Estado.PENDIENTE, 24000),
                crearOferta(2L, "QA Tester", "Indra", Estado.PENDIENTE, 18000),
                crearOferta(3L, "Backend Developer", "Accenture", Estado.ENTREVISTA, 30000)));

        List<Oferta> resultado = servicio.filtrar(null, "Indra", null, 20000.0, null, null, null);

        assertEquals(1, resultado.size());
        assertEquals("Desarrollador Java", resultado.get(0).getTitulo());
    }

    @Test
    void contarPorEstado() {
        when(ofertaDAO.findAll()).thenReturn(List.of(
                crearOferta(1L, "Desarrollador Java", "Indra", Estado.PENDIENTE, 24000),
                crearOferta(2L, "QA Tester", "Indra", Estado.RECHAZADO, 18000),
                crearOferta(3L, "Backend Developer", "Accenture", Estado.PENDIENTE, 30000)));

        assertEquals(2, servicio.contar(Estado.PENDIENTE, null));
    }

    @Test
    void borrarHaceCommitDeLaTransaccion() throws Exception {
        Oferta oferta = crearOferta(1L, "Desarrollador Java", "Indra", Estado.PENDIENTE, 24000);
        when(dataSource.getConnection()).thenReturn(conexion);

        servicio.borrar(oferta);

        verify(ofertaDAO).deleteById(conexion, 1L);
        verify(conexion).commit();
    }
}
