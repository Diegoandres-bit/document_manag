package com.example.document_management.controller;

import com.example.document_management.models.DocumentType;
import com.example.document_management.service.DocumentTypeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DocumetTypeController.class)
class DocumetTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentTypeService documentTypeService;

    @Test
    void testGetAllDocumentTypes() throws Exception {
        // Crear datos de prueba
        DocumentType docType1 = new DocumentType();
        docType1.setDocumentTypeId(1L);
        docType1.setDescription("Invoice");

        DocumentType docType2 = new DocumentType();
        docType2.setDocumentTypeId(2L);
        docType2.setDescription("Contract");

        List<DocumentType> allTypes = Arrays.asList(docType1, docType2);

        // Mockear el servicio
        Mockito.when(documentTypeService.getAllDocumentTypes()).thenReturn(allTypes);

        // Realizar la petición GET y validar la respuesta
        mockMvc.perform(get("/api/document-type/all")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].documentTypeId", is(1)))
            .andExpect(jsonPath("$[0].description", is("Invoice")))
            .andExpect(jsonPath("$[1].documentTypeId", is(2)))
            .andExpect(jsonPath("$[1].description", is("Contract")));
    }
}
