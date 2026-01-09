package com.github.heroslender.hero_api.service;

import com.github.heroslender.hero_api.database.entity.PluginVersionEntity;
import com.github.heroslender.hero_api.database.repository.PluginVersionRepository;
import com.github.heroslender.hero_api.dto.request.CreatePluginVersionRequest;
import com.github.heroslender.hero_api.exceptions.PluginVersionNotFoundException;
import com.github.heroslender.hero_api.model.PluginVersion;
import com.github.heroslender.hero_api.persistence.MockEntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.github.heroslender.hero_api.MockData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PluginVersionServiceTest {

    private PluginVersionService service;

    @Mock
    private PluginVersionRepository versionRepository;
    @Mock
    private PluginService pluginService;

    @BeforeEach
    void setup() {
        service = new PluginVersionService(versionRepository, pluginService, MockEntityManager.INSTANCE, CLOCK);
    }

    @Test
    void testAddVersion() {
        PluginVersionEntity versionEntity = new PluginVersionEntity(PUBLIC_PLUGIN_ENTITY, "v1.0", CLOCK.millis(), "Sample Title", "", 0);

        when(versionRepository.save(versionEntity)).thenReturn(versionEntity);

        PluginVersion version = service.addVersion(PUBLIC_PLUGIN_ID, "v1.0", new CreatePluginVersionRequest("Sample Title", ""));

        assertThat(version.tag()).isEqualTo("v1.0");
        assertThat(version.releaseTitle()).isEqualTo("Sample Title");
        verify(versionRepository).save(versionEntity);
    }

    @Test
    void testGetVersion() {
        when(versionRepository.findByPluginIdOrderByReleasedAtDesc(PUBLIC_PLUGIN_ID)).thenReturn(PUBLIC_PLUGIN_ENTITY.getVersions());

        PluginVersion version = service.getVersion(PUBLIC_PLUGIN_ID, "v1.0");

        assertThat(version.pluginId()).isEqualTo(PUBLIC_PLUGIN_ID);

        assertThatThrownBy(() -> service.getVersion(PUBLIC_PLUGIN_ID, "v1.1"))
                .isInstanceOf(PluginVersionNotFoundException.class);
    }

    @Test
    void testGetVersions() {
        when(versionRepository.findByPluginIdOrderByReleasedAtDesc(PUBLIC_PLUGIN_ID)).thenReturn(PUBLIC_PLUGIN_ENTITY.getVersions());

        List<PluginVersion> versions = service.getVersions(PUBLIC_PLUGIN_ID);

        assertThat(versions).isNotEmpty();
        verify(versionRepository).findByPluginIdOrderByReleasedAtDesc(PUBLIC_PLUGIN_ID);
    }
}