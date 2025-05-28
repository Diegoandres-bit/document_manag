package com.example.document_management.controller;

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

/**
 * Controller responsible for handling HTTP requests related to document operations,
 * including uploading, deleting, and downloading documents.
 */
@RestController
@RequestMapping("/api/documents") // Base path for all document endpoints
public class DocumentController {

    // Inject the DocumentService to handle business logic related to documents
    @Autowired
    private DocumentService documentService;

    /**
     * Handles POST requests for uploading PDF documents.
     * 
     * @param file the uploaded file received from the client (must be PDF)
     * @param documentTypeID the ID of the document type for classification
     * @param userID the ID of the user uploading the document
     * @return ResponseEntity with status and message depending on success or failure
     */
    @PostMapping("/upload/{documentTypeID}/{userID}")
    public ResponseEntity<String> uploadPdf(@RequestParam("file") MultipartFile file,
                                            @PathVariable Long documentTypeID,
                                            @PathVariable Long userID) {
        // Check if the file's MIME type is exactly "application/pdf"
        if (!file.getContentType().equals("application/pdf")) {
            // If not, return HTTP 400 Bad Request with a message
            return ResponseEntity.badRequest().body("El archivo debe ser un PDF.");
        }

        // If valid, delegate the upload process to the service layer
        documentService.uploadDocument(file, documentTypeID, userID);

        // Return HTTP 200 OK with success message
        return ResponseEntity.ok("Archivo subido correctamente");
    }

    /**
     * Handles DELETE requests to remove a document by its ID.
     * 
     * @param documentID the ID of the document to be deleted
     * @return ResponseEntity with a success message
     */
    @DeleteMapping("/delete/{documentID}")
    public ResponseEntity<String> deleteDocument(@PathVariable Long documentID) {
        // Delegate the deletion process to the service
        documentService.deleteDocument(documentID);

        // Return HTTP 200 OK with confirmation
        return ResponseEntity.ok("Documento eliminado correctamente");
    }

    /**
     * Handles GET requests to download a document by its ID.
     * Streams the PDF file directly to the HTTP response output stream.
     * 
     * @param documentID the ID of the document to download
     * @param response HttpServletResponse to write the file content to
     * @throws IOException if reading or writing the stream fails
     */
    @GetMapping("/downloadDocument/{documentID}")
    public void downloadDocument(@PathVariable Long documentID, HttpServletResponse response) throws IOException {
        // Get the document metadata (e.g. filename, size)
        Document document = documentService.getDocumentMetadata(documentID);

        // Retrieve the actual file from S3 storage
        S3Object s3Object = documentService.getDocument(documentID);

        // Set the HTTP response content type to PDF
        response.setContentType("application/pdf");

        // Set header to indicate this is an attachment with the original filename
        response.setHeader("Content-Disposition", "attachment; filename=\"" + document.getFileName() + "\"");

        // Set content length to the file size from S3 metadata
        response.setContentLengthLong(s3Object.getObjectMetadata().getContentLength());

        // Stream the file content from S3 to the HTTP response output stream
        try (InputStream in = s3Object.getObjectContent();
             OutputStream out = response.getOutputStream()) {

            byte[] buffer = new byte[8192]; // 8KB buffer for efficient streaming
            int bytesRead;

            // Read bytes from the input stream and write to output until done
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            // Flush any remaining bytes in output stream
            out.flush();
        }
    }
}
