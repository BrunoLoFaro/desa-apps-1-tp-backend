package com.example.desabackend.util;

import java.util.Locale;

/**
 * Utilidad de normalización de emails compartida entre servicios.
 * Centraliza la lógica para evitar duplicación entre AuthServiceImpl y OtpServiceImpl.
 */
public final class EmailUtils {

    private EmailUtils() {}

    /**
     * Normaliza un email: trim + lowercase en Locale.ROOT.
     * Retorna "" si el valor es null.
     */
    public static String normalize(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
