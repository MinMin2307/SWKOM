package com.fht.ocr.spring_ocr.repo;

import com.fht.ocr.spring_ocr.model.Metadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetadataRepo extends JpaRepository<Metadata, Long> {

}
