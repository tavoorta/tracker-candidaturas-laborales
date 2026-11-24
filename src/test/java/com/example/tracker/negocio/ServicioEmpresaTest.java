package com.example.tracker.negocio;

import com.example.tracker.datos.DAO.DAO;
import com.example.tracker.datos.modelo.Empresa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicioEmpresaTest {

    @Mock
    private DAO<Empresa> empresaDAO;
    @Mock
    private ServicioOferta servicioOferta;
    @Mock
    private DataSource dataSource;

    private ServicioEmpresa servicio;

    @BeforeEach
    void setUp() {
        servicio = new ServicioEmpresa(empresaDAO, servicioOferta, dataSource);
    }

    private Empresa crearEmpresa(Long id, String nombre) {
        Empresa empresa = new Empresa();
        empresa.setEmpresaId(id);
        empresa.setNombre(nombre);
        return empresa;
    }

    @Test
    void guardarSinNombreLanzaExcepcion() {
        Empresa empresa = crearEmpresa(null, "");

        assertThrows(RuntimeException.class, () -> servicio.guardar(empresa));
        verify(empresaDAO, never()).save(any());
    }

    @Test
    void guardarConNombreRepetidoLanzaExcepcion() {
        when(empresaDAO.findAll()).thenReturn(List.of(crearEmpresa(1L, "Indra")));
        Empresa empresa = crearEmpresa(null, "Indra");

        assertThrows(RuntimeException.class, () -> servicio.guardar(empresa));
        verify(empresaDAO, never()).save(any());
    }

    @Test
    void guardarEmpresaNuevaLlamaASave() {
        when(empresaDAO.findAll()).thenReturn(List.of());
        Empresa empresa = crearEmpresa(null, "Everis");

        servicio.guardar(empresa);

        verify(empresaDAO).save(empresa);
    }
}
