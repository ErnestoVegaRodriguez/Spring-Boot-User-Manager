package com.ernesto.usermanagerapi.application.ports.drivens;

import com.ernesto.usermanagerapi.domain.exceptions.ValidationException;
import com.ernesto.usermanagerapi.domain.patterns.Result;

/**
 * Puerto de infraestructura para parsear y validar números telefónicos.
 * <p>
 * Esta interfaz pertenece a la capa de aplicación (driven port) y
 * permite que el dominio {@code Telephone} se mantenga limpio de
 * dependencias externas. La implementación concreta (eg. libphonenumber
 * de Google) vive en {@code adapter.phone}.
 * <p>
 * <b>Flujo típico:</b>
 * <ol>
 *   <li>El use case recibe un raw {@code String} del controller</li>
 *   <li>Llama a {@link #parse(String, String)} con el raw + región</li>
 *   <li>Si es válido, crea {@code Telephone.fromE164(parsedPhone.e164())}</li>
 *   <li>Si no es válido, propaga el {@code ValidationException}</li>
 * </ol>
 *
 * @see com.ernesto.usermanagerapi.domain.values.Telephone
 */
public interface PhoneParser {

    /**
     * Resultado del parseo exitoso de un número telefónico.
     *
     * @param e164 número normalizado en formato E.164 (ej: +573001234567)
     */
    record ParsedPhone(String e164) {}

    /**
     * Parsea y valida un número telefónico raw.
     * <p>
     * El {@code defaultRegion} es el código ISO 3166-1 alpha-2
     * (ej: "CO" para Colombia, "US" para Estados Unidos) que ayuda
     * al parser a interpretar números sin prefijo internacional.
     * <p>
     * Si el número es válido, retorna un {@link ParsedPhone} con el
     * E.164 normalizado. Si no, retorna un {@link ValidationException}
     * con el detalle del error.
     *
     * @param raw           número telefónico sin normalizar (puede contener
     *                      espacios, guiones, paréntesis)
     * @param defaultRegion código de país ISO 3166-1 alpha-2 para
     *                      interpretar números sin "+"
     * @return {@code Result.success(ParsedPhone)} si es válido,
     *         {@code Result.failure(ValidationException)} si no
     */
    Result<ParsedPhone, ValidationException> parse(String raw, String defaultRegion);

}
