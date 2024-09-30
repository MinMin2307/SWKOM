package com.fht.ocr.spring_ocr.controller;

import com.fht.ocr.spring_ocr.model.Document;
import com.fht.ocr.spring_ocr.model.Metadata;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost")
@RequestMapping("/document")
public class DocumentController {

    @PostMapping("/upload")
    public ResponseEntity<String> uploadDocument(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok("Document successfully uploaded!");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateDocument(@PathVariable String id, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok("Document successfully updated!");
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<String> deleteDocument(@PathVariable String id) {
        return ResponseEntity.ok("Document successfully deleted!");
    }

    @GetMapping("/search")
    public ResponseEntity<List<Document>> searchDocument(@RequestParam String query) {
        return ResponseEntity.ok(List.of(new Document("Document1"), new Document("Document2"),new Document( "Document3")));
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
