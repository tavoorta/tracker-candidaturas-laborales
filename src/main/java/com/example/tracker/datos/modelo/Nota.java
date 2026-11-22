package com.example.tracker.datos.modelo;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Nota {
    private Long notaId;
    private Oferta oferta;
    private LocalDate fecha;
    private String texto;
    private boolean active;
}
