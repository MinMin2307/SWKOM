package com.fht.ocr.spring_ocr.service;

import com.fht.ocr.spring_ocr.dto.DocumentDTO;
import com.fht.ocr.spring_ocr.mapper.DocumentMapper;
import com.fht.ocr.spring_ocr.model.Document;
import com.fht.ocr.spring_ocr.repo.DokumentRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentService {
    @Autowired
    private DokumentRepo dokumentRepo;

    @Autowired
    private DocumentMapper documentMapper;

    @Transactional
    public DocumentDTO addDocument(MultipartFile file) {
        Document document = new Document();
        try {
            document.setContent(new String(file.getBytes()));  // Assuming simple text content
        } catch (IOException e) {
            throw new RuntimeException("Error processing file", e);
        }
        document = dokumentRepo.save(document);
        return documentMapper.toDto(document);
    }

    public DocumentDTO findDocumentById(Long id) {
        Document document = dokumentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        return documentMapper.toDto(document);
    }

    @Transactional
    public DocumentDTO updateDocument(Long id, MultipartFile file) {
        Document document = dokumentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        try {
            document.setContent(new String(file.getBytes()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to update file content", e);
        }
        document = dokumentRepo.save(document);
        return documentMapper.toDto(document);
    }

    public List<DocumentDTO> searchDocuments(String query) {
        List<Document> documents = dokumentRepo.findByContentContaining(query); // Ensure this method is implemented in DokumentRepo
        return documents.stream()
                .map(documentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean deleteDocument(Long id) {
        if (!dokumentRepo.existsById(id)) {
            return false;  // Or throw a custom NotFoundException
        }
        dokumentRepo.deleteById(id);
        return true;
    }


}
