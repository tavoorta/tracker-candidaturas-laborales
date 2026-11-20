package com.example.tracker.datos.ayuda;

import java.sql.Connection;

/**
 * Clase descartable solo para probar manualmente que ConexionBaseDatos
 * funciona. No forma parte de la entrega: se borra una vez verificado.
 */
public class PruebaConexion {
    public static void main(String[] args) throws Exception {

        try (Connection conexion = ConexionBaseDatos.getDataSource().getConnection()) {
            System.out.println("Conexión OK: " + conexion.getMetaData().getURL());
        }
    }
}
