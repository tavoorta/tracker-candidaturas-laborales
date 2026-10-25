package com.tracker.datos.modelo;

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
    private LocalDate fechaPostulacion;
    private String enlace;
    private String notas;
}
