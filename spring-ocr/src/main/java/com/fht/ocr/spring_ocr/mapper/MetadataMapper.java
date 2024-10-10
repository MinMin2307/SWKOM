package com.fht.ocr.spring_ocr.mapper;

import com.fht.ocr.spring_ocr.dto.MetadataDTO;
import com.fht.ocr.spring_ocr.model.Metadata;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MetadataMapper {
    MetadataDTO toDto(Metadata metadata);
    Metadata toEntity(MetadataDTO metadataDTO);
}