package com.github.heroslender.hero_api.controller;

import com.github.heroslender.hero_api.exceptions.StorageFileNotFoundException;
import com.github.heroslender.hero_api.service.PluginVersionStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class PluginVersionControllerTest {
    private static final String PLUGIN_NAME = "crates";
    private static final String PLUGIN_VERSION = "v1.0";
    private static final String PLUGIN_VERSION_FILE = PLUGIN_NAME + "-" + PLUGIN_VERSION + ".jar";
    private static final String BASE_PATH = "/plugins/" + PLUGIN_NAME + "/versions/" + PLUGIN_VERSION;

    @Autowired
    private MockMvc mvc;
    @MockitoBean
    private PluginVersionStorageService storageService;

    @Test
    public void shouldSaveUploadedFile() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.txt",
                "text/plain", "Spring Framework".getBytes());

        this.mvc.perform(multipart(BASE_PATH + "/upload").file(multipartFile))
                .andExpect(status().isFound());

        then(this.storageService).should().store(PLUGIN_VERSION_FILE, multipartFile);
    }

    @Test
    public void shouldDownloadFile() throws Exception {
        File file = File.createTempFile("file", null);

        given(this.storageService.loadAsResource(PLUGIN_VERSION_FILE))
                .willReturn(new UrlResource(file.toURI()));

        this.mvc.perform(get(BASE_PATH + "/download"))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.CONTENT_DISPOSITION));
    }

    @Test
    public void should404WhenMissingFile() throws Exception {
        given(this.storageService.loadAsResource(PLUGIN_VERSION_FILE))
                .willThrow(StorageFileNotFoundException.class);

        this.mvc.perform(get(BASE_PATH + "/download"))
                .andExpect(status().isNotFound());
    }
}