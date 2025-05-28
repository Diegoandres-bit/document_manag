package com.example.document_management.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.example.document_management.models.DocumentType;
import com.example.document_management.repo.DocumentTypeRepository;
import com.example.document_management.service.DocumentTypeService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class DocumentTypeServiceTest {

    @Mock
    private DocumentTypeRepository documentTypeRepository;

    @InjectMocks
    private DocumentTypeService documentTypeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindDocumentTypeById_found() {
        // Arrange
        DocumentType docType = new DocumentType();
        docType.setDocumentTypeId(1L);
        docType.setDescription("Passport");

        when(documentTypeRepository.findById(1L)).thenReturn(Optional.of(docType));

        // Act
        Optional<DocumentType> result = documentTypeService.findDocumentTypeById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Passport", result.get().getDescription());
        verify(documentTypeRepository, times(1)).findById(1L);
    }

    @Test
    void testFindDocumentTypeById_notFound() {
        // Arrange
        when(documentTypeRepository.findById(2L)).thenReturn(Optional.empty());

        // Act
        Optional<DocumentType> result = documentTypeService.findDocumentTypeById(2L);

        // Assert
        assertFalse(result.isPresent());
        verify(documentTypeRepository, times(1)).findById(2L);
    }

    @Test
    void testGetAllDocumentTypes() {
        // Arrange
        DocumentType type1 = new DocumentType();
        type1.setDocumentTypeId(1L);
        type1.setDescription("Passport");

        DocumentType type2 = new DocumentType();
        type2.setDocumentTypeId(2L);
        type2.setDescription("License");

        when(documentTypeRepository.findAll()).thenReturn(Arrays.asList(type1, type2));

        // Act
        List<DocumentType> result = documentTypeService.getAllDocumentTypes();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Passport", result.get(0).getDescription());
        assertEquals("License", result.get(1).getDescription());
        verify(documentTypeRepository, times(1)).findAll();
    }
}
