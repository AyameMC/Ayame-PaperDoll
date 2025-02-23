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

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import org.ayamemc.ayamepaperdoll.config.Configs;
import org.ayamemc.ayamepaperdoll.config.Configs.RotationMode;
import org.ayamemc.ayamepaperdoll.hud.DataBackup.DataBackupEntry;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.geom.Rectangle2D;
import java.util.List;

import static org.ayamemc.ayamepaperdoll.AyamePaperDoll.CONFIGS;

public class PaperDollRenderer {
    private static final List<DataBackupEntry<LivingEntity, ?>> LIVINGENTITY_BACKUP_ENTRIES = ImmutableList.of(
            new DataBackupEntry<>(LivingEntity::getPose, LivingEntity::setPose),
            // required for player on client side
            new DataBackupEntry<>(Entity::isCrouching, (e, flag) -> {
                if (e instanceof LocalPlayer player) player.crouching = flag;
            }),
            new DataBackupEntry<>(e -> e.swimAmount, (e, pitch) -> e.swimAmount = pitch),
            new DataBackupEntry<>(e -> e.swimAmountO, (e, pitch) -> e.swimAmountO = pitch),
            new DataBackupEntry<>(LivingEntity::isFallFlying, (e, flag) -> e.setSharedFlag(7, flag)),
            new DataBackupEntry<>(LivingEntity::getFallFlyingTicks, (e, ticks) -> e.fallFlyTicks = ticks),

            new DataBackupEntry<>(LivingEntity::getVehicle, (e, vehicle) -> e.vehicle = vehicle),

            new DataBackupEntry<>(e -> e.yBodyRotO, (e, yaw) -> e.yBodyRotO = yaw),
            new DataBackupEntry<>(e -> e.yBodyRot, (e, yaw) -> e.yBodyRot = yaw),
            new DataBackupEntry<>(e -> e.yHeadRotO, (e, yaw) -> e.yHeadRotO = yaw),
            new DataBackupEntry<>(e -> e.yHeadRot, (e, yaw) -> e.yHeadRot = yaw),
            new DataBackupEntry<>(e -> e.xRotO, (e, pitch) -> e.xRotO = pitch),
            new DataBackupEntry<>(LivingEntity::getXRot, LivingEntity::setXRot),

            new DataBackupEntry<>(e -> e.attackAnim, (e, prog) -> e.attackAnim = prog),
            new DataBackupEntry<>(e -> e.oAttackAnim, (e, prog) -> e.oAttackAnim = prog),
            new DataBackupEntry<>(e -> e.hurtTime, (e, time) -> e.hurtTime = time),
            new DataBackupEntry<>(LivingEntity::getRemainingFireTicks, LivingEntity::setRemainingFireTicks),
            new DataBackupEntry<>(e -> e.getSharedFlag(0), (e, flag) -> e.setSharedFlag(0, flag)) // on fire
    );
    private static final PaperDollRenderer instance = new PaperDollRenderer();
    private final Minecraft minecraft = Minecraft.getInstance();
    private Rectangle2D.Double currentRenderBounds;

    private PaperDollRenderer() {
    }

    public static PaperDollRenderer getInstance() {
        return instance;
    }

    @SuppressWarnings("resource")
    private static int getLight(Entity entity, float tickDelta) {
        if (CONFIGS.useWorldLight.getValue()) {
            Level world = entity.level();
            int blockLight = world.getBrightness(LightLayer.BLOCK, BlockPos.containing(entity.getEyePosition(tickDelta)));
            int skyLight = world.getBrightness(LightLayer.SKY, BlockPos.containing(entity.getEyePosition(tickDelta)));
            int min = CONFIGS.worldLightMin.getValue();
            blockLight = Mth.clamp(blockLight, min, 15);
            skyLight = Mth.clamp(skyLight, min, 15);
            return LightTexture.pack(blockLight, skyLight);
        }
        return LightTexture.pack(15, 15);
    }

    private static float getFallFlyingLeaning(LivingEntity entity, float partialTicks) {
        float ticks = partialTicks + entity.getFallFlyingTicks();
        return Mth.clamp(ticks * ticks / 100f, 0f, 1f);
    }

