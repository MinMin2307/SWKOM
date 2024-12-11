package com.fht.ocr.spring_ocr.service;

import com.fht.ocr.spring_ocr.dto.DocumentDTO;
import com.fht.ocr.spring_ocr.mapper.DocumentMapper;
import com.fht.ocr.spring_ocr.model.Document;
import com.fht.ocr.spring_ocr.repo.DocumentRepo;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);

    @Value("${minio.bucket.name}")
    private String bucketName;

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.access.key}")
    private String accessKey;

    @Value("${minio.secret.key}")
    private String secretKey;

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

        try (InputStream fileInputStream = file.getInputStream()) {
            String fileName = System.currentTimeMillis() + "_" + sanitizeFileName(file.getOriginalFilename());

            // Create MinIO client
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(minioUrl)
                    .credentials(accessKey, secretKey)
                    .build();

            // Upload file to MinIO bucket
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(fileInputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            logger.info("File uploaded successfully to MinIO bucket: {}", bucketName);

            // Save file metadata in the database
            document.setPath(fileName);
            document = documentRepo.save(document);

            // Send message to OCR queue
            logger.info("Message sent to OCR queue");
            messageSenderService.sendDocumentMessage(document.getId(), document.getPath());

        } catch (MinioException e) {
            logger.error("MinIO error occurred", e);
            throw new RuntimeException("Failed to upload file to MinIO", e);
        } catch (Exception e) {
            logger.error("Error processing file", e);
            throw new RuntimeException("Error processing file", e);
        }

        return documentMapper.toDto(document);
    }

    public DocumentDTO findDocumentById(Long id) {
        Document document = documentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        return documentMapper.toDto(document);
    }

    public List<DocumentDTO> searchDocuments(String query) {
        List<Document> documents = documentRepo.findByPathContaining(query);
        return documents.stream()
                .map(documentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean deleteDocument(Long id) {
        if (!documentRepo.existsById(id)) {
            return false;
        }
        documentRepo.deleteById(id);
        return true;
    }
}
