package com.example.document_management.controller;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.example.document_management.models.Document;
import com.example.document_management.service.DocumentService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DocumentController.class)
public class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentService documentService;

    @Test
    void uploadPdf_validPdf_shouldReturnOk() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "Dummy PDF content".getBytes()
        );

        doNothing().when(documentService).uploadDocument(any(), anyLong(), anyLong());

        mockMvc.perform(multipart("/api/documents/upload/1/1")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("Archivo subido correctamente"));
    }

    @Test
    void uploadPdf_invalidFileType_shouldReturnBadRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "This is a text file".getBytes()
        );

        mockMvc.perform(multipart("/api/documents/upload/1/1")
                        .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El archivo debe ser un PDF."));
    }

    @Test
    void deleteDocument_shouldReturnOk() throws Exception {
        doNothing().when(documentService).deleteDocument(anyLong());

        mockMvc.perform(delete("/api/documents/delete/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Documento eliminado correctamente"));
    }

    @Test
    void downloadDocument_shouldReturnPdfStream() throws Exception {
        // Mock Document metadata
        Document doc = new Document();
        doc.setFileName("test.pdf");

        when(documentService.getDocumentMetadata(anyLong())).thenReturn(doc);

        // Mock S3Object with InputStream and metadata
        S3Object s3Object = new S3Object();
        byte[] content = "PDF content".getBytes();
        InputStream stream = new ByteArrayInputStream(content);
        s3Object.setObjectContent(stream);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(content.length);
        s3Object.setObjectMetadata(metadata);

        when(documentService.getDocument(anyLong())).thenReturn(s3Object);

        mockMvc.perform(get("/api/documents/downloadDocument/1"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"test.pdf\""))
                .andExpect(content().bytes(content));
    }
}
