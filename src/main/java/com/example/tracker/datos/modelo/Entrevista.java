package com.example.tracker.datos.modelo;

import com.example.tracker.datos.enums.TipoEntrevista;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Entrevista {
    private Long entrevistaId;
    private Oferta oferta;
    private LocalDate fecha;
    private TipoEntrevista tipo;
    private String notas;
    private String resultado;
    private boolean active;
}
