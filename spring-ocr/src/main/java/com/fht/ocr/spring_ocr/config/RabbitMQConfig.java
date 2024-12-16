package com.fht.ocr.spring_ocr.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class RabbitMQConfig {

    public static final String OCR_QUEUE = "ocr_queue";
    public static final String OCR_RESPONSE_QUEUE = "ocr_response_queue";

    @Bean
    public Queue getOcrQueue() {
        return new Queue(OCR_QUEUE, true);
    }

    @Bean
    public Queue getOcrResponseQueue() {
        return new Queue(OCR_RESPONSE_QUEUE, true);
    }
}
