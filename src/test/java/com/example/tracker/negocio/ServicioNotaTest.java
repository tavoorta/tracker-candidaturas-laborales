package com.example.tracker.negocio;

import com.example.tracker.datos.DAO.DAO;
import com.example.tracker.datos.modelo.Nota;
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
class ServicioNotaTest {

    @Mock
    private DAO<Nota> notaDAO;

    private ServicioNota servicio;

    @BeforeEach
    void setUp() {
        servicio = new ServicioNota(notaDAO);
    }

    private Oferta crearOferta(Long id) {
        Oferta oferta = new Oferta();
        oferta.setOfertaId(id);
        return oferta;
    }

    @Test
    void agregarNotaVaciaLanzaExcepcion() {
        assertThrows(RuntimeException.class, () -> servicio.agregar(crearOferta(1L), " "));
        verify(notaDAO, never()).save(any());
    }

    @Test
    void listarPorOfertaDevuelveSoloLasDeEsaOferta() {
        Oferta oferta1 = crearOferta(1L);
        Oferta oferta2 = crearOferta(2L);
        when(notaDAO.findAll()).thenReturn(List.of(
                new Nota(1L, oferta1, LocalDate.now(), "Enviado el CV", true),
                new Nota(2L, oferta2, LocalDate.now(), "Me llamaron", true)));

        List<Nota> resultado = servicio.listarPorOferta(oferta1);

        assertEquals(1, resultado.size());
        assertEquals("Enviado el CV", resultado.get(0).getTexto());
    }
}
