package com.example.document_management.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.example.document_management.models.Document;
import com.example.document_management.models.DocumentType;
import com.example.document_management.repo.DocumentRepository;
import com.example.document_management.repo.S3Repository;
import com.example.document_management.service.DocumentService;
import com.example.document_management.service.DocumentTypeService;

public class DocumentServiceTest {

    @InjectMocks
    private DocumentService documentService;

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private S3Repository s3Repository;

    @Mock
    private DocumentTypeService documentTypeService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadDocument() throws Exception {
        // Given
        Long userId = 1L;
        Long docTypeId = 2L;
        String fileName = "test.pdf";
        byte[] fileContent = "dummy content".getBytes();

        MockMultipartFile file = new MockMultipartFile("file", fileName, "application/pdf", fileContent);
        DocumentType docType = new DocumentType();
        docType.setDocumentTypeId(docTypeId);
        docType.setDescription("Test Type");

        when(documentTypeService.findDocumentTypeById(docTypeId)).thenReturn(Optional.of(docType));

        // When
        documentService.uploadDocument(file, docTypeId, userId);

        // Then
        verify(s3Repository, times(1)).uploadFile(
            eq("documents/" + userId + "/" + fileName),
            any(ByteArrayInputStream.class),
            any(ObjectMetadata.class)
        );
        verify(documentRepository, times(1)).save(any(Document.class));
    }

    @Test
    void testDeleteDocument_whenDocumentExists() {
        // Given
        Long documentId = 1L;
        Document doc = new Document();
        doc.setS3Path("documents/1/sample.pdf");

        when(documentRepository.findById(documentId)).thenReturn(Optional.of(doc));

        // When
        documentService.deleteDocument(documentId);

        // Then
        verify(s3Repository, times(1)).deleteFile(doc.getS3Path());
        verify(documentRepository, times(1)).deleteById(documentId);
    }

    @Test
    void testDeleteDocument_whenDocumentNotFound() {
        when(documentRepository.findById(anyLong())).thenReturn(Optional.empty());

        documentService.deleteDocument(1L);

        verifyNoInteractions(s3Repository);
        verify(documentRepository, never()).deleteById(any());
    }

    @Test
    void testGetDocument() throws Exception {
        Long docId = 1L;
        Document doc = new Document();
        doc.setS3Path("documents/1/test.pdf");

        S3Object s3Object = new S3Object();

        when(documentRepository.findById(docId)).thenReturn(Optional.of(doc));
        when(s3Repository.downloadFile(doc.getS3Path())).thenReturn(s3Object);

        S3Object result = documentService.getDocument(docId);

        assertNotNull(result);
        verify(s3Repository).downloadFile(doc.getS3Path());
    }

    @Test
    void testGetDocumentMetadata_success() throws Exception {
        Long docId = 1L;
        Document doc = new Document();
        doc.setDocumentId(docId);
        doc.setFileName("meta.txt");

        when(documentRepository.findById(docId)).thenReturn(Optional.of(doc));

        Document result = documentService.getDocumentMetadata(docId);

        assertEquals("meta.txt", result.getFileName());
    }

    @Test
    void testGetDocumentMetadata_notFound() {
        when(documentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IOException.class, () -> {
            documentService.getDocumentMetadata(1L);
        });
    }
}
