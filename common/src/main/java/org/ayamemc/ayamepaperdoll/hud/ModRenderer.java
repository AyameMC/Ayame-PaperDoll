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

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.textures.FilterMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.state.gui.BlitRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.OptionalDouble;

public class ModRenderer extends PictureInPictureRenderer<ModRenderState> {
    private final EntityRenderDispatcher entityRenderDispatcher;
    private final SubmitNodeStorage submitNodeStorage = new SubmitNodeStorage();
    private int width, height, guiScale;

    public ModRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        this.entityRenderDispatcher = entityRenderDispatcher;
    }

    @Override
    public @NotNull Class<ModRenderState> getRenderStateClass() {
        return ModRenderState.class;
    }

    @Override
    protected void renderToTexture(ModRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        Minecraft.getInstance().gameRenderer.lighting().setupFor(Lighting.Entry.ENTITY_IN_UI);
        Quaternionf quaternionf = renderState.overrideCameraAngle();
        CameraRenderState camerarenderstate = new CameraRenderState();

        if (quaternionf != null) {
            camerarenderstate.orientation = quaternionf.conjugate(new Quaternionf()).rotateY((float) Math.PI);
        }
        if(renderState.vehicleRenderState() != null){
            poseStack.pushPose();
            Vector3f vector3f = renderState.translation2();
            assert vector3f != null;
            poseStack.rotate(renderState.rotation2());
            this.entityRenderDispatcher.submit(renderState.vehicleRenderState(), camerarenderstate, vector3f.x, vector3f.y, vector3f.z, poseStack, submitNodeCollector);
            poseStack.popPose();
        }
        Vector3f vector3f = renderState.translation();
        poseStack.rotate(renderState.rotation());
        this.entityRenderDispatcher.submit(renderState.renderState(), camerarenderstate, vector3f.x, vector3f.y, vector3f.z, poseStack, submitNodeCollector);
    }

    @Override
    protected @NotNull String getTextureLabel() {
        return "ayame-paperdoll";
    }

    public void prepare(ModRenderState renderState, GuiRenderState guiRenderState, FeatureRenderDispatcher featureRenderDispatcher, int guiScale) {
        int width = renderState.x1() - renderState.x0();
		int height = renderState.y1() - renderState.y0();
        boolean needsAResize = this.width != width || this.height != height || this.guiScale != guiScale;
        if (needsAResize) {
            this.width = width;
            this.height = height;
            this.guiScale = guiScale;
        }
        this.prepareTexturesAndProjection(needsAResize, width * guiScale, height * guiScale);
        Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushMatrix();
        modelViewStack.rotateY(renderState.lightDegree());
        PoseStack posestack = new PoseStack();
        posestack.rotate(Axis.YP.rotation(-renderState.lightDegree()));
        posestack.scale(guiScale, guiScale, guiScale);
        float offsetX = renderState.mirror() ? width - renderState.offsetX() : renderState.offsetX();
        posestack.translate(offsetX, renderState.offsetY(), 0.0F);
        float f = renderState.scale();
        posestack.scale(f, f, -f);

        // ImprovedTransparency should be temporarily set before renderToTexture() call
        // since we want RenderTypes.waterMask() returning RenderTypes.WATER_MASK
        GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
        boolean useUiLightmap = gameRenderer.useUiLightmap;
        boolean improvedTransparency = gameRenderer.gameRenderState().optionsRenderState.improvedTransparency;
        gameRenderer.useUiLightmap = false;
        gameRenderer.gameRenderState().optionsRenderState.improvedTransparency = false;

        this.renderToTexture(renderState, posestack, this.submitNodeStorage);

        try (
            FeatureRenderDispatcher.PreparedFrame frame = featureRenderDispatcher.prepareFrame(this.submitNodeStorage);
            RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Picture in picture", this.textureView, Optional.empty(), this.depthTextureView, OptionalDouble.empty());
        ) {
            RenderSystem.bindDefaultUniforms(renderPass);
            FeatureRenderDispatcher.renderAllFeatures(renderPass, frame);
        }

        gameRenderer.useUiLightmap = useUiLightmap;
        gameRenderer.gameRenderState().optionsRenderState.improvedTransparency = improvedTransparency;

        modelViewStack.popMatrix();
        blitTexture(renderState, guiRenderState);
    }

    @Override
    protected void blitTexture(final ModRenderState renderState, final GuiRenderState guiRenderState) {
        float u0,u1;
        if (renderState.mirror()){
            u0 = 1.0f;
            u1 = 0.0f;
        } else {
            u0 = 0.0f;
            u1 = 1.0f;
        }
        guiRenderState.addBlitToCurrentLayer(
			new BlitRenderState(
				RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA,
				TextureSetup.singleTexture(this.textureView, RenderSystem.getSamplerCache().getRepeat(FilterMode.NEAREST)),
				renderState.pose(),
				renderState.x0(),
				renderState.y0(),
				renderState.x1(),
				renderState.y1(),
				u0,
				u1,
				1.0F,
				0.0F,
				-1,
				renderState.scissorArea(),
				null
			)
		);
    }
}