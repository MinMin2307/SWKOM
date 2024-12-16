package com.fht.ocr.spring_ocr.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElasticDocumentRepository extends ElasticsearchRepository<ElasticDocument, Long> {
    List<ElasticDocument> findByExtractedTextContaining(String keyword);
    List<ElasticDocument> findByPath(String path);

}
