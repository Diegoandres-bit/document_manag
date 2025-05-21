package com.example.document_management.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.S3Object;
import com.example.document_management.models.Document;
import com.example.document_management.service.DocumentService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    @Autowired
    private DocumentService documentService;

    @PostMapping("/upload/{documentTypeID}/{userID}")
        public ResponseEntity<String> uploadPdf(@RequestParam("file") MultipartFile file,@PathVariable Long documentTypeID, @PathVariable Long userID
    ) {
        if (!file.getContentType().equals("application/pdf")) {
            return ResponseEntity.badRequest().body("El archivo debe ser un PDF.");
        }
    
        documentService.uploadDocument(file, documentTypeID, userID);
        return ResponseEntity.ok("Archivo subido correctamente");
    }

    @DeleteMapping("/delete/{documentID}")
    public ResponseEntity<String> deleteDocument(@PathVariable Long documentID) {
        documentService.deleteDocument(documentID);
        return ResponseEntity.ok("Documento eliminado correctamente");
    }
    
    @GetMapping("/downloadDocument/{documentID}")
    public void downloadDocument(@PathVariable Long documentID, HttpServletResponse response) throws IOException {
        Document document = documentService.getDocumentMetadata(documentID);
    
        S3Object s3Object = documentService.getDocument(documentID); 
    
        response.setContentType("application/pdf");
response.setHeader("Content-Disposition", "attachment; filename=\"" + document.getFileName() + "\"");
response.setContentLengthLong(s3Object.getObjectMetadata().getContentLength());
       
    
        try (InputStream in = s3Object.getObjectContent();
             OutputStream out = response.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
        }
    }
}