package com.ernesto.usermanagerapi.domain.patterns;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ernesto.usermanagerapi.domain.enums.DeliveryType;

import lombok.Getter;

/**
 * Envoltura genérica para un evento que viaja desde el publisher hasta el suscriptor.
 *
 * TPayload es el tipo del contenido del evento (ej: UserResponse).
 * Delivery es un objeto de tránsito: se serializa a JSON, viaja por HTTP y
 * contiene la metadata necesaria para que el suscriptor procese el evento.
 *
 * @param <TPayload> Tipo del payload del evento
 */
@Getter
public class Delivery<TPayload> {

    private final UUID id;
    private final DeliveryType type;
    private final TPayload payload;
    private final Instant emittedAt;

    /*
     * Se usa Instant en lugar de LocalDateTime porque Instant representa un momento
     * específico en la línea de tiempo universal (UTC), independiente de zonas horarias.
     *
     * LocalDateTime no tiene zona horaria: "2026-06-22T10:30:00" no dice si es UTC-5,
     * UTC+2, etc. Para eventos distribuidos (webhooks, colas), Instant es la opción
     * correcta porque:
     * - Es timezone-agnostic: todos los sistemas lo interpretan igual
     * - Jackson lo serializa como ISO-8601: "2026-06-22T15:30:00Z"
     * - Evita errores de huso horario cuando publisher y subscriber corren en
     *   zonas horarias distintas
     */

    // Constructor usado por create() — genera id y emittedAt automáticamente
    private Delivery(DeliveryType type, TPayload payload) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.payload = payload;
        this.emittedAt = Instant.now();
    }

    /*
     * Constructor usado por Jackson para deserializar el JSON que llega al
     * SuscriptorController vía HTTP.
     *
     * @JsonCreator + @JsonProperty le indican a Jackson qué constructor usar y
     * cómo mapear cada campo del JSON al parámetro correspondiente.
     * Sin esto, Jackson no podría instanciar Delivery porque los campos son
     * final y no hay setters.
     */
    @JsonCreator
    private Delivery(
            @JsonProperty("id") UUID id,
            @JsonProperty("type") DeliveryType type,
            @JsonProperty("payload") TPayload payload,
            @JsonProperty("emittedAt") Instant emittedAt) {
        this.id = id;
        this.type = type;
        this.payload = payload;
        this.emittedAt = emittedAt;
    }

    /**
     * Factory method de creación. Único punto de entrada para crear un Delivery
     * desde el código del publisher.
     */
    public static <TPayload> Delivery<TPayload> create(DeliveryType type, TPayload payload) {
        return new Delivery<>(type, payload);
    }

}
