
package com.example.document_management.models;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

/**
 * Entity class representing a document in the system.
 * This class manages document metadata and storage information.
 */
@Data
@Entity
public class Document {
    /** Unique identifier for the document */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    private Long documentId;

    /** ID of the user who owns this document */
    @Column(name = "user_id")
    private Long userId;
    
    /** Reference to the document type entity */
    @ManyToOne
    @JoinColumn(name = "document_type_id")
    private DocumentType documentTypeId;

    /** Type of the document (e.g., PDF, DOC) */
    private String documentType;
    
    /** Original name of the uploaded file */
    private String fileName;

    /** Path where the document is stored in S3 */
    @Column(name = "s3_path")
    private String s3Path;

    /** Date when the document was uploaded */
    @Column(name = "upload_date")
    private LocalDate uploadDate;

    /** Size of the document in bytes */
    private long size;

    /** User-provided description of the document */
    private String description;

    /** Timestamp when the document was created */
    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    /** Timestamp when the document was last updated */
    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;
}

