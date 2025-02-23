/*
 *     Highly configurable PaperDoll mod. Forked from Extra Player Renderer.
 *     Copyright (C) 2024-2025  LucunJi(Original author), HappyRespawnanchor
 *
 *     This file is part of Ayame PaperDoll.
 *
 *     Ayame PaperDoll is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Ayame PaperDoll is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Ayame PaperDoll.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.ayamemc.ayamepaperdoll;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import org.ayamemc.ayamepaperdoll.config.Configs;
import org.ayamemc.ayamepaperdoll.config.persistence.ConfigPersistence;
import org.ayamemc.ayamepaperdoll.config.persistence.GsonConfigPersistence;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public final class AyamePaperDoll {
    public static final String MOD_ID = "ayame_paperdoll";
    public static final String MOD_NAME = "Ayame PaperDoll";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final KeyMapping SHOW_PAPERDOLL_KEY = new KeyMapping(
            "key.%s.showPaperDoll".formatted(MOD_ID),
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F8,
            "key.%s.category".formatted(MOD_ID));
    public static final KeyMapping OPEN_CONFIG_GUI = new KeyMapping(
            "key.%s.openConfigGui".formatted(MOD_ID),
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            "key.%s.category".formatted(MOD_ID));

    public static final Configs CONFIGS = new Configs();
    public static final ConfigPersistence CONFIG_PERSISTENCE = new GsonConfigPersistence(Path.of("config/" + MOD_ID + "_v0.json"));

    public static ResourceLocation path(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void init() {
        // Write common init code here.
        CONFIG_PERSISTENCE.load(AyamePaperDoll.CONFIGS.getOptions());
    }
}
