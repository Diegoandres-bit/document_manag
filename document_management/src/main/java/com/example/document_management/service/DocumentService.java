package com.example.document_management.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.services.s3.model.S3Object;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.document_management.models.Document;
import com.example.document_management.repo.DocumentRepository;
import com.example.document_management.repo.S3Repository;
@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;  // JPA repository for Document entities

    @Autowired
    private S3Repository s3Repository;  // Custom repository for AWS S3 operations

    @Autowired
    private DocumentTypeService documentTypeService;  // Service to fetch document type details

    /**
     * Uploads a document to S3 and saves its metadata in the database.
     * 
     * @param file           The uploaded file (MultipartFile)
     * @param documentTypeId The ID of the document type to associate
     * @param userID         The ID of the user uploading the document
     */
    public void uploadDocument(MultipartFile file, Long documentTypeId, Long userID) {
        InputStream inputStream;
        try {
            // Get input stream from the uploaded file
            inputStream = file.getInputStream();
        } catch (IOException e) {
            // Wrap checked IOException in a runtime exception to propagate
            throw new RuntimeException(e);
        }

        // Prepare S3 metadata with file size and content type
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        // Create a new Document entity and set its properties
        Document document = new Document();
        document.setUserId(userID);
        // Fetch document type description from service using the provided ID
        document.setDocumentType(documentTypeService.findDocumentTypeById(documentTypeId).get().getDescription());
        document.setFileName(file.getOriginalFilename());
        document.setUploadDate(LocalDate.now());
        document.setSize(file.getSize());
        document.setDescription("idk");  // Placeholder description
        // Store the whole DocumentType entity in the Document (assuming a relation)
        document.setDocumentTypeId(documentTypeService.findDocumentTypeById(documentTypeId).get());
        // Construct the S3 path using user ID and file name
        document.setS3Path("documents/" + userID + "/" + document.getFileName());

        // Upload the file to S3
        s3Repository.uploadFile(document.getS3Path(), inputStream, metadata);

        // Save document metadata to database
        documentRepository.save(document);
    }

    /**
     * Deletes a document both from S3 and the database by its ID.
     * 
     * @param documentId The ID of the document to delete
     */
    public void deleteDocument(Long documentId) {
        // Find the document entity by ID
        Document document = documentRepository.findById(documentId).orElse(null);
        if (document != null) {
            // Delete the file from S3 using the stored S3 path
            s3Repository.deleteFile(document.getS3Path());
            // Delete the document record from the database
            documentRepository.deleteById(documentId);
        }
    }

    /**
     * Retrieves the document file from S3.
     * 
     * @param documentId The ID of the document to fetch
     * @return The S3Object representing the file stream and metadata
     * @throws IOException if the document is not found or S3 access fails
     */
    public S3Object getDocument(Long documentId) throws IOException {
        // Fetch document metadata from DB or throw exception if not found
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new FileNotFoundException("Documento no encontrado con ID: " + documentId));
    
        // Download the file from S3 using the stored path
        return s3Repository.downloadFile(document.getS3Path());
    }

    /**
     * Retrieves only the metadata of the document by its ID.
     * 
     * @param documentId The document's ID
     * @return The Document entity containing metadata
     * @throws IOException if the document is not found
     */
    public Document getDocumentMetadata(Long documentId) throws IOException {
        // Fetch the document metadata or throw exception if missing
        return documentRepository.findById(documentId)
            .orElseThrow(() -> new FileNotFoundException("Documento no encontrado con ID: " + documentId));
    }
}
