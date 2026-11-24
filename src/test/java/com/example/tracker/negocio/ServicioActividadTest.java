package com.example.tracker.negocio;

import com.example.tracker.datos.DAO.DAO;
import com.example.tracker.datos.enums.Estado;
import com.example.tracker.datos.enums.TipoActividad;
import com.example.tracker.datos.modelo.Actividad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicioActividadTest {

    @Mock
    private DAO<Actividad> actividadDAO;

    private ServicioActividad servicio;

    @BeforeEach
    void setUp() {
        servicio = new ServicioActividad(actividadDAO);
    }

    private Actividad crearActividad(Long id, LocalDateTime fecha) {
        return new Actividad(id, null, TipoActividad.CAMBIO_ESTADO, Estado.PENDIENTE, "actividad " + id, fecha, true);
    }

    @Test
    void listarRecientesDevuelveLaMasRecientePrimero() {
        LocalDateTime ahora = LocalDateTime.now();
        when(actividadDAO.findAll()).thenReturn(List.of(
                crearActividad(1L, ahora.minusDays(2)),
                crearActividad(2L, ahora)));

        List<Actividad> resultado = servicio.listarRecientes(5);

        assertEquals(2L, resultado.get(0).getActividadId());
    }
}
