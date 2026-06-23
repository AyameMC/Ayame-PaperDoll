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

package org.ayamemc.ayamepaperdoll.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.PictureInPictureRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import org.ayamemc.ayamepaperdoll.AyamePaperDoll;
import org.ayamemc.ayamepaperdoll.handler.EventHandler;
import org.ayamemc.ayamepaperdoll.hud.ModRenderer;

public class AyamePaperDollFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Run our Fabric setup.
        KeyMappingHelper.registerKeyMapping(AyamePaperDoll.SHOW_PAPERDOLL_KEY);
        KeyMappingHelper.registerKeyMapping(AyamePaperDoll.OPEN_CONFIG_GUI);

        AyamePaperDoll.init();

        ClientTickEvents.END_CLIENT_TICK.register((minecraft) ->
                EventHandler.keyPressed()
        );

        HudElementRegistry.attachElementAfter(
                VanillaHudElements.MISC_OVERLAYS,
                AyamePaperDoll.path("ayame_paperdoll_renderer_layer_after_misc_overlays"),
                EventHandler::renderPaperDoll
        );
        PictureInPictureRendererRegistry.register(context -> new ModRenderer(
                context.minecraft().getEntityRenderDispatcher()
        ));
    }
}