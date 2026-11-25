package com.example.tracker.datos.modelo;

import com.example.tracker.datos.enums.Estado;
import lombok.*;

import java.time.LocalDate;

/** Candidatura a una oferta de empleo (tabla OFERTA). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Oferta {
    private Long ofertaId;
    private Empresa empresa;
    private String titulo;
    private String descripcion;
    private Estado estado;
    private LocalDate fechaPostulacion;
    private String enlace;
    private double salario;
    private boolean active;
}