    public static boolean shouldLockRotationYaw() {
        final RotationMode rotationUnlock = CONFIGS.rotationMode.getValue();
        return (rotationUnlock == RotationMode.LOCK); //|| (rotationUnlock == RotationMode.SMOOTH_LOCK)*/;

    }

    // 这会导致织布机渲染问题，不要使用↓
    // follow convention in LayeredDrawer#renderInternal
    // guiGraphics.pose().translate(0, 0, 200);
    /**
     * Mimics the code in {@link InventoryScreen#renderEntityInInventory}
     */
    public void render(float partialTicks, GuiGraphics guiGraphics) {
        if (minecraft.level == null || minecraft.player == null || !CONFIGS.displayPaperDoll.getValue()) return;
        LivingEntity targetEntity = minecraft.level.players().stream().filter(p -> p.getName().getString().equals(CONFIGS.playerName.getValue())).findFirst().orElse(minecraft.player);
        if (CONFIGS.spectatorAutoSwitch.getValue() && minecraft.player.isSpectator()) {
            Entity cameraEntity = minecraft.getCameraEntity();
            if (cameraEntity instanceof LivingEntity livingEntity) {
                targetEntity = livingEntity;
            } else if (cameraEntity != null) {
                return;
            }
        }

        int scaledWidth = minecraft.getWindow().getGuiScaledWidth();
        int scaledHeight = minecraft.getWindow().getGuiScaledHeight();
        Configs.PoseOffsetMethod poseOffsetMethod = CONFIGS.poseOffsetMethod.getValue();

        var backup = new DataBackup<>(targetEntity, LIVINGENTITY_BACKUP_ENTRIES);
        backup.save();

        transformEntity(targetEntity, partialTicks, poseOffsetMethod == Configs.PoseOffsetMethod.FORCE_STANDING);

        DataBackup<LivingEntity> vehicleBackup = null;
        if (CONFIGS.renderVehicle.getValue() && poseOffsetMethod != Configs.PoseOffsetMethod.FORCE_STANDING && targetEntity.isPassenger()) {
            var vehicle = targetEntity.getVehicle();
            assert vehicle != null;

            // get the overall yaw before transforming
            var yawLerped = vehicle.getViewYRot(partialTicks);

            // FIXME: NEVERFIX - the rendered yaw of minecart is determined non-trivially in its MinecartEntityRenderer#render, so it cannot be fixed to 0 easily
            if (vehicle instanceof LivingEntity livingVehicle) {
                vehicleBackup = new DataBackup<>(livingVehicle, LIVINGENTITY_BACKUP_ENTRIES);
                vehicleBackup.save();
                transformEntity(livingVehicle, partialTicks, false);
            }

            performRendering(vehicle,
                    CONFIGS.offsetX.getValue() * scaledWidth,
                    CONFIGS.offsetY.getValue() * scaledHeight,
                    CONFIGS.size.getValue() * scaledHeight,
                    CONFIGS.mirrored.getValue(),
                    vehicle.getPosition(partialTicks).subtract(targetEntity.getPosition(partialTicks))
                            .yRot((float) Math.toRadians(yawLerped)).toVector3f(), // undo the rotation
                    CONFIGS.lightDegree.getValue(),
                    partialTicks, guiGraphics);
        }


        performRendering(targetEntity,
                CONFIGS.offsetX.getValue() * scaledWidth,
                CONFIGS.offsetY.getValue() * scaledHeight,
                CONFIGS.size.getValue() * scaledHeight,
                CONFIGS.mirrored.getValue(),
                new Vector3f(0, (float) getPoseOffsetY(targetEntity, partialTicks, poseOffsetMethod), 0),
                CONFIGS.lightDegree.getValue(),
                partialTicks, guiGraphics);

        if (vehicleBackup != null) vehicleBackup.restore();

        backup.restore();
    }

