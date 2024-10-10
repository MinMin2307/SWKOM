package com.fht.ocr.spring_ocr.mapper;

import com.fht.ocr.spring_ocr.dto.DocumentDTO;
import com.fht.ocr.spring_ocr.model.Document;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DocumentMapper {
    DocumentDTO toDto(Document document);
    Document toEntity(DocumentDTO documentDTO);
}
