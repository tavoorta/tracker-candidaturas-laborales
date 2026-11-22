package com.example.tracker.datos.modelo;

import com.example.tracker.datos.enums.EstadoOferta;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Oferta {
    private Long ofertaId;
    private Empresa empresa;
    private String titulo;
    private String descripcion;
    private EstadoOferta estado;
    private LocalDate fechaPostulacion;
    private String enlace;
    private double salario;
    private boolean active;
}
