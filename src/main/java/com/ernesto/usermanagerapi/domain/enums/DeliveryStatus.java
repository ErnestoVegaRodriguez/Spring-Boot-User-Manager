package com.ernesto.usermanagerapi.domain.enums;

/**
 * Representa los estados por los que pasa un DeliveryAttempt dentro del suscriptor.
 *
 * PENDING   → El mensaje fue recibido y persistido, pero aún no se procesó.
 * DELIVERED → El mensaje fue desencolado y procesado correctamente por el EventProcessor.
 * FAILED    → El procesamiento falló (no se usa en esta implementación de prueba).
 *
 * Se usa enum en lugar de un String crudo para:
 * - Type-safety: el compilador evita errores tipográficos ("PENDIGN" no compila)
 * - Descubrimiento: el IDE autocompleta los valores disponibles
 * - Consistencia: todos los puntos del código usan los mismos valores
 */
public enum DeliveryStatus {
    PENDING,
    DELIVERED,
    FAILED
}
