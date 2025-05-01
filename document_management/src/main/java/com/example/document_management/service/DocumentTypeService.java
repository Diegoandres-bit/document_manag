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
    private DocumentTypeRepository documentTypeRepository;

    public Optional<DocumentType> findDocumentTypeById(Long id) {
        return documentTypeRepository.findById(id);
    }
     
    public List<DocumentType> getAllDocumentTypes() {
        return documentTypeRepository.findAll();
    }
}
