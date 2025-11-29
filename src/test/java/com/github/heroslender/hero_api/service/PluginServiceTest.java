package com.github.heroslender.hero_api.service;

import com.github.heroslender.hero_api.database.entity.PluginEntity;
import com.github.heroslender.hero_api.database.entity.PluginVersionEntity;
import com.github.heroslender.hero_api.database.repository.PluginRepository;
import com.github.heroslender.hero_api.database.repository.PluginVersionRepository;
import com.github.heroslender.hero_api.dto.NewPluginVersionDto;
import com.github.heroslender.hero_api.exceptions.PluginNotFoundException;
import com.github.heroslender.hero_api.exceptions.PluginVersionNotFoundException;
import com.github.heroslender.hero_api.model.Plugin;
import com.github.heroslender.hero_api.model.PluginDtoMapper;
import com.github.heroslender.hero_api.model.PluginVersion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.github.heroslender.hero_api.security.MockUser.MOCK_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PluginServiceTest {
    private static final String PLUGIN_ID = "Test";
    private static final PluginEntity PLUGIN_TEST = new PluginEntity(PLUGIN_ID, "", "");
    private static final Plugin PLUGIN_TEST_DTO = PluginDtoMapper.toDto(PLUGIN_TEST);
    private static final Clock CLOCK = Clock.fixed(Instant.now(), ZoneOffset.UTC);

    static {
        PLUGIN_TEST.setOwner(MOCK_USER);
        PLUGIN_TEST.setVersions(List.of(
                new PluginVersionEntity(PLUGIN_TEST, "v1.0", CLOCK.millis(), "Sample Title", "", 0)
        ));
    }

    private PluginService service;

    @Mock
    private PluginRepository repository;
    @Mock
    private PluginVersionRepository versionRepository;

    @BeforeEach
    void setup() {
        service = new PluginService(repository, versionRepository, CLOCK);
    }

    @Test
    void testGetPlugins() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        List<Plugin> result = service.getPlugins();
        assertThat(result).isEmpty();
        verify(repository).findAll();

        PluginEntity plugin = new PluginEntity("Test2", "", "");
        plugin.setOwner(MOCK_USER);
        when(repository.findAll()).thenReturn(List.of(PLUGIN_TEST, plugin));

        result = service.getPlugins();
        assertThat(result).hasSize(2);
        assertThat(result.getFirst().name()).isEqualTo(PLUGIN_ID);
        verify(repository, times(2)).findAll();
    }

    @Test
    void testGetPlugin() {
        when(repository.findByName(PLUGIN_ID)).thenReturn(Optional.of(PLUGIN_TEST));

        Optional<Plugin> plugin = service.getPluginOpt(PLUGIN_ID);

        assertThat(plugin)
                .isPresent()
                .contains(PLUGIN_TEST_DTO);

        when(repository.findByName(PLUGIN_ID)).thenReturn(Optional.empty());

        plugin = service.getPluginOpt(PLUGIN_ID);
        assertThat(plugin).isNotPresent();
        verify(repository, times(2)).findByName(PLUGIN_ID);
    }

    @Test
    void testSave() {
        when(repository.save(PLUGIN_TEST)).thenReturn(PLUGIN_TEST);

        Plugin save = service.save(PLUGIN_TEST_DTO, MOCK_USER);

        assertThat(save.name()).isEqualTo(PLUGIN_TEST_DTO.name());
        verify(repository).save(PLUGIN_TEST);
    }

    @Test
    void testAddVersion() {
        PluginVersionEntity versionEntity = new PluginVersionEntity(PLUGIN_TEST, "v1.0", CLOCK.millis(), "Sample Title", "", 0);

        when(repository.findByName(PLUGIN_ID)).thenReturn(Optional.of(PLUGIN_TEST));
        when(versionRepository.save(versionEntity)).thenReturn(versionEntity);

        PluginVersion version = service.addVersion(PLUGIN_ID, "v1.0", new NewPluginVersionDto("Sample Title", ""));

        assertThat(version.tag()).isEqualTo("v1.0");
        assertThat(version.releaseTitle()).isEqualTo("Sample Title");
        verify(versionRepository).save(versionEntity);
    }

    @Test
    void addVersionShouldThrowPluginNotFound() {
        when(repository.findByName(PLUGIN_ID)).thenReturn(Optional.empty());

        NewPluginVersionDto newPluginVersionDto = new NewPluginVersionDto("", "");
        assertThatThrownBy(() -> service.addVersion(PLUGIN_ID, "", newPluginVersionDto))
                .isInstanceOf(PluginNotFoundException.class);
    }

    @Test
    void testGetVersion() {
        when(repository.findByName(PLUGIN_ID)).thenReturn(Optional.of(PLUGIN_TEST));

        PluginVersion version = service.getVersion(PLUGIN_ID, "v1.0");

        assertThat(version.pluginId()).isEqualTo(PLUGIN_ID);

        assertThatThrownBy(() -> service.getVersion(PLUGIN_ID, "v1.1"))
                .isInstanceOf(PluginVersionNotFoundException.class);
    }

    @Test
    void testGetVersions() {
        when(repository.findByName(PLUGIN_ID)).thenReturn(Optional.of(PLUGIN_TEST));

        List<PluginVersion> versions = service.getVersions(PLUGIN_ID);

        assertThat(versions).isNotEmpty();
        verify(repository).findByName(PLUGIN_ID);
    }
}