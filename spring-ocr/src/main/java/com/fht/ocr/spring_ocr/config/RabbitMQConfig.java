package com.fht.ocr.spring_ocr.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


//Projekt
//Web UI in React - > File Upload.
//Es landet im Springboot, bevor wir es abspeichern, passiert parallel etwas.
//Wir geben dieses Dokument in eine OCR_QUEUE, dieses Dokument landetet beim OCR Worker, der wird aus diesem Dokument
//dann text  herausextrahieren und uns den Text zurückschicken (Das ist alles noch nicht erledigt)


@Configuration
public class RabbitMQConfig {
//Konfigurationsklasse für RabbitMQ, sagt wie die Queue heißt.

    //Die Queue wird benutzt um die ID und den Pfad der Dokumente, an unseren OCR Worker (existiert noch nicht) zu schicken
    //Die Queue wiird bei der MessageSendService verwendet
    public static final String OCR_QUEUE = "ocr_queue";
    public static final String OCR_RESPONSE_QUEUE = "ocr_response_queue";


    //Bean -> Wiederverwendbare Library-Komponents
    //Queue -> first in first out / Queue für die Dokumente, damit die DOkumente zum OCR
    @Bean
    public Queue getOcrQueue() {
        return new Queue(OCR_QUEUE, true);
    }

    @Bean
    public Queue getOcrResponseQueue() {
        return new Queue(OCR_RESPONSE_QUEUE, true);
    }
}
