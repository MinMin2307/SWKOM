package com.fht.ocr.spring_ocr.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "documents") // Specify the index name
public class ElasticDocument {
    @Id
    private Long id;
    private String path;
    private String extractedText;
}
