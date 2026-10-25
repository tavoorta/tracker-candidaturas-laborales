package com.tracker.datos.enums;

public enum EstadoCandidatura {

    /** La candidatura se ha enviado y aún no hay respuesta. */
    PENDIENTE,

    /** La empresa ha citado al candidato a una o varias entrevistas. */
    ENTREVISTA,

    /** La empresa ha descartado al candidato. */
    RECHAZADO,

    /** El candidato ha recibido una oferta y la ha aceptado. */
    ACEPTADO

}
