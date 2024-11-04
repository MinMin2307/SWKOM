package com.fht.ocr.spring_ocr.repo;

import com.fht.ocr.spring_ocr.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepo extends JpaRepository<Document, Long> {
    List<Document> findByPathContaining(String content);
}
