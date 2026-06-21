package com.ernesto.usermanagerapi.adapter.phone;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.ernesto.usermanagerapi.application.ports.drivens.PhoneParser;
import com.ernesto.usermanagerapi.domain.enums.ErrorCode;
import com.ernesto.usermanagerapi.domain.exceptions.ValidationException;
import com.ernesto.usermanagerapi.domain.patterns.Result;
import com.ernesto.usermanagerapi.domain.patterns.ValidationItem;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

/**
 * Implementación de {@link PhoneParser} usando la librería
 * <a href="https://github.com/google/libphonenumber">libphonenumber</a>
 * de Google.
 * <p>
 * libphonenumber es la misma librería que Google usa internamente en
 * Android, Chrome, Google Contacts y Google Voice. Soporta:
 * <ul>
 *   <li><b>Parsing:</b> interpreta números con o sin prefijo internacional,
 *       con espacios, guiones, paréntesis</li>
 *   <li><b>Validación por país:</b> conoce la estructura de numeración
 *       de cada país (códigos de área, largos de números, prefijos
 *       móviles/fijos)</li>
 *   <li><b>Formateo:</b> E.164, Internacional, Nacional, RFC3966</li>
 *   <li><b>Tipos de línea:</b> MOBILE, FIXED_LINE, VOIP, TOLL_FREE, etc</li>
 * </ul>
 * <p>
 * <b>Uso:</b>
 * <pre>{@code
 * PhoneNumberUtil util = PhoneNumberUtil.getInstance();
 * PhoneNumber number = util.parse("3001234567", "CO");
 * util.isValidNumber(number)   // → true
 * util.format(number, PhoneNumberFormat.E164)  // → "+573001234567"
 * util.format(number, PhoneNumberFormat.INTERNATIONAL)  // → "+57 300 1234567"
 * util.getNumberType(number)  // → PhoneNumberType.MOBILE
 * }</pre>
 * <p>
 * <b>Thread-safe:</b> {@link PhoneNumberUtil#getInstance()} retorna
 * un singleton thread-safe. Se puede llamar sin sincronización.
 *
 * @see <a href="https://github.com/google/libphonenumber">Google libphonenumber</a>
 * @see PhoneNumberUtil
 * @see PhoneNumberFormat#E164
 */
@Service
@Scope("singleton")
public class GooglePhoneParser implements PhoneParser {

    private final PhoneNumberUtil util = PhoneNumberUtil.getInstance();

    /**
     * Parsea y valida un número telefónico usando libphonenumber.
     * <p>
     * <b>Proceso:</b>
     * <ol>
     *   <li>Valida que el raw no sea null/blank (validación básica)</li>
     *   <li>{@link PhoneNumberUtil#parse(String, String)} interpreta
     *       el número. Si tiene "+", ignora la región; si no, asume
     *       la región por defecto</li>
     *   <li>{@link PhoneNumberUtil#isValidNumber(PhoneNumber)} verifica
     *       contra los metadatos del país (código de área, longitud,
     *       prefijos válidos)</li>
     *   <li>{@link PhoneNumberUtil#format(PhoneNumber, PhoneNumberFormat)}
     *       normaliza a E.164</li>
     * </ol>
     *
     * @param raw           número crudo: "+573001234567", "3001234567",
     *                      "57 300 123 4567", "(300) 123-4567", etc.
     * @param defaultRegion código ISO 3166-1 alpha-2 (ej: "CO" para
     *                      Colombia, "US" para USA). Se usa cuando
     *                      el raw no comienza con "+"
     * @return ParsedPhone con E.164 si es válido,
     *         ValidationException con detalle si no
     */
    @Override
    public Result<ParsedPhone, ValidationException> parse(String raw, String defaultRegion) {

        // --- Paso 1: validación básica de null/vacío ---
        if (raw == null || raw.isBlank()) {
            return Result.failure(new ValidationException(
                    ErrorCode.VALIDATION_ERROR,
                    "Phone validation failed.",
                    List.of(ValidationItem.of(Map.of("telephone",
                            "The phone number cannot be left blank")))));
        }

        // --- Paso 2: parsear con libphonenumber ---
        // PhoneNumberUtil.parse() interpreta el string:
        //   - Si empieza con "+" → parsea el código de país del prefijo
        //   - Si NO empieza con "+" → usa defaultRegion para inferir
        // Lanza NumberParseException si el formato es irreconocible
        try {
            PhoneNumber number = util.parse(raw, defaultRegion);

            // --- Paso 3: validar contra metadatos del país ---
            // isValidNumber() chequea que el código de país, código de
            // área (si aplica) y longitud del número sean válidos para
            // ese país. NO es solo un regex — usa los metadatos oficiales
            // de la UIT (Unión Internacional de Telecomunicaciones).
            if (!util.isValidNumber(number)) {
                String formatted = util.format(number, PhoneNumberFormat.INTERNATIONAL);
                return Result.failure(new ValidationException(
                        ErrorCode.VALIDATION_ERROR,
                        "Phone validation failed.",
                        List.of(ValidationItem.of(Map.of("telephone",
                                "The phone number is not valid: " + formatted)))));
            }

            // --- Paso 4: normalizar a E.164 ---
            // PhoneNumberFormat.E164 produce: +573001234567
            // Este es el formato estándar internacional sin espacios ni
            // caracteres especiales. Es el que almacena el dominio.
            String e164 = util.format(number, PhoneNumberFormat.E164);
            return Result.success(new ParsedPhone(e164));

        } catch (NumberParseException e) {
            // Número con formato irreconocible (letras, demasiados
            // dígitos, código de país inválido, etc.)
            return Result.failure(new ValidationException(
                    ErrorCode.VALIDATION_ERROR,
                    "Phone validation failed.",
                    List.of(ValidationItem.of(Map.of("telephone",
                            "The phone number format is invalid: " + raw)))));
        }
    }

}
