package com.github.heroslender.hero_api.controller;

import com.github.heroslender.hero_api.dto.NewLicenceDTO;
import com.github.heroslender.hero_api.dto.UpdateLicenceDTO;
import com.github.heroslender.hero_api.service.PluginLicenceService;
import com.github.heroslender.hero_api.service.PluginService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.heroslender.hero_api.security.MockUser.MOCK_USER;
import static com.github.heroslender.hero_api.security.MockUser.MOCK_USER_REQ;
import static com.github.heroslender.hero_api.service.PluginLicenceServiceTest.LICENCE;
import static com.github.heroslender.hero_api.service.PluginLicenceServiceTest.PLUGIN_TEST_DTO;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class PluginLicenceControllerTest {
    private static final String PLUGIN_NAME = "crates";
    private static final String BASE_PATH = "/plugins/" + PLUGIN_NAME + "/licence";

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private PluginService pluginService;
    @MockitoBean
    private PluginLicenceService service;

    @Test
    void shouldCreateLicence() throws Exception {
        NewLicenceDTO newLicenceDTO = new NewLicenceDTO(12345L);

        given(pluginService.getPlugin(PLUGIN_NAME))
                .willReturn(PLUGIN_TEST_DTO);
        given(service.createLicence(MOCK_USER, PLUGIN_NAME, newLicenceDTO))
                .willReturn(LICENCE);


        mvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"duration\": \"" + newLicenceDTO.duration() + "\" }")
                        .with(MOCK_USER_REQ))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$.createdAt", anything()));

        verify(service).createLicence(MOCK_USER, PLUGIN_NAME, newLicenceDTO);
    }

    @Test
    void shouldNotCreateLicenceForNonOwners() throws Exception {
        given(pluginService.getPlugin(PLUGIN_NAME))
                .willReturn(PLUGIN_TEST_DTO);

        mvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"duration\": \"" + 12345L + "\" }"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldValidateLicence() throws Exception {
        given(service.uuidFromString(LICENCE.id().toString())).willReturn(LICENCE.id());
        given(service.validateLicence(LICENCE.id())).willReturn(LICENCE);

        mvc.perform(get(BASE_PATH + "/" + LICENCE.id()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$.pluginId", is(LICENCE.pluginId())));
    }

    @Test
    void shouldUpdateLicence() throws Exception {
        given(pluginService.getPlugin(PLUGIN_NAME))
                .willReturn(PLUGIN_TEST_DTO);
        given(service.uuidFromString(LICENCE.id().toString())).willReturn(LICENCE.id());
        given(service.updateLicence(LICENCE.id(), new UpdateLicenceDTO(123L, null)))
                .willReturn(LICENCE);

        mvc.perform(put(BASE_PATH + "/" + LICENCE.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"duration\": \"" + 123L + "\" }")
                        .with(MOCK_USER_REQ))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$.createdAt", anything()));
    }

    @Test
    void shouldNotUpdateLicenceForNonOwners() throws Exception {
        given(pluginService.getPlugin(PLUGIN_NAME))
                .willReturn(PLUGIN_TEST_DTO);

        mvc.perform(put(BASE_PATH + "/" + LICENCE.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"duration\": \"" + 123L + "\" }"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldDeleteLicence() throws Exception {
        mvc.perform(delete(BASE_PATH + "/" + LICENCE.id()))
                .andExpect(status().isOk());

    }
}