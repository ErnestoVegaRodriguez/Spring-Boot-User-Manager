package com.ernesto.usermanagerapi.adapter.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configura los elementos fundamentales de RabbitMQ para el webhook:
 * Exchange, Cola, Binding y MessageConverter.
 *
 * Con @EnableRabbit, Spring escanea @RabbitListener en los beans y registra
 * los consumers necesarios. Sin esta anotación, las colas se declaran pero
 * los listeners nunca se activan.
 */
@EnableRabbit
@Configuration
public class RabbitMQConfig {

    // ─────────────────────────────────────────────────────────────
    // Exchange
    // ─────────────────────────────────────────────────────────────
    /*
     * Exchange de tipo DIRECT: enruta mensajes a la cola cuya routing key
     * coincida EXACTAMENTE con la routing key del mensaje.
     *
     * Diferencias con otros tipos:
     * - Fanout: ignora la routing key, envía a TODAS las colas bindeadas.
     *   Útil para broadcast puro (ej: notificaciones globales).
     * - Topic: usa patrones con comodines (ej: "pedidos.*").
     *   Útil cuando múltiples consumidores filtran por categoría.
     * - Direct: coincidencia exacta. Simple, predecible y eficiente.
     *
     * Para nuestro caso:
     * - El SuscriptorController publica con routing key = eventType (ej: "CREATED_USER")
     * - La cola "pedidos.creado.queue" está bindeada con routing key "CREATED_USER"
     * - Solo los mensajes de creación de usuario llegan a esta cola
     * - Si mañana agregamos "DELETED_USER", creamos otra cola bindeada con esa key
     */
    @Bean
    public DirectExchange webhookExchange() {
        return new DirectExchange("webhook.exchange");
    }

    // ─────────────────────────────────────────────────────────────
    // Cola
    // ─────────────────────────────────────────────────────────────
    /*
     * Cola unificada para eventos de pedidos/usuarios creados.
     * El nombre usa convención de puntos (pedidos.creado.queue) porque
     * es legible, escalable por dominio, y consistente con la nomenclatura
     * estándar de RabbitMQ en la industria.
     *
     * Parámetros por defecto:
     * - durable = true  → la cola sobrevive a reinicios de RabbitMQ
     * - exclusive = false → varios consumers pueden conectarse
     * - autoDelete = false → la cola persiste aunque no haya consumers
     */
    @Bean
    public Queue pedidosCreadoQueue() {
        return new Queue("pedidos.creado.queue", true);
    }

    // ─────────────────────────────────────────────────────────────
    // Binding
    // ─────────────────────────────────────────────────────────────
    /*
     * El Binding conecta la cola al exchange con una routing key específica.
     * Solo los mensajes publicados en webhook.exchange con routing key
     * "CREATED_USER" serán encolados en pedidos.creado.queue.
     *
     * La routing key coincide con DeliveryType.CREATED_USER.name() para
     * mantener coherencia entre el código y la configuración de infraestructura.
     */
    @Bean
    public Binding binding(Queue pedidosCreadoQueue, DirectExchange webhookExchange) {
        return BindingBuilder
                .bind(pedidosCreadoQueue)
                .to(webhookExchange)
                .with("CREATED_USER");
    }

    // ─────────────────────────────────────────────────────────────
    // Message Converter
    // ─────────────────────────────────────────────────────────────
    /*
     * Jackson2JsonMessageConverter le dice a Spring AMQP que serialice/deserialice
     * los mensajes como JSON en lugar de usar la serialización nativa de Java
     * (ObjectOutputStream).
     *
     * Ventajas sobre la serialización nativa:
     * - El mensaje es legible en la consola de RabbitMQ
     * - Cualquier tecnología (.NET, Python, Go) puede consumir el mensaje
     * - Los objetos no necesitan implementar Serializable
     * - Jackson maneja Instant, UUID, records y enums sin configuración extra
     *
     * Sin este bean, @RabbitListener fallaría al intentar deserializar
     * DeliveryAttempt porque Spring AMQP usaría Java serialization por defecto.
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
