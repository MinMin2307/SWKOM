package com.fht.ocr.spring_ocr.controller;

import com.fht.ocr.spring_ocr.dto.DocumentDTO;
import com.fht.ocr.spring_ocr.model.Document;
import com.fht.ocr.spring_ocr.model.Metadata;
import com.fht.ocr.spring_ocr.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost", "http://localhost:3000"})
@RequestMapping("/document")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<DocumentDTO> uploadDocument(@RequestParam("file") MultipartFile file) {
        try {
            DocumentDTO documentDTO = documentService.addDocument(file);
            return ResponseEntity.ok(documentDTO);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
/*
    @PutMapping("/update/{id}")
    public ResponseEntity<DocumentDTO> updateDocument(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            DocumentDTO updatedDocument = documentService.updateDocument(id, file);
            return ResponseEntity.ok(updatedDocument);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
*/
    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        try {
            boolean deleted = documentService.deleteDocument(id);
            if (deleted) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<DocumentDTO>> searchDocument(@RequestParam String query) {
        try {
            List<DocumentDTO> documents = documentService.searchDocuments(query);
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PostMapping("/ocr")
    public ResponseEntity<String> processDocument(@RequestParam Long id) {
        try {
            // Implement the OCR processing logic if required
            return ResponseEntity.ok("Document successfully processed!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to process document.");
        }
    }

    @PutMapping("/metadata/{id}")
    public ResponseEntity<String> updateMetadata(@PathVariable Long id, @RequestBody Metadata metadata) {
        try {
            // Implement metadata update logic if required
            return ResponseEntity.ok("Metadata successfully updated!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to update metadata.");
        }
    }
}
