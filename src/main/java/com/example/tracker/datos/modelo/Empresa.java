package com.example.tracker.datos.modelo;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Empresa {
    private Long empresaId;
    private String nombre;
    private String sector;
    private String web;
    private String ubicacion;
    private String linkedIn;
    private String notas;
    private LocalDate fechaRegistro;
    private boolean active;
}
