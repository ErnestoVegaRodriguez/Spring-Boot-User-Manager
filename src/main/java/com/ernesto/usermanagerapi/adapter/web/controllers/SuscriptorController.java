package com.ernesto.usermanagerapi.adapter.web.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ernesto.usermanagerapi.application.services.IdempotentDeliveryService;
import com.ernesto.usermanagerapi.domain.patterns.Delivery;

/**
 * Controlador que recibe los webhooks HTTP enviados por WebhookPublisher.
 *
 * Es el punto de entrada del lado suscriptor. Su responsabilidad es:
 * 1. Recibir el Delivery<T> serializado como JSON en el body del POST
 * 2. Delegar en IdempotentDeliveryService para persistencia y encolado
 * 3. Responder 200 OK inmediatamente (el publisher no espera más que eso)
 *
 * Por qué responde 200 OK plano sin ApiResponse:
 * El publisher no es un cliente REST de nuestra API, es otro sistema (posiblemente
 * en otra tecnología como .NET, Python, Go). Solo necesita saber que el mensaje
 * fue recibido. No le interesa nuestra estructura de respuesta estándar.
 * Es un patrón fire-and-forget: el publisher envía y sigue con lo suyo.
 */
@RestController
@RequestMapping("/api/v1/webhook")
public class SuscriptorController {

    private final IdempotentDeliveryService deliveryService;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(SuscriptorController.class);

    public SuscriptorController(IdempotentDeliveryService deliveryService,
                                 ObjectMapper objectMapper) {
        this.deliveryService = deliveryService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/delivery")
    public ResponseEntity<Void> receiveDelivery(@RequestBody Delivery<?> delivery) {

        /*
         * Se serializa solo el payload (UserResponse) a JSON, no el Delivery completo.
         * Delivery ya tiene sus propios campos (id, type, emittedAt) que se
         * almacenan por separado en DeliveryAttempt. Guardar el Delivery completo
         * serializado duplicaría datos innecesariamente en la columna payload.
         *
         * Delivery<?> se usa en lugar de Delivery<UserResponse> porque el controlador
         * no necesita conocer el tipo concreto del payload. Jackson infiere el tipo
         * desde el JSON y ObjectMapper.writeValueAsString() serializa cualquier objeto.
         * Esto mantiene el controlador genérico y reutilizable para otros tipos de evento.
         */
        try {
            var payloadJson = objectMapper.writeValueAsString(delivery.getPayload());

            deliveryService.tryEnqueue(
                    delivery.getId(),
                    delivery.getType().name(),
                    payloadJson,
                    delivery.getEmittedAt());

        } catch (Exception e) {
            /*
             * Si falla la serialización del payload (extremadamente raro con records
             * simples como UserResponse), se loguea el error pero se responde OK
             * igual para no bloquear al publisher. El evento se pierde en este caso,
             * pero es un escenario aceptable para este ejercicio de prueba.
             */
            log.error("Error al serializar payload del webhook: {}", e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

}
