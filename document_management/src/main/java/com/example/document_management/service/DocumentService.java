package com.example.document_management.service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.services.s3.model.S3Object;
import java.nio.file.Paths;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.document_management.models.Document;
import com.example.document_management.repo.DocumentRepository;
import com.example.document_management.repo.S3Repository;
@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private S3Repository s3Repository;

    @Autowired
    private DocumentTypeService documentTypeService;

    public void uploadDocument(MultipartFile file, Long documentTypeId, Long userID) {
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        Document document = new Document();
        document.setUserId(userID);
        document.setDocumentType(documentTypeService.findDocumentTypeById(documentTypeId).get().getDescription());
        document.setFileName(file.getOriginalFilename());
        document.setUploadDate(LocalDate.now());
        document.setSize(file.getSize());
        document.setDescription("idk");
        document.setDocumentTypeId(documentTypeService.findDocumentTypeById(documentTypeId).get());
        document.setS3Path("documents/"+userID+"/"+document.getFileName());    
        s3Repository.uploadFile(document.getS3Path(), inputStream, metadata);

        documentRepository.save(document);
    }
    public void deleteDocument(Long documentId) {
        Document document = documentRepository.findById(documentId).orElse(null);
        if (document != null) {
            s3Repository.deleteFile(document.getS3Path());
            documentRepository.deleteById(documentId);
        }
    }
   

    public S3Object getDocument(Long documentId) throws IOException {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new FileNotFoundException("Documento no encontrado con ID: " + documentId));
    
        return s3Repository.downloadFile(document.getS3Path());
    }
    public Document getDocumentMetadata(Long documentId) throws IOException {
        return documentRepository.findById(documentId)
            .orElseThrow(() -> new FileNotFoundException("Documento no encontrado con ID: " + documentId));
    }
}
