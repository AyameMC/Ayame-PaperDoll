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

package org.ayamemc.ayamepaperdoll.mixin.hud;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.ayamemc.ayamepaperdoll.hud.ModRenderState;
import org.ayamemc.ayamepaperdoll.mixininterface.GuiGraphicsExtractorInterface;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GuiGraphicsExtractor.class)
public class GuiGraphicsExtractorMixin implements GuiGraphicsExtractorInterface {

    @Shadow
    @Final
    private GuiRenderState guiRenderState;

    @Override
    public void addPicturesInPictureState(EntityRenderState renderState, Vector3f translation, Quaternionf rotation, @Nullable Quaternionf overrideCameraAngle, int x0, int y0, float scale,boolean boat) {
        this.guiRenderState.addPicturesInPictureState(
                new ModRenderState(
                        renderState,
                        translation,
                        rotation,
                        overrideCameraAngle,
                        x0, y0,scale,boat,
                        null
                )
        );
    }
}
