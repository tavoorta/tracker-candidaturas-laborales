package com.example.tracker.negocio;

import com.example.tracker.datos.DAO.DAO;
import com.example.tracker.datos.modelo.Entrevista;
import com.example.tracker.datos.modelo.Oferta;
import lombok.RequiredArgsConstructor;

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
        List<Entrevista> listaOferta = new ArrayList<>();
        for (Entrevista entrevista : repository.findAll()) {
            if (entrevista.getOferta().getOfertaId().equals(oferta.getOfertaId())) {
                listaOferta.add(entrevista);
            }
        }
        return listaOferta;
    }
}
