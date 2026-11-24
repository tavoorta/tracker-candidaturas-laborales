package com.example.tracker.negocio;

import com.example.tracker.datos.DAO.DAO;
import com.example.tracker.datos.modelo.Nota;
import com.example.tracker.datos.modelo.Oferta;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Lógica de negocio de las notas de seguimiento de una candidatura. No sabe de
 * dónde vienen los datos, eso lo resuelve {@link DAO}.
 */
@RequiredArgsConstructor
public class ServicioNota {

    private final DAO<Nota> repository;

    /**
     * Crea una nota con la fecha de hoy para la candidatura indicada.
     *
     * @param oferta candidatura a la que pertenece la nota
     * @param texto  texto de la nota
     * @throws RuntimeException si la nota está vacía
     */
    public void agregar(Oferta oferta, String texto) {
        if (texto == null || texto.isBlank()) {
            throw new RuntimeException("La nota no puede estar vacía.");
        }

        Nota nota = new Nota();
        nota.setOferta(oferta);
        nota.setFecha(LocalDate.now());
        nota.setTexto(texto.trim());
        repository.save(nota);
    }

    /**
     * @param oferta candidatura de la que se quieren las notas
     * @return las notas de esa candidatura
     */
    public List<Nota> listarPorOferta(Oferta oferta) {
        List<Nota> notasDeOferta = new ArrayList<>();
        for (Nota nota : repository.findAll()) {
            if (nota.getOferta().getOfertaId().equals(oferta.getOfertaId())) {
                notasDeOferta.add(nota);
            }
        }
        return notasDeOferta;
    }

    /**
     * Borra una nota (borrado lógico: queda en la base de datos con Active = 0).
     *
     * @param nota nota a borrar
     */
    public void borrar(Nota nota) {
        repository.deleteById(nota.getNotaId());
    }
}
