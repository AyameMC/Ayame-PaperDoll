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

package org.ayamemc.ayamepaperdoll.hud;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public record ModRenderState(
        EntityRenderState renderState,
        Vector3f translation,
        Quaternionf rotation,
        @Nullable Quaternionf overrideCameraAngle,
        int x0,
        int y0,
        int x1,
        int y1,
        float scale,
        boolean boat,
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds
) implements PictureInPictureRenderState {
    public ModRenderState(
            EntityRenderState renderState,
            Vector3f translation,
            Quaternionf rotation,
            @Nullable Quaternionf overrideCameraAngle,
            int x,
            int y,
            float scale,
            boolean boat,
            @Nullable ScreenRectangle scissorArea
    ) {
        this(
                renderState,
                translation,
                rotation,
                overrideCameraAngle,
                x,
                y,
                x,
                y,
                scale,
                boat,
                scissorArea,
                PictureInPictureRenderState.getBounds(x, y, x, y, scissorArea)
        );
    }
}