    private double getPoseOffsetY(LivingEntity targetEntity, float partialTicks, Configs.PoseOffsetMethod poseOffsetMethod) {
        if (poseOffsetMethod == Configs.PoseOffsetMethod.AUTO) {
            final float defaultPlayerEyeHeight = Player.DEFAULT_EYE_HEIGHT;
            final float defaultPlayerSwimmingBBHeight = Player.SWIMMING_BB_HEIGHT;
            final float eyeHeightRatio = 0.85f;
            if (targetEntity.isFallFlying()) {
                return (defaultPlayerEyeHeight - targetEntity.getEyeHeight()) * getFallFlyingLeaning(targetEntity, partialTicks);
            } else if (targetEntity.isAutoSpinAttack()) {
                return defaultPlayerEyeHeight - defaultPlayerSwimmingBBHeight * eyeHeightRatio * 0.8;
            } else if (targetEntity.isVisuallySwimming()) {
                return targetEntity.getSwimAmount(partialTicks) <= 0 ? 0 : defaultPlayerEyeHeight - targetEntity.getEyeHeight();
            } else if (!targetEntity.isVisuallySwimming() && targetEntity.getSwimAmount(partialTicks) > 0) { // for swimming/crawling pose, only smooth the falling edge
                return (defaultPlayerEyeHeight - defaultPlayerSwimmingBBHeight * eyeHeightRatio * 0.85) * targetEntity.getSwimAmount(partialTicks);
            } else {
                return Player.DEFAULT_EYE_HEIGHT - targetEntity.getEyeHeight();
            }
        } else if (poseOffsetMethod == Configs.PoseOffsetMethod.MANUAL) {
            if (targetEntity.isFallFlying()) {
                return CONFIGS.elytraOffsetY.getValue() * getFallFlyingLeaning(targetEntity, partialTicks);
            } else if ((targetEntity.isVisuallySwimming()) && targetEntity.getSwimAmount(partialTicks) > 0 || targetEntity.isAutoSpinAttack()) { // require nonzero leaning to filter out glitch
                return CONFIGS.swimCrawlOffsetY.getValue();
            } else if (!targetEntity.isVisuallySwimming() && targetEntity.getSwimAmount(partialTicks) > 0) { // for swimming/crawling pose, only smooth the falling edge
                return CONFIGS.swimCrawlOffsetY.getValue() * targetEntity.getSwimAmount(partialTicks);
            } else if (targetEntity.isCrouching()) {
                return CONFIGS.sneakOffsetY.getValue();
            }
        }
        return 0;
    }

    private void transformEntity(LivingEntity targetEntity, float partialTicks, boolean forceStanding) {
        // synchronize values to remove glitch
        if (!targetEntity.isSwimming() && !targetEntity.isFallFlying() && !targetEntity.isVisuallyCrawling()) {
            targetEntity.setPose(targetEntity.isCrouching() ? Pose.CROUCHING : Pose.STANDING);
        }

        if (forceStanding) {
            if (targetEntity instanceof LocalPlayer player) {
                player.crouching = false;
            }
            targetEntity.vehicle = null;

            targetEntity.swimAmount = 0;
            targetEntity.swimAmountO = 0;

            targetEntity.setSharedFlag(7, false);
            targetEntity.fallFlyTicks = 0;
        }

        // FIXME: NEVERFIX - glitch when the mouse moves too fast, caused by lerping a warped value, it is possibly wrapped in LivingEntity#tick or LivingEntity#turnHead
        final float headLerp = Mth.lerp(partialTicks, targetEntity.yHeadRotO, targetEntity.yHeadRot);
        final double headYaw = CONFIGS.headYaw.getValue(), headYawRange = CONFIGS.headYawRange.getValue();
        final double bodyYaw = CONFIGS.bodyYaw.getValue(), bodyYawRange = CONFIGS.bodyYawRange.getValue();
        final double pitch = CONFIGS.pitch.getValue(), pitchRange = CONFIGS.pitchRange.getValue();
        final float headClamp = (float) Mth.clamp(headLerp, headYaw - headYawRange, headYaw + headYawRange);
        final float bodyLerp = Mth.lerp(partialTicks, targetEntity.yBodyRotO, targetEntity.yBodyRot);
        final float diff = headLerp - bodyLerp;
        final float bodyClamp = (float) Mth.clamp(Mth.wrapDegrees(headClamp - diff), bodyYaw - bodyYawRange, bodyYaw + bodyYawRange);
        final float pitchClamp = (float) (Mth.clamp(Mth.lerp(partialTicks, targetEntity.xRotO, targetEntity.getXRot()), -pitchRange, pitchRange) + pitch);
        final RotationMode rotationMode = CONFIGS.rotationMode.getValue();

        // 头部锁定
        if (rotationMode == RotationMode.LOCK) {
            targetEntity.yHeadRot = targetEntity.yHeadRotO = 180 - headClamp;
        }
        // 身体锁定
        if (rotationMode == RotationMode.LOCK) {
            targetEntity.yBodyRot = targetEntity.yBodyRotO = 180 - bodyClamp;
        }

        // 头部俯视角度
        targetEntity.setXRot(targetEntity.xRotO = pitchClamp);


        if (!CONFIGS.swingHands.getValue()) {
            targetEntity.attackAnim = 0;
            targetEntity.oAttackAnim = 0;
        }

        if (!CONFIGS.hurtFlash.getValue()) {
            targetEntity.hurtTime = 0;
        }

        targetEntity.setRemainingFireTicks(0);

        targetEntity.setSharedFlag(0, false);
    }

