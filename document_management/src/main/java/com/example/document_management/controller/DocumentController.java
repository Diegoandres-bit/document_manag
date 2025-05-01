package com.example.document_management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.document_management.service.DocumentService;

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
    
    
}