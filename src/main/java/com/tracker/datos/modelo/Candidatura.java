package com.tracker.datos.modelo;

import com.tracker.datos.enums.EstadoCandidatura;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Candidatura {
    private int id;
    private String titulo;
    private String empresa;
    private EstadoCandidatura estado;
    private LocalDate fechaPostulacion;
    private String enlace;
    private String notas;
}
