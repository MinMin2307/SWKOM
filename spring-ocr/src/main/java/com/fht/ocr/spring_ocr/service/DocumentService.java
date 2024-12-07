package com.fht.ocr.spring_ocr.service;

import com.fht.ocr.spring_ocr.dto.DocumentDTO;
import com.fht.ocr.spring_ocr.mapper.DocumentMapper;
import com.fht.ocr.spring_ocr.model.Document;
import com.fht.ocr.spring_ocr.repo.DocumentRepo;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    //LOG Einträge für das Senden von Dokumenten
    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);

    @Value("${file.storage.directory}")
    private String fileStorageDirectory;

    @Autowired
    private DocumentRepo documentRepo;

    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private MessageSenderService messageSenderService;

    private String sanitizeFileName(String originalFileName) {
        return originalFileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    @Transactional
    public DocumentDTO addDocument(MultipartFile file) {
        Document document = new Document();
        try {
            Path storagePath = Paths.get(fileStorageDirectory).toAbsolutePath().normalize();
            Files.createDirectories(storagePath);

            String fileName = System.currentTimeMillis() + "_" + sanitizeFileName(file.getOriginalFilename());
            Path filePath = storagePath.resolve(fileName);

            logger.debug("Saving file to path: {}", filePath);

            file.transferTo(filePath.toFile());

            document.setPath(storagePath.relativize(filePath).toString());

        } catch (IOException e) {
            logger.error("Error processing file", e);
            throw new RuntimeException("Error processing file", e);
        }

        document = documentRepo.save(document);

        try {
            logger.info("Message sent to OCR queue");
            messageSenderService.sendDocumentMessage(document.getId(), document.getPath());
        } catch (Exception e) {
            logger.error("Failed to upload document to queue", e);
        }

        return documentMapper.toDto(document);
    }

    public DocumentDTO findDocumentById(Long id) {
        Document document = documentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        return documentMapper.toDto(document);
    }

    @Transactional
    public DocumentDTO updateDocument(Long id, MultipartFile file) {
        Document document = documentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        try {
            Path storagePath = Paths.get(fileStorageDirectory);
            Files.createDirectories(storagePath);

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = storagePath.resolve(fileName);
            file.transferTo(filePath.toFile());

            document.setPath(filePath.toString());
        } catch (IOException e) {
            throw new RuntimeException("Failed to update file content", e);
        }
        document = documentRepo.save(document);
        return documentMapper.toDto(document);
    }

    public List<DocumentDTO> searchDocuments(String query) {
        List<Document> documents = documentRepo.findByPathContaining(query); // Ensure this method is implemented in DokumentRepo
        return documents.stream()
                .map(documentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean deleteDocument(Long id) {
        if (!documentRepo.existsById(id)) {
            return false;  // Or throw a custom NotFoundException
        }
        documentRepo.deleteById(id);
        return true;
    }




}
