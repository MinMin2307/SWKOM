package com.fht.ocr.spring_ocr.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fht.ocr.spring_ocr.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OcrResponseService {

    // A thread-safe map to store OCR results with document ID as the key
    private final ConcurrentHashMap<Long, String> ocrResults = new ConcurrentHashMap<>();

    /**
     * Processes messages from the OCR response queue.
     *
     * @param message JSON message containing document ID and OCR result
     */
    @RabbitListener(queues = RabbitMQConfig.OCR_RESPONSE_QUEUE)
    public void processOcrResponse(String message) {
        try {
            // Parse message JSON: {"id":123, "ocrResult":"extracted text"}
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> result = mapper.readValue(message, new TypeReference<Map<String, Object>>() {});

            Long documentId = Long.parseLong(result.get("id").toString());
            String ocrResult = result.get("ocrResult").toString();

            // Store the OCR result in the map
            ocrResults.put(documentId, ocrResult);
            System.out.println("Received OCR result for document ID: " + documentId);
        } catch (Exception e) {
            System.err.println("Failed to process OCR response: " + e.getMessage());
        }
    }

    /**
     * Retrieves the OCR result for a given document ID.
     *
     * @param documentId the ID of the document
     * @return the OCR result or null if not available
     */
    public String getOcrResult(Long documentId) {
        return ocrResults.get(documentId);
    }

    /**
     * Removes the OCR result from the map after it's been processed.
     *
     * @param documentId the ID of the document
     */
    public void removeOcrResult(Long documentId) {
        ocrResults.remove(documentId);
    }
}
