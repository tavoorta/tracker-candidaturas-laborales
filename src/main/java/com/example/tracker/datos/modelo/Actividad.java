package com.example.tracker.datos.modelo;

import com.example.tracker.datos.enums.Estado;
import com.example.tracker.datos.enums.TipoActividad;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Actividad {
    private Long actividadId;
    private Oferta oferta;
    private TipoActividad tipo;
    private Estado estado;
    private String descripcion;
    private LocalDateTime fecha;
    private boolean active;
}
