package com.fht.ocr.spring_ocr.controller;

import com.fht.ocr.spring_ocr.dto.DocumentDTO;
import com.fht.ocr.spring_ocr.model.Document;
import com.fht.ocr.spring_ocr.model.Metadata;
import com.fht.ocr.spring_ocr.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost")
@RequestMapping("/document")
public class DocumentController {

    @Autowired
    private DocumentService documentService;
    @PostMapping("/upload")
    public ResponseEntity<DocumentDTO> uploadDocument(@RequestParam("file") MultipartFile file) {
        DocumentDTO documentDTO = documentService.addDocument(file);
        return ResponseEntity.ok(documentDTO);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<DocumentDTO> updateDocument(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        DocumentDTO updatedDocument = documentService.updateDocument(id, file);
        return ResponseEntity.ok(updatedDocument);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<DocumentDTO>> searchDocument(@RequestParam String query) {
        List<DocumentDTO> documents = documentService.searchDocuments(query);
        return ResponseEntity.ok(documents);
    }

    @PostMapping("/ocr")
    public ResponseEntity<String> processDocument(@RequestParam String id) {
        return ResponseEntity.ok("Document successfully processed!");
    }

    @PutMapping("/metadata/{id}")
    public ResponseEntity<String> updateMetadata(@PathVariable String id, @RequestParam("metadata") Metadata metadata) {
        return ResponseEntity.ok("Document successfully processed!");
    }
}
