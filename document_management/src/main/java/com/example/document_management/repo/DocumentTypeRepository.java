package com.example.document_management.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.document_management.models.DocumentType;





public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long> {
    
}
