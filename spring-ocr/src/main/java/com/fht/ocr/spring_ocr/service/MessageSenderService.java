package com.fht.ocr.spring_ocr.service;

import com.fht.ocr.spring_ocr.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

//Wir brauchen einen Service damit das Dokument zum MessageBroker geschickt wird
@Service
public class MessageSenderService {


    private final RabbitTemplate rabbitTemplate;

    public MessageSenderService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    //Funktion um es in die Queue zu schicken
    //Schicken die ID und den Path vom Dokument
    //Mit dem rabbitTemplate (erstellt alle Konfiguarationen die man braucht automatisch) wird alles ereldigt
    //müssen dem rabbitTemplate sagen, welche Queue wir wollen, was wir schicken wollen (json) - DokumentId und Pfad

    public void sendDocumentMessage (Long documentId, String documentPath) {
        String json = "{\"id\":"+documentId+", \"path\":\""+documentPath+"\"}";
        rabbitTemplate.convertAndSend(RabbitMQConfig.OCR_QUEUE, json);
    }
}
