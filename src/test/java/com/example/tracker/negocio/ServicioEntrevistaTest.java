package com.example.tracker.negocio;

import com.example.tracker.datos.DAO.DAO;
import com.example.tracker.datos.enums.Estado;
import com.example.tracker.datos.enums.TipoEntrevista;
import com.example.tracker.datos.modelo.Entrevista;
import com.example.tracker.datos.modelo.Oferta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicioEntrevistaTest {

    @Mock
    private DAO<Entrevista> entrevistaDAO;

    private ServicioEntrevista servicio;

    @BeforeEach
    void setUp() {
        servicio = new ServicioEntrevista(entrevistaDAO);
    }

    private Oferta crearOferta(Long id) {
        Oferta oferta = new Oferta();
        oferta.setOfertaId(id);
        return oferta;
    }

    private Entrevista crearEntrevista(Long id, Oferta oferta, LocalDate fecha) {
        return new Entrevista(id, oferta, fecha, TipoEntrevista.RRHH, "", Estado.PENDIENTE, true);
    }

    @Test
    void guardarSinTipoLanzaExcepcion() {
        Entrevista entrevista = crearEntrevista(null, crearOferta(1L), LocalDate.now());
        entrevista.setTipo(null);

        assertThrows(RuntimeException.class, () -> servicio.guardar(entrevista));
        verify(entrevistaDAO, never()).save(any());
    }

    @Test
    void listarPorOfertaDevuelveSoloLasDeEsaOferta() {
        Oferta oferta1 = crearOferta(1L);
        Oferta oferta2 = crearOferta(2L);
        when(entrevistaDAO.findAll()).thenReturn(List.of(
                crearEntrevista(10L, oferta1, LocalDate.now()),
                crearEntrevista(11L, oferta2, LocalDate.now())));

        List<Entrevista> resultado = servicio.listarPorOferta(oferta1);

        assertEquals(1, resultado.size());
        assertEquals(10L, resultado.get(0).getEntrevistaId());
    }

    @Test
    void listarProximasNoIncluyeLasPasadas() {
        Oferta oferta = crearOferta(1L);
        when(entrevistaDAO.findAll()).thenReturn(List.of(
                crearEntrevista(10L, oferta, LocalDate.now().minusDays(1)),
                crearEntrevista(11L, oferta, LocalDate.now().plusDays(3))));

        List<Entrevista> resultado = servicio.listarProximas(5);

        assertEquals(1, resultado.size());
        assertEquals(11L, resultado.get(0).getEntrevistaId());
    }
}
