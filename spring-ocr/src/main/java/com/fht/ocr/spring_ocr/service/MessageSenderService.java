package com.fht.ocr.spring_ocr.service;

import com.fht.ocr.spring_ocr.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageSenderService {

    private final RabbitTemplate rabbitTemplate;

    public MessageSenderService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendDocumentMessage (Long documentId, String documentPath) {
        String json = "{\"id\":"+documentId+", \"path\":"+documentPath+"}";
        rabbitTemplate.convertAndSend(RabbitMQConfig.OCR_QUEUE, json);
    }
}
