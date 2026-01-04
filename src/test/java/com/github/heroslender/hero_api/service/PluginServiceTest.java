package com.github.heroslender.hero_api.service;

import com.github.heroslender.hero_api.database.entity.PluginEntity;
import com.github.heroslender.hero_api.database.entity.PluginVersionEntity;
import com.github.heroslender.hero_api.database.repository.PluginRepository;
import com.github.heroslender.hero_api.database.repository.PluginVersionRepository;
import com.github.heroslender.hero_api.dto.request.CreatePluginVersionRequest;
import com.github.heroslender.hero_api.exceptions.PluginNotFoundException;
import com.github.heroslender.hero_api.exceptions.PluginVersionNotFoundException;
import com.github.heroslender.hero_api.model.Plugin;
import com.github.heroslender.hero_api.model.PluginVersion;
import com.github.heroslender.hero_api.persistence.MockEntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.github.heroslender.hero_api.MockData.*;
import static com.github.heroslender.hero_api.security.MockUser.MOCK_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PluginServiceTest {

    private PluginService service;

    @Mock
    private PluginRepository repository;
    @Mock
    private PluginVersionRepository versionRepository;

    @BeforeEach
    void setup() {
        service = new PluginService(repository, versionRepository, CLOCK, MockEntityManager.INSTANCE);
    }

    @Test
    void testGetPlugins() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        List<Plugin> result = service.getPlugins();
        assertThat(result).isEmpty();
        verify(repository).findAll();

        PluginEntity plugin = new PluginEntity("Test2");
        plugin.setOwner(MOCK_USER);
        when(repository.findAll()).thenReturn(List.of(PUBLIC_PLUGIN_ENTITY, plugin));

        result = service.getPlugins();
        assertThat(result).hasSize(2);
        assertThat(result.getFirst().id()).isEqualTo(PUBLIC_PLUGIN_ID);
        verify(repository, times(2)).findAll();
    }

    @Test
    void testGetPlugin() {
        when(repository.findById(PUBLIC_PLUGIN_ID)).thenReturn(Optional.of(PUBLIC_PLUGIN_ENTITY));

        Optional<Plugin> plugin = service.getPluginOpt(PUBLIC_PLUGIN_ID);

        assertThat(plugin)
                .isPresent()
                .contains(PUBLIC_PLUGIN);

        when(repository.findById(PUBLIC_PLUGIN_ID)).thenReturn(Optional.empty());

        plugin = service.getPluginOpt(PUBLIC_PLUGIN_ID);
        assertThat(plugin).isNotPresent();
        verify(repository, times(2)).findById(PUBLIC_PLUGIN_ID);
    }

    @Test
    void testSave() {
        when(repository.save(PUBLIC_PLUGIN_ENTITY)).thenReturn(PUBLIC_PLUGIN_ENTITY);

        Plugin save = service.save(PUBLIC_PLUGIN, MOCK_USER);

        assertThat(save.id()).isEqualTo(PUBLIC_PLUGIN.id());
        verify(repository).save(PUBLIC_PLUGIN_ENTITY);
    }

    @Test
    void testAddVersion() {
        PluginVersionEntity versionEntity = new PluginVersionEntity(PUBLIC_PLUGIN_ENTITY, "v1.0", CLOCK.millis(), "Sample Title", "", 0);

        when(repository.findById(PUBLIC_PLUGIN_ID)).thenReturn(Optional.of(PUBLIC_PLUGIN_ENTITY));
        when(versionRepository.save(versionEntity)).thenReturn(versionEntity);

        PluginVersion version = service.addVersion(PUBLIC_PLUGIN_ID, "v1.0", new CreatePluginVersionRequest("Sample Title", ""));

        assertThat(version.tag()).isEqualTo("v1.0");
        assertThat(version.releaseTitle()).isEqualTo("Sample Title");
        verify(versionRepository).save(versionEntity);
    }

    @Test
    void addVersionShouldThrowPluginNotFound() {
        when(repository.findById(PUBLIC_PLUGIN_ID)).thenReturn(Optional.empty());

        CreatePluginVersionRequest request = new CreatePluginVersionRequest("", "");
        assertThatThrownBy(() -> service.addVersion(PUBLIC_PLUGIN_ID, "", request))
                .isInstanceOf(PluginNotFoundException.class);
    }

    @Test
    void testGetVersion() {
        when(repository.findById(PUBLIC_PLUGIN_ID)).thenReturn(Optional.of(PUBLIC_PLUGIN_ENTITY));

        PluginVersion version = service.getVersion(PUBLIC_PLUGIN_ID, "v1.0");

        assertThat(version.pluginId()).isEqualTo(PUBLIC_PLUGIN_ID);

        assertThatThrownBy(() -> service.getVersion(PUBLIC_PLUGIN_ID, "v1.1"))
                .isInstanceOf(PluginVersionNotFoundException.class);
    }

    @Test
    void testGetVersions() {
        when(repository.findById(PUBLIC_PLUGIN_ID)).thenReturn(Optional.of(PUBLIC_PLUGIN_ENTITY));

        List<PluginVersion> versions = service.getVersions(PUBLIC_PLUGIN_ID);

        assertThat(versions).isNotEmpty();
        verify(repository).findById(PUBLIC_PLUGIN_ID);
    }
}