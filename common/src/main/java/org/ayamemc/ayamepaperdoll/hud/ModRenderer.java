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

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.renderer.CachedOrthoProjectionMatrixBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.CameraRenderState;
import org.ayamemc.ayamepaperdoll.AyamePaperDoll;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static org.ayamemc.ayamepaperdoll.AyamePaperDoll.CONFIGS;

public class ModRenderer extends PictureInPictureRenderer<ModRenderState> {
    private final CachedOrthoProjectionMatrixBuffer projectionMatrixBuffer = new CachedOrthoProjectionMatrixBuffer(
            "PIP - " + this.getClass().getSimpleName(), -1000.0F, 1000.0F, true
    );
    private final EntityRenderDispatcher entityRenderDispatcher;

    public ModRenderer(MultiBufferSource.BufferSource bufferSource, EntityRenderDispatcher entityRenderDispatcher) {
        super(bufferSource);
        this.entityRenderDispatcher = entityRenderDispatcher;
    }

    @Override
    public @NotNull Class<ModRenderState> getRenderStateClass() {
        return ModRenderState.class;
    }

    @Override
    protected void renderToTexture(ModRenderState renderState, PoseStack poseStack) {
        Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ENTITY_IN_UI);
        Vector3f vector3f = renderState.translation();
        poseStack.mulPose(renderState.rotation());
        Quaternionf quaternionf = renderState.overrideCameraAngle();
        FeatureRenderDispatcher featurerenderdispatcher = Minecraft.getInstance().gameRenderer.getFeatureRenderDispatcher();
        CameraRenderState camerarenderstate = new CameraRenderState();

        if (quaternionf != null) {
            camerarenderstate.orientation = quaternionf.conjugate(new Quaternionf()).rotateY((float) Math.PI);
        }

        this.entityRenderDispatcher.submit(renderState.renderState(), camerarenderstate, vector3f.x, vector3f.y, vector3f.z, poseStack, featurerenderdispatcher.getSubmitNodeStorage());
        featurerenderdispatcher.renderAllFeatures();
    }

    @Override
    protected @NotNull String getTextureLabel() {
        return "ayame-paperdoll";
    }
    @Override
    public void prepare(ModRenderState renderState, GuiRenderState guiRenderState, int guiScale) {
        AyamePaperDoll.identifier=CONFIGS.mirrored.getValue();
        RenderSystem.setProjectionMatrix(
                this.projectionMatrixBuffer.getBuffer(Minecraft.getInstance().getWindow().getGuiScaledWidth(),
                        Minecraft.getInstance().getWindow().getGuiScaledHeight()),
                ProjectionType.ORTHOGRAPHIC);

        PoseStack posestack = renderState.boat()?new PaperDollRenderer.PaperDollPoseStack():new PoseStack();
        posestack.translate(renderState.x0(), renderState.y0(), 0.0F);
        posestack.scale(CONFIGS.mirrored.getValue()?-1.0f:1.0f, 1.0f, -1.0f);
        float f =  renderState.scale();
        posestack.scale(f, f, f);
        this.renderToTexture(renderState, posestack);
        this.bufferSource.endBatch();
        AyamePaperDoll.identifier=false;
    }
}