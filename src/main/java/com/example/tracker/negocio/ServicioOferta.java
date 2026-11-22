package com.example.tracker.negocio;

import com.example.tracker.datos.DAO.DAO;
import com.example.tracker.datos.enums.EstadoOferta;
import com.example.tracker.datos.modelo.Oferta;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Lógica de negocio de las candidaturas: validaciones, filtrado y conversión
 * de los datos de entrada a los tipos del modelo. No sabe de dónde vienen ni
 * a dónde van los datos, eso lo resuelve {@link DAO}.
 */
@RequiredArgsConstructor
public class ServicioOferta {

    private final DAO<Oferta> repository;

    /**
     * Valida la candidatura y la guarda: si no tiene id la crea, si tiene id la actualiza.
     *
     * @param oferta candidatura con los datos del formulario
     * @throws RuntimeException si falta un campo obligatorio o el salario es negativo
     */
    public void guardar(Oferta oferta) {
        if (oferta.getTitulo() == null || oferta.getTitulo().isBlank()) {
            throw new RuntimeException("El título es obligatorio.");
        }
        if (oferta.getEmpresa() == null) {
            throw new RuntimeException("La empresa es obligatoria.");
        }
        if (oferta.getEstado() == null) {
            throw new RuntimeException("El estado es obligatorio.");
        }
        if (oferta.getFechaPostulacion() == null) {
            throw new RuntimeException("La fecha de aplicación es obligatoria.");
        }
        if (oferta.getSalario() < 0) {
            throw new RuntimeException("El salario no puede ser negativo.");
        }

        if (oferta.getOfertaId() == null) {
            repository.save(oferta);
        } else {
            repository.update(oferta);
        }
    }

    /**
     * Borra una candidatura (borrado lógico: queda en la base de datos con Active = 0).
     *
     * @param oferta candidatura a borrar
     * @throws RuntimeException si no se indica la candidatura o no tiene id
     */
    public void borrar(Oferta oferta) {
        if (oferta == null || oferta.getOfertaId() == null) {
            throw new RuntimeException("Debe indicar la candidatura a borrar.");
        }
        repository.deleteById(oferta.getOfertaId());
    }

    /**
     * @param estado  texto del estado a filtrar (por ejemplo "Pendiente"), o {@code null}
     * @param empresa nombre exacto de la empresa a filtrar, o {@code null}
     * @param texto   texto a buscar en el título o en el nombre de empresa, o {@code null}
     * @return las candidaturas que cumplen los filtros indicados
     */
    public List<Oferta> filtrar(String estado, String empresa, String texto) {
        String busqueda = texto == null ? "" : texto.trim().toLowerCase();

        return repository.findAll().stream()
                .filter(c -> estado == null || c.getEstado().toString().equals(estado))
                .filter(c -> empresa == null || c.getEmpresa().getNombre().equalsIgnoreCase(empresa))
                .filter(c -> busqueda.isEmpty()
                        || c.getTitulo().toLowerCase().contains(busqueda)
                        || c.getEmpresa().getNombre().toLowerCase().contains(busqueda))
                .toList();
    }

    /**
     * Cambia el estado de una candidatura y lo guarda en la base de datos.
     *
     * @param oferta      candidatura a modificar
     * @param nuevoEstado estado nuevo
     * @throws RuntimeException si no se indica la candidatura o el estado
     */
    public void cambiarEstado(Oferta oferta, EstadoOferta nuevoEstado) {
        if (oferta == null || nuevoEstado == null) {
            throw new RuntimeException("Debe indicar la candidatura y el nuevo estado.");
        }
        oferta.setEstado(nuevoEstado);
        repository.update(oferta);
    }

    /**
     * @return los nombres de las empresas que tienen candidaturas
     */
    public List<String> listarEmpresas() {
        return repository.findAll().stream()
                .map(c -> c.getEmpresa().getNombre())
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }
}
