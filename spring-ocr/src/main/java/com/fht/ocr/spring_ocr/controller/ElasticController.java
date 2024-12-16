package com.fht.ocr.spring_ocr.controller;

import com.fht.ocr.spring_ocr.elasticsearch.ElasticsearchService;
import com.fht.ocr.spring_ocr.elasticsearch.ElasticDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost", "http://localhost:3000"})
@RequestMapping("/elastic")
public class ElasticController {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @GetMapping("/search")
    public ResponseEntity<List<ElasticDocument>> searchByExtractedText(@RequestParam String keyword) {
        try {
            List<ElasticDocument> results = elasticsearchService.searchByExtractedText(keyword);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/search-by-path")
    public ResponseEntity<List<ElasticDocument>> searchByPath(@RequestParam String path) {
        try {
            List<ElasticDocument> results = elasticsearchService.searchByPath(path);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
