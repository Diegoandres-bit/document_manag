package com.example.document_management.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.document_management.models.DocumentType;
import com.example.document_management.repo.DocumentTypeRepository;

@Service
public class DocumentTypeService {
    
    @Autowired
    private DocumentTypeRepository documentTypeRepository;  // Repository for DocumentType entities

    /**
     * Finds a DocumentType entity by its ID.
     * 
     * @param id the ID of the document type to find
     * @return Optional containing the DocumentType if found, empty otherwise
     */
    public Optional<DocumentType> findDocumentTypeById(Long id) {
        return documentTypeRepository.findById(id);
    }
    
    /**
     * Retrieves all DocumentType entities from the database.
     * 
     * @return a List of all DocumentType objects
     */
    public List<DocumentType> getAllDocumentTypes() {
        return documentTypeRepository.findAll();
    }
}
