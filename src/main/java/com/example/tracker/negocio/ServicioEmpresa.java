package com.example.tracker.negocio;

import com.example.tracker.datos.DAO.DAO;
import com.example.tracker.datos.modelo.Empresa;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Lógica de negocio de las empresas. No sabe de dónde vienen los datos,
 * eso lo resuelve {@link DAO}.
 */
@RequiredArgsConstructor
public class ServicioEmpresa {

    private final DAO<Empresa> repository;

    /**
     * @return los nombres de todas las empresas en orden alfabético
     */
    public List<String> listarNombres() {
        return repository.findAll().stream()
                .map(Empresa::getNombre)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    /**
     * @param nombre nombre de la empresa (ignora mayúsculas/minúsculas)
     * @return la empresa con ese nombre, o {@code null} si no existe
     */
    public Empresa buscarPorNombre(String nombre) {
        for (Empresa empresa : repository.findAll()) {
            if (empresa.getNombre().equalsIgnoreCase(nombre)) {
                return empresa;
            }
        }
        return null;
    }
}
