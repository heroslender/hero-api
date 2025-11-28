package com.github.heroslender.hero_api.controller;

import com.github.heroslender.hero_api.dto.NewPluginVersionDto;
import com.github.heroslender.hero_api.exceptions.PluginVersionNotFoundException;
import com.github.heroslender.hero_api.exceptions.StorageFileNotFoundException;
import com.github.heroslender.hero_api.model.Plugin;
import com.github.heroslender.hero_api.model.PluginVersion;
import com.github.heroslender.hero_api.service.PluginService;
import com.github.heroslender.hero_api.service.PluginVersionStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.util.Collections;
import java.util.Optional;

import static com.github.heroslender.hero_api.security.MockUser.MOCK_USER;
import static com.github.heroslender.hero_api.security.MockUser.MOCK_USER_REQ;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    @MockitoBean
    private PluginService service;

    @Test
    void shouldGiveVersionList() throws Exception {
        given(this.service.getVersions(PLUGIN_NAME))
                .willReturn(Collections.emptyList());

        this.mvc.perform(get("/plugins/" + PLUGIN_NAME + "/versions"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGiveVersion() throws Exception {
        given(this.service.getVersion(PLUGIN_NAME, PLUGIN_VERSION))
                .willReturn(new PluginVersion(
                        PLUGIN_NAME,
                        PLUGIN_VERSION,
                        System.currentTimeMillis(),
                        "",
                        "",
                        0
                ));

        this.mvc.perform(get(BASE_PATH))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAddPluginVersion() throws Exception {
        PluginVersion version = new PluginVersion(
                PLUGIN_NAME,
                PLUGIN_VERSION,
                System.currentTimeMillis(),
                "",
                "",
                0
        );

        given(this.service.getPlugin(PLUGIN_NAME))
                .willReturn(new Plugin(PLUGIN_NAME, MOCK_USER.getId(), PLUGIN_NAME, ""));
        given(this.service.getVersion(PLUGIN_NAME, PLUGIN_VERSION))
                .willThrow(new PluginVersionNotFoundException(PLUGIN_VERSION));
        given(this.service.addVersion(PLUGIN_NAME, PLUGIN_VERSION, new NewPluginVersionDto(PLUGIN_VERSION, "")))
                .willReturn(version);

        this.mvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"releaseTitle\": \"" + PLUGIN_VERSION + "\", \"releaseNotes\": \"\" }")
                        .with(MOCK_USER_REQ))
                .andExpect(status().isCreated());

        then(this.service).should().addVersion(PLUGIN_NAME, PLUGIN_VERSION, new NewPluginVersionDto(PLUGIN_VERSION, ""));
    }

    @Test
    void shouldDenyNewVersionForNonOwners() throws Exception {
        given(this.service.getPlugin(PLUGIN_NAME))
                .willReturn(new Plugin(PLUGIN_NAME, 99, PLUGIN_NAME, ""));

        this.mvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"releaseTitle\": \"" + PLUGIN_VERSION + "\", \"releaseNotes\": \"\" }")
                        .with(MOCK_USER_REQ))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldSaveUploadedFile() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.txt",
                "text/plain", "Spring Framework".getBytes());

        given(this.service.getPlugin(PLUGIN_NAME))
                .willReturn(new Plugin(PLUGIN_NAME, MOCK_USER.getId(), PLUGIN_NAME, ""));

        this.mvc.perform(multipart(BASE_PATH + "/upload").file(multipartFile).with(MOCK_USER_REQ))
                .andExpect(status().isCreated());

        then(this.storageService).should().store(PLUGIN_VERSION_FILE, multipartFile);
    }

    @Test
    void shouldDenyUploadForNonOwners() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.txt",
                "text/plain", "Spring Framework".getBytes());

        given(this.service.getPlugin(PLUGIN_NAME))
                .willReturn(new Plugin(PLUGIN_NAME, 99, PLUGIN_NAME, ""));

        this.mvc.perform(multipart(BASE_PATH + "/upload").file(multipartFile).with(MOCK_USER_REQ))
                .andExpect(status().isForbidden());

        then(this.storageService).shouldHaveNoInteractions();
    }

    @Test
    void shouldDownloadFile() throws Exception {
        File file = File.createTempFile("file", null);

        given(this.storageService.loadAsResource(PLUGIN_VERSION_FILE))
                .willReturn(new UrlResource(file.toURI()));

        this.mvc.perform(get(BASE_PATH + "/download"))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.CONTENT_DISPOSITION));
    }

    @Test
    void should404WhenMissingFile() throws Exception {
        given(this.storageService.loadAsResource(PLUGIN_VERSION_FILE))
                .willThrow(StorageFileNotFoundException.class);

        this.mvc.perform(get(BASE_PATH + "/download"))
                .andExpect(status().isNotFound());
    }
}