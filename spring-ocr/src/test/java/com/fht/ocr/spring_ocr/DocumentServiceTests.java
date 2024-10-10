package com.fht.ocr.spring_ocr;

import com.fht.ocr.spring_ocr.dto.DocumentDTO;
import com.fht.ocr.spring_ocr.model.Document;
import com.fht.ocr.spring_ocr.repo.DokumentRepo;
import com.fht.ocr.spring_ocr.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=password",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})

public class DocumentServiceTests {
    @Autowired
    private DocumentService documentService;

    @MockBean
    private DokumentRepo dokumentRepo;



        @Test
        void testAddDocument() {
            MultipartFile file = new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes());
            Document dummyDocument = new Document();
            dummyDocument.setContent("some xml");

            when(dokumentRepo.save(any(Document.class))).thenReturn(dummyDocument);

            DocumentDTO result = documentService.addDocument(file);

            assertNotNull(result);
            assertEquals("some xml", result.getContent());
        }
    }



