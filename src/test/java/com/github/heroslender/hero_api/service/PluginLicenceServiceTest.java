package com.github.heroslender.hero_api.service;

import com.github.heroslender.hero_api.database.entity.PluginEntity;
import com.github.heroslender.hero_api.database.entity.PluginLicenceEntity;
import com.github.heroslender.hero_api.database.entity.PluginVersionEntity;
import com.github.heroslender.hero_api.database.repository.PluginLicenceRepository;
import com.github.heroslender.hero_api.dto.NewLicenceDTO;
import com.github.heroslender.hero_api.dto.UpdateLicenceDTO;
import com.github.heroslender.hero_api.exceptions.ForbiddenException;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.github.heroslender.hero_api.security.MockUser.MOCK_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PluginLicenceServiceTest {
    private static final Clock CLOCK = Clock.fixed(Instant.now(), ZoneOffset.UTC);
    private static final String PUBLIC_PLUGIN_ID = "Test";
    private static final PluginEntity PUBLIC_PLUGIN_ENTITY = new PluginEntity(PUBLIC_PLUGIN_ID, PluginVisibility.PUBLIC, "", "");
    private static final Plugin PUBLIC_PLUGIN;

    private static final String PLUGIN_ID = "PaidPlugin";
    private static final PluginEntity PLUGIN_TEST = new PluginEntity(PLUGIN_ID, PluginVisibility.REQUIRE_LICENCE, "", "");
    private static final UUID LICENCE_ID = UUID.randomUUID();
    private static final PluginLicenceEntity LICENCE_ENTITY = new PluginLicenceEntity(LICENCE_ID, CLOCK.millis() - 10000, 14L * 1000 * 60 * 60 * 24, PLUGIN_TEST, MOCK_USER);
    private static final PluginLicence LICENCE = PluginLicenceDtoMapper.toDto(LICENCE_ENTITY);
    private static final Plugin PLUGIN_TEST_DTO;

    static {
        PLUGIN_TEST.setOwner(MOCK_USER);
        PLUGIN_TEST.setVersions(List.of(
                new PluginVersionEntity(PLUGIN_TEST, "v1.0", CLOCK.millis(), "Sample Title", "", 0)
        ));

        PUBLIC_PLUGIN_ENTITY.setOwner(MOCK_USER);
        PUBLIC_PLUGIN_ENTITY.setVersions(List.of(
                new PluginVersionEntity(PLUGIN_TEST, "v1.0", CLOCK.millis(), "Sample Title", "", 0)
        ));

        PLUGIN_TEST_DTO = PluginDtoMapper.toDto(PLUGIN_TEST);
        PUBLIC_PLUGIN = PluginDtoMapper.toDto(PUBLIC_PLUGIN_ENTITY);
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
        assertThat(service.userHasAccessToPlugin(MOCK_USER, PUBLIC_PLUGIN)).isTrue();
        assertThat(service.userHasAccessToPlugin(null, PLUGIN_TEST_DTO)).isFalse();

        when(repository.findByOwnerId(LICENCE.ownerId())).thenReturn(List.of(LICENCE_ENTITY));
        assertThat(service.userHasAccessToPlugin(MOCK_USER, PLUGIN_TEST_DTO)).isTrue();

        when(repository.findByOwnerId(LICENCE.ownerId())).thenReturn(Collections.emptyList());
        assertThat(service.userHasAccessToPlugin(MOCK_USER, PLUGIN_TEST_DTO)).isFalse();
    }

    @Test
    void testCheckUserAccessToPlugin() {
        service.checkUserAccessToPlugin(MOCK_USER, PUBLIC_PLUGIN);
        assertThatThrownBy(() -> service.checkUserAccessToPlugin(null, PLUGIN_TEST_DTO))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void testHasLicence() {
        when(repository.findByOwnerId(LICENCE.ownerId())).thenReturn(List.of(LICENCE_ENTITY));
        assertThat(service.hasLicence(MOCK_USER.getId(), PLUGIN_ID)).isTrue();

        when(repository.findByOwnerId(LICENCE.ownerId())).thenReturn(Collections.emptyList());
        assertThat(service.hasLicence(MOCK_USER.getId(), PUBLIC_PLUGIN_ID)).isFalse();
    }

    @Test
    void testCreateLicence() {
        when(pluginService.getPlugin(PLUGIN_ID)).thenReturn(PLUGIN_TEST_DTO);
        PluginLicenceEntity newLicence = new PluginLicenceEntity(CLOCK.millis(), 14L * 1000 * 60 * 60 * 24, PLUGIN_TEST, MOCK_USER);
        when(repository.save(newLicence)).thenReturn(LICENCE_ENTITY);

        NewLicenceDTO newLicenceDTO = new NewLicenceDTO(14L * 1000 * 60 * 60 * 24);
        assertThat(service.createLicence(MOCK_USER, PLUGIN_ID, newLicenceDTO))
                .isEqualTo(LICENCE);

        when(pluginService.getPlugin(PLUGIN_ID)).thenReturn(new Plugin("test", 123123, PluginVisibility.REQUIRE_LICENCE, "", ""));
        assertThatThrownBy(() -> service.createLicence(MOCK_USER, PLUGIN_ID, newLicenceDTO))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void testUpdateLicence() {
        PluginLicenceEntity newLicence = new PluginLicenceEntity(LICENCE_ID, CLOCK.millis(), 14L * 1000 * 60 * 60 * 24, PLUGIN_TEST, MOCK_USER);
        PluginLicence licence = PluginLicenceDtoMapper.toDto(newLicence);
        when(repository.findById(LICENCE_ID)).thenReturn(Optional.of(newLicence));

        UpdateLicenceDTO updateLicenceDTO = new UpdateLicenceDTO(12345L, null);
        PluginLicence updatedLicence = service.updateLicence(LICENCE_ID, updateLicenceDTO);
        assertThat(updatedLicence).isNotEqualTo(licence);
        assertThat(updatedLicence.duration()).isEqualTo(12345);
        verify(repository).save(any());

        newLicence = new PluginLicenceEntity(LICENCE_ID, CLOCK.millis(), 14L * 1000 * 60 * 60 * 24, PLUGIN_TEST, MOCK_USER);
        when(repository.findById(LICENCE_ID)).thenReturn(Optional.of(newLicence));

        updateLicenceDTO = new UpdateLicenceDTO(null, null);
        updatedLicence = service.updateLicence(LICENCE_ID, updateLicenceDTO);
        assertThat(updatedLicence).isEqualTo(licence);
        verify(repository, times(1)).save(any());
    }
}