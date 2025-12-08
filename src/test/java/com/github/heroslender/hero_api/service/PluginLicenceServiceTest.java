package com.github.heroslender.hero_api.service;

import com.github.heroslender.hero_api.database.entity.PluginEntity;
import com.github.heroslender.hero_api.database.entity.PluginLicenceEntity;
import com.github.heroslender.hero_api.database.entity.PluginVersionEntity;
import com.github.heroslender.hero_api.database.repository.PluginLicenceRepository;
import com.github.heroslender.hero_api.exceptions.ResourceNotFoundException;
import com.github.heroslender.hero_api.exceptions.UnauthorizedException;
import com.github.heroslender.hero_api.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.github.heroslender.hero_api.security.MockUser.MOCK_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PluginLicenceServiceTest {
    private static final String PLUGIN_ID = "Test";
    private static final PluginEntity PLUGIN_TEST = new PluginEntity(PLUGIN_ID, PluginVisibility.PUBLIC, "", "");
    private static final Plugin PLUGIN_TEST_DTO;
    private static final Clock CLOCK = Clock.fixed(Instant.now(), ZoneOffset.UTC);

    private static final UUID LICENCE_ID = UUID.randomUUID();
    private static final PluginLicenceEntity LICENCE_ENTITY = new PluginLicenceEntity(CLOCK.millis() - 10000, 14L * 1000 * 60 * 60 * 24, PLUGIN_TEST, MOCK_USER);
    private static final PluginLicence LICENCE = PluginLicenceDtoMapper.toDto(LICENCE_ENTITY);

    static {
        PLUGIN_TEST.setOwner(MOCK_USER);
        PLUGIN_TEST.setVersions(List.of(
                new PluginVersionEntity(PLUGIN_TEST, "v1.0", CLOCK.millis(), "Sample Title", "", 0)
        ));

        PLUGIN_TEST_DTO = PluginDtoMapper.toDto(PLUGIN_TEST);
    }

    private PluginLicenceService service;

    @Mock
    private PluginLicenceRepository repository;
    @Mock
    private PluginService pluginService;
    @Mock
    private UserService userService;

    @BeforeEach
    void setup() {
        service = new PluginLicenceService(repository, pluginService, userService, CLOCK);
    }

    @Test
    void testGetLicenceOpt() {
        when(repository.findById(LICENCE_ID)).thenReturn(Optional.of(LICENCE_ENTITY));

        Optional<PluginLicence> licence = service.getLicenceOpt(LICENCE_ID);
        assertThat(licence)
                .isPresent()
                .contains(LICENCE);
        verify(repository).findById(LICENCE_ID);

        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        licence = service.getLicenceOpt(id);
        assertThat(licence).isNotPresent();
        verify(repository).findById(id);
    }

    @Test
    void testGetLicence() {
        when(repository.findById(LICENCE_ID)).thenReturn(Optional.of(LICENCE_ENTITY));

        PluginLicence licence = service.getLicence(LICENCE_ID);
        assertThat(licence).isEqualTo(LICENCE);
        verify(repository).findById(LICENCE_ID);

        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getLicence(id)).isInstanceOf(ResourceNotFoundException.class);
        verify(repository).findById(id);
    }

    @Test
    void testValidateLicence() {
        when(repository.findById(LICENCE_ID)).thenReturn(Optional.of(LICENCE_ENTITY));

        PluginLicence licence = service.validateLicence(LICENCE_ID);
        assertThat(licence).isEqualTo(LICENCE);
        verify(repository).findById(LICENCE_ID);

        PluginLicenceEntity expriredLicence = new PluginLicenceEntity(
                LICENCE_ID,
                CLOCK.millis(),
                -1L,
                PLUGIN_TEST,
                MOCK_USER
        );
        when(repository.findById(LICENCE_ID)).thenReturn(Optional.of(expriredLicence));

        assertThatThrownBy(() -> service.validateLicence(LICENCE_ID)).isInstanceOf(UnauthorizedException.class);
        verify(repository, times(2)).findById(LICENCE_ID);
    }

    @Test
    void testUserHasAccessToPlugin() {

    }
}