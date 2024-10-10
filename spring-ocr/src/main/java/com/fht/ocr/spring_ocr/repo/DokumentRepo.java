package com.fht.ocr.spring_ocr.repo;

import com.fht.ocr.spring_ocr.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DokumentRepo extends JpaRepository<Document, Long> {
    List<Document> findByContentContaining(String content);
}
