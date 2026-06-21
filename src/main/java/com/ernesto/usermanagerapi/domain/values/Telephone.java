package com.ernesto.usermanagerapi.domain.values;

import lombok.Getter;

/**
 * Value Object que representa un número telefónico en formato E.164.
 * <p>
 * <b>E.164</b> es el estándar internacional de numeración telefónica.
 * Su formato es: {@code +[código de país][número nacional]}.
 * <br>Ejemplo: {@code +573001234567} (Colombia, móvil).
 * <p>
 * Este objeto de valor es intencionalmente ANÉMICO — solo almacena
 * el string E.164 normalizado. NO hace validación ni parseo porque
 * esas responsabilidades pertenecen a la infraestructura
 * (ver {@code adapter.phone.GooglePhoneParser}).
 * <p>
 * Al mantener Telephone sin dependencias externas, el dominio se
 * protege de cambios en librerías de validación telefónica.
 *
 * @see com.ernesto.usermanagerapi.application.ports.drivens.PhoneParser
 * @see com.ernesto.usermanagerapi.adapter.phone.GooglePhoneParser
 */
@Getter
public class Telephone {

    /** Número normalizado en formato E.164 (ej: +573001234567). */
    private final String e164;

    private Telephone(String e164) {
        this.e164 = e164;
    }

    /**
     * Crea un Telephone desde un string E.164 ya validado.
     * <p>
     * Este factory se usa DESPUÉS de que {@code PhoneParser.parse()}
     * validó y normalizó el número. El dominio asume que el string
     * ya es un E.164 válido.
     *
     * @param e164 número en formato E.164 (con + inicial)
     * @return Telephone listo para usar en la entidad
     * @see com.ernesto.usermanagerapi.application.ports.drivens.PhoneParser
     */
    public static Telephone fromE164(String e164) {
        return new Telephone(e164);
    }

    /**
     * Reconstituye un Telephone desde datos persistidos.
     * <p>
     * Se usa exclusivamente en el mapper de infraestructura
     * ({@code UserSchemaMapper}) al leer desde la base de datos,
     * donde el E.164 se reconstruye desde {@code countryCode + number}.
     *
     * @param e164 número en formato E.164
     * @return Telephone reconstituido
     */
    public static Telephone reconstitute(String e164) {
        return new Telephone(e164);
    }

    /**
     * Retorna el número completo para exponer en APIs o logs.
     * <p>
     * Es un alias semántico de {@link #getE164()} para mantener
     * compatibilidad con mappers y DTOs existentes.
     *
     * @return el mismo string E.164
     */
    public String getFullNumber() {
        return e164;
    }

    /**
     * Compara este Telephone con otro por su valor E.164.
     * <p>
     * Reemplaza la sobrecarga de {@code equals()/hashCode()} eliminada
     * intencionalmente para evitar efectos secundarios en colecciones
     * y mantener el VO simple.
     *
     * @param other otro Telephone (puede ser null)
     * @return true si ambos tienen el mismo E.164
     */
    public boolean isEqual(Telephone other) {
        return other != null && this.e164.equals(other.e164);
    }

}
