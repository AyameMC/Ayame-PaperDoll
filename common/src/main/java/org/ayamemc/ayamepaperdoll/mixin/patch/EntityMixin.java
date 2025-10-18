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

package org.ayamemc.ayamepaperdoll.mixin.patch;

import net.minecraft.world.entity.Entity;
import org.ayamemc.ayamepaperdoll.mixininterface.EntityMixinInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityMixinInterface {
    @Unique
    private final Entity ayame_PaperDoll$entity = (Entity) (Object) this;
    @Unique
    private boolean ayame_PaperDoll$isSSitting = false;
    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;ZZ)Z", at = @At("RETURN"))
    private void onStartRiding(Entity entity, boolean bl, boolean bl2, CallbackInfoReturnable<Boolean> cir) {
        ayame_PaperDoll$entity.ayame_paperdoll$setSitting(cir.getReturnValue());
    }

    @Inject(method = "removeVehicle", at = @At("HEAD"))
    private void onRemoveVehicle(CallbackInfo ci) {
        ayame_PaperDoll$entity.ayame_paperdoll$setSitting(false);
    }

    @Override
    public void ayame_paperdoll$setSitting(boolean sitting) {
        ayame_PaperDoll$isSSitting = sitting;
    }

    @Override
    public boolean ayame_paperdoll$isSitting() {
        return ayame_PaperDoll$isSSitting;
    }
}