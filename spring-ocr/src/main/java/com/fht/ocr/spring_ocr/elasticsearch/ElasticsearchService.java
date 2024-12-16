package com.fht.ocr.spring_ocr.elasticsearch;

import com.fht.ocr.spring_ocr.elasticsearch.ElasticDocument;
import com.fht.ocr.spring_ocr.elasticsearch.ElasticDocumentRepository;
import com.fht.ocr.spring_ocr.model.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ElasticsearchService {

    @Autowired
    private ElasticDocumentRepository elasticDocumentRepository;

    public void saveDocumentToElasticsearch(Document document) {
        ElasticDocument elasticDocument = new ElasticDocument(
                document.getId(),
                document.getPath(),
                document.getExtractedText() // Assuming extractedText is set
        );
        elasticDocumentRepository.save(elasticDocument);
    }

    public List<ElasticDocument> searchByExtractedText(String keyword) {
        return elasticDocumentRepository.findByExtractedTextContaining(keyword);
    }

    public List<ElasticDocument> searchByPath(String path) {
        return elasticDocumentRepository.findByPath(path);
    }
}
