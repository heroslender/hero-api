package com.github.heroslender.hero_api.service;

import com.github.heroslender.hero_api.DtoMapper;
import com.github.heroslender.hero_api.database.entity.PluginLicenceEntity;
import com.github.heroslender.hero_api.database.repository.PluginLicenceRepository;
import com.github.heroslender.hero_api.dto.request.CreateLicenceRequest;
import com.github.heroslender.hero_api.dto.request.UpdateLicenceRequest;
import com.github.heroslender.hero_api.exceptions.ForbiddenException;
import com.github.heroslender.hero_api.exceptions.ResourceNotFoundException;
import com.github.heroslender.hero_api.exceptions.UnauthorizedException;
import com.github.heroslender.hero_api.model.Plugin;
import com.github.heroslender.hero_api.model.PluginLicence;
import com.github.heroslender.hero_api.model.PluginVisibility;
import com.github.heroslender.hero_api.persistence.MockEntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.github.heroslender.hero_api.MockData.*;
import static com.github.heroslender.hero_api.security.MockUser.MOCK_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PluginLicenceServiceTest {
    private static final UUID LICENCE_ID = UUID.randomUUID();
    private static final PluginLicenceEntity LICENCE_ENTITY = new PluginLicenceEntity(LICENCE_ID, CLOCK.millis() - 10000, 14L * 1000 * 60 * 60 * 24, PAID_PLUGIN_ENTITY, MOCK_USER);
    public static final PluginLicence LICENCE = DtoMapper.toDto(LICENCE_ENTITY);

    private PluginLicenceService service;

    @Mock
    private PluginLicenceRepository repository;
    @Mock
    private PluginService pluginService;
    @Mock
    private UserService userService;

    @BeforeEach
    void setup() {
        service = new PluginLicenceService(repository, pluginService, userService, CLOCK, MockEntityManager.INSTANCE);
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
                PAID_PLUGIN_ENTITY,
                MOCK_USER
        );
        when(repository.findById(LICENCE_ID)).thenReturn(Optional.of(expriredLicence));

        assertThatThrownBy(() -> service.validateLicence(LICENCE_ID)).isInstanceOf(UnauthorizedException.class);
        verify(repository, times(2)).findById(LICENCE_ID);
    }

    @Test
    void testUserHasAccessToPlugin() {
        assertThat(service.userHasAccessToPlugin(MOCK_USER, PUBLIC_PLUGIN)).isTrue();
        assertThat(service.userHasAccessToPlugin(null, PAID_PLUGIN_DTO)).isFalse();

        when(repository.findByOwnerId(LICENCE.ownerId())).thenReturn(List.of(LICENCE_ENTITY));
        assertThat(service.userHasAccessToPlugin(MOCK_USER, PAID_PLUGIN_DTO)).isTrue();

        when(repository.findByOwnerId(LICENCE.ownerId())).thenReturn(Collections.emptyList());
        assertThat(service.userHasAccessToPlugin(MOCK_USER, PAID_PLUGIN_DTO)).isFalse();
    }

    @Test
    void testCheckUserAccessToPlugin() {
        service.checkUserAccessToPlugin(MOCK_USER, PUBLIC_PLUGIN);
        assertThatThrownBy(() -> service.checkUserAccessToPlugin(null, PAID_PLUGIN_DTO))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void testHasLicence() {
        when(repository.findByOwnerId(LICENCE.ownerId())).thenReturn(List.of(LICENCE_ENTITY));
        assertThat(service.hasLicence(MOCK_USER.getId(), PAID_PLUGIN_ID)).isTrue();

        when(repository.findByOwnerId(LICENCE.ownerId())).thenReturn(Collections.emptyList());
        assertThat(service.hasLicence(MOCK_USER.getId(), PUBLIC_PLUGIN_ID)).isFalse();
    }

    @Test
    void testCreateLicence() {
        when(pluginService.getPlugin(PAID_PLUGIN_ID)).thenReturn(PAID_PLUGIN_DTO);
        PluginLicenceEntity newLicence = new PluginLicenceEntity(CLOCK.millis(), 14L * 1000 * 60 * 60 * 24, PAID_PLUGIN_ENTITY, MOCK_USER);
        when(repository.save(newLicence)).thenReturn(LICENCE_ENTITY);

        CreateLicenceRequest request = new CreateLicenceRequest(14L * 1000 * 60 * 60 * 24);
        assertThat(service.createLicence(MOCK_USER, PAID_PLUGIN_ID, request))
                .isEqualTo(LICENCE);

        when(pluginService.getPlugin(PAID_PLUGIN_ID)).thenReturn(new Plugin("test", "test", 123123, PluginVisibility.REQUIRE_LICENCE, 0F, 0F, "", ""));
        assertThatThrownBy(() -> service.createLicence(MOCK_USER, PAID_PLUGIN_ID, request))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void testUpdateLicence() {
        PluginLicenceEntity newLicence = new PluginLicenceEntity(LICENCE_ID, CLOCK.millis(), 14L * 1000 * 60 * 60 * 24, PAID_PLUGIN_ENTITY, MOCK_USER);
        PluginLicence licence = DtoMapper.toDto(newLicence);
        when(repository.findById(LICENCE_ID)).thenReturn(Optional.of(newLicence));

        UpdateLicenceRequest request = new UpdateLicenceRequest(12345L, null);
        PluginLicence updatedLicence = service.updateLicence(LICENCE_ID, request);
        assertThat(updatedLicence).isNotEqualTo(licence);
        assertThat(updatedLicence.duration()).isEqualTo(12345);
        verify(repository).save(any());

        newLicence = new PluginLicenceEntity(LICENCE_ID, CLOCK.millis(), 14L * 1000 * 60 * 60 * 24, PAID_PLUGIN_ENTITY, MOCK_USER);
        when(repository.findById(LICENCE_ID)).thenReturn(Optional.of(newLicence));

        request = new UpdateLicenceRequest(null, null);
        updatedLicence = service.updateLicence(LICENCE_ID, request);
        assertThat(updatedLicence).isEqualTo(licence);
        verify(repository, times(1)).save(any());
    }
}