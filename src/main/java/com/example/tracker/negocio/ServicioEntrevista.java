package com.example.tracker.negocio;

import com.example.tracker.datos.DAO.DAO;
import com.example.tracker.datos.modelo.Entrevista;
import com.example.tracker.datos.modelo.Oferta;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Lógica de negocio de las entrevistas. No sabe de dónde vienen los datos,
 * eso lo resuelve {@link DAO}.
 */
@RequiredArgsConstructor
public class ServicioEntrevista {

    private final DAO<Entrevista> repository;

    /**
     * Valida la entrevista y la guarda: si no tiene id la crea, si tiene id la actualiza.
     *
     * @param entrevista entrevista con los datos del formulario
     * @throws RuntimeException si falta un campo obligatorio
     */
    public void guardar(Entrevista entrevista) {
        if (entrevista.getOferta() == null) {
            throw new RuntimeException("La candidatura es obligatoria.");
        }
        if (entrevista.getFecha() == null) {
            throw new RuntimeException("La fecha de la entrevista es obligatoria.");
        }
        if (entrevista.getTipo() == null) {
            throw new RuntimeException("El tipo de entrevista es obligatorio.");
        }
        if (entrevista.getResultado() == null) {
            throw new RuntimeException("El resultado de la entrevista es obligatorio.");
        }

        if (entrevista.getEntrevistaId() == null) {
            repository.save(entrevista);
        } else {
            repository.update(entrevista);
        }
    }

    /**
     * @param oferta candidatura de la que se quieren las entrevistas
     * @return las entrevistas de esa candidatura
     */
    public List<Entrevista> listarPorOferta(Oferta oferta) {
        List<Entrevista> entrevistasDeOferta = new ArrayList<>();
        for (Entrevista entrevista : repository.findAll()) {
            if (entrevista.getOferta().getOfertaId().equals(oferta.getOfertaId())) {
                entrevistasDeOferta.add(entrevista);
            }
        }
        return entrevistasDeOferta;
    }

    /**
     * @param cantidad cuántas entrevistas devolver como máximo
     * @return las entrevistas de hoy en adelante, la más cercana primero
     */
    public List<Entrevista> listarProximas(int cantidad) {
        LocalDate hoy = LocalDate.now();
        return repository.findAll().stream()
                .filter(e -> !e.getFecha().isBefore(hoy))
                .sorted((a, b) -> a.getFecha().compareTo(b.getFecha()))
                .limit(cantidad)
                .toList();
    }

    /**
     * Borra una entrevista (borrado lógico: queda en la base de datos con Active = 0).
     *
     * @param entrevista entrevista a borrar
     */
    public void borrar(Entrevista entrevista) {
        repository.deleteById(entrevista.getEntrevistaId());
    }
}
