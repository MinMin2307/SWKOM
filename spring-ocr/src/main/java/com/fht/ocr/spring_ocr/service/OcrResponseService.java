package com.fht.ocr.spring_ocr.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fht.ocr.spring_ocr.config.RabbitMQConfig;
import com.fht.ocr.spring_ocr.elasticsearch.ElasticsearchService;
import com.fht.ocr.spring_ocr.model.Document;
import com.fht.ocr.spring_ocr.repo.DocumentRepo;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OcrResponseService {

    // A thread-safe map to store OCR results with document ID as the key
    private final ConcurrentHashMap<Long, String> ocrResults = new ConcurrentHashMap<>();

    @Autowired
    private DocumentRepo documentRepo;

    @Autowired
    private ElasticsearchService elasticsearchService;
    /**
     * Processes messages from the OCR response queue.
     *
     * @param message JSON message containing document ID and OCR result
     */
    @RabbitListener(queues = RabbitMQConfig.OCR_RESPONSE_QUEUE)
    public void processOcrResponse(String message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> result = mapper.readValue(message, new TypeReference<Map<String, Object>>() {});

            Long documentId = Long.parseLong(result.get("id").toString());
            String ocrResult = result.get("ocrResult").toString();
            System.out.println(ocrResult);
            // Update the Document entity
            Document document = documentRepo.findById(documentId)
                    .orElseThrow(() -> new RuntimeException("Document not found"));

            document.setExtractedText(ocrResult);
            documentRepo.save(document);

            // Save to Elasticsearch
            elasticsearchService.saveDocumentToElasticsearch(document);

            System.out.println("Processed OCR result for document ID: " + documentId);
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