    private void performRendering(Entity targetEntity, double posX, double posY, double size, boolean mirror,
                                  Vector3f offset, double lightDegree, float partialTicks, GuiGraphics guiGraphics) {
        EntityRenderDispatcher entityRenderDispatcher = minecraft.getEntityRenderDispatcher();

        Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushMatrix();
        modelViewStack.scale(mirror ? -1 : 1, 1, -1);
        // IDK what shit Mojang made but let's add 180 deg to restore the old behavior
        modelViewStack.rotateY((float) Math.toRadians(lightDegree + 180));

        PoseStack poseStack = new PaperDollPoseStack();
        poseStack.mulPose(Axis.YP.rotationDegrees(-(float) lightDegree - 180));
        poseStack.translate((mirror ? -1 : 1) * posX, posY, 0);
        poseStack.scale((float) size, (float) size, (float) size);
        Quaternionf zRot = new Quaternionf().rotateZ((float) Math.PI);

        final RotationMode rotationMode = CONFIGS.rotationMode.getValue();

        Quaternionf xyzRot = /*(rotationMode == RotationMode.SMOOTH_LOCK) ?
                new Quaternionf().rotateXYZ(
                        (float) Math.toRadians(CONFIGS.rotationX.getValue()),
                        (float) ((targetEntity.getYRot() + CONFIGS.rotationY.getValue() - 180) * ((float) Math.PI / 180F)),
                        (float) Math.toRadians(CONFIGS.rotationZ.getValue()))
                :*/
                new Quaternionf().rotateXYZ(
                        (float) Math.toRadians(CONFIGS.rotationX.getValue()),
                        (float) Math.toRadians(CONFIGS.rotationY.getValue()),
                        (float) Math.toRadians(CONFIGS.rotationZ.getValue()));

        zRot.mul(xyzRot);
        poseStack.mulPose(zRot);

        if (targetEntity instanceof Boat) {
            poseStack.mulPose(new Quaternionf().rotateY((float) Math.toRadians(180)));
        }

        Lighting.setupForEntityInInventory();
        xyzRot.conjugate();

        entityRenderDispatcher.overrideCameraOrientation(xyzRot);
        boolean renderHitbox = entityRenderDispatcher.shouldRenderHitBoxes();
        entityRenderDispatcher.setRenderHitBoxes(false);
        entityRenderDispatcher.setRenderShadow(false);
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();

        // TODO: 修复矿车锁定旋转时不被锁定的问题
        guiGraphics.drawSpecial(multiBufferSource ->
                entityRenderDispatcher.render(targetEntity, offset.x, offset.y, offset.z, partialTicks, poseStack, bufferSource, getLight(targetEntity, partialTicks)));

        // 事实证明1.21.3+只需一直禁用剔除，镜像也不会导致什么问题
        bufferSource.ayame_PaperDoll$setForceDisableCulling(true);
        bufferSource.endBatch();

        // do not need to restore this value in fact
        entityRenderDispatcher.setRenderShadow(true);
        entityRenderDispatcher.setRenderHitBoxes(renderHitbox);

        modelViewStack.popMatrix();
        Lighting.setupFor3DItems();
    }

    public Rectangle2D.Double getRenderBounds() {
        return this.currentRenderBounds;
    }

    public interface LockedPaperDoll {
    }

    public static class PaperDollPoseStack extends PoseStack implements LockedPaperDoll{
    }
}