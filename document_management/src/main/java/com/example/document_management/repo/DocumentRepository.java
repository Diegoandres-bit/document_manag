package com.example.document_management.repo;

import com.example.document_management.models.Document;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long>{
    
    
}
