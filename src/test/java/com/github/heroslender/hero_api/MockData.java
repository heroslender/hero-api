package com.github.heroslender.hero_api;

import com.github.heroslender.hero_api.database.entity.PluginEntity;
import com.github.heroslender.hero_api.database.entity.PluginVersionEntity;
import com.github.heroslender.hero_api.model.Plugin;
import com.github.heroslender.hero_api.model.PluginVisibility;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

import static com.github.heroslender.hero_api.security.MockUser.MOCK_USER;

public class MockData {
    public static final String PUBLIC_PLUGIN_ID = "Test";
    public static final PluginEntity PUBLIC_PLUGIN_ENTITY = new PluginEntity(
            PUBLIC_PLUGIN_ID,
            "Test",
            MOCK_USER,
            PluginVisibility.PUBLIC,
            0.0F,
            0.0F,
            "",
            "",
            Collections.emptyList()
    );
    public static final Plugin PUBLIC_PLUGIN;

    public static final String PAID_PLUGIN_ID = "PaidPlugin";
    public static final PluginEntity PAID_PLUGIN_ENTITY = new PluginEntity(
            PAID_PLUGIN_ID,
            "Paid Test",
            MOCK_USER,
            PluginVisibility.REQUIRE_LICENCE,
            0.0F,
            0.0F,
            "",
            "",
            Collections.emptyList()
    );
    public static final Plugin PAID_PLUGIN_DTO;

    public static final Clock CLOCK = Clock.fixed(Instant.now(), ZoneOffset.UTC);


    static {
        PUBLIC_PLUGIN_ENTITY.setOwner(MOCK_USER);
        PUBLIC_PLUGIN_ENTITY.setVersions(List.of(
                new PluginVersionEntity(PUBLIC_PLUGIN_ENTITY, "v1.0", CLOCK.millis(), "Sample Title", "", 0)
        ));

        PUBLIC_PLUGIN = DtoMapper.toDto(PUBLIC_PLUGIN_ENTITY);


        PAID_PLUGIN_ENTITY.setOwner(MOCK_USER);
        PAID_PLUGIN_ENTITY.setVisibility(PluginVisibility.REQUIRE_LICENCE);
        PAID_PLUGIN_ENTITY.setVersions(List.of(
                new PluginVersionEntity(PAID_PLUGIN_ENTITY, "v1.0", CLOCK.millis(), "Sample Title", "", 0)
        ));

        PAID_PLUGIN_DTO = DtoMapper.toDto(PAID_PLUGIN_ENTITY);
    }
}
