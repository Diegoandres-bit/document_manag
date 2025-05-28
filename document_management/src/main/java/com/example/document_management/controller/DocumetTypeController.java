package com.example.document_management.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.document_management.models.DocumentType;
import com.example.document_management.service.DocumentTypeService;

/**
 * REST controller for managing document types.
 * Provides endpoints to interact with document type data.
 */
@RestController
@RequestMapping("/api/document-type") // Base URL path for all endpoints in this controller
public class DocumetTypeController { // Consider correcting the class name typo to "DocumentTypeController"

    // Injects the service layer to handle business logic
    @Autowired
    private DocumentTypeService documentTypeService;

    /**
     * GET endpoint to retrieve all document types.
     * URL: /api/document-type/all
     * @return a list of all DocumentType entities.
     */
    @GetMapping("/all")
    public List<DocumentType> getAllDocumentTypes() {
        return documentTypeService.getAllDocumentTypes();
    }
}
