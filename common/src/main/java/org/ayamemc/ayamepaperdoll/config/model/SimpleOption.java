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

package org.ayamemc.ayamepaperdoll.config.model;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.ayamemc.ayamepaperdoll.AyamePaperDoll;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Simplest implementation of {@link ConfigOption} with no callbacks, and only checks for non-null values.
 */
public class SimpleOption<T> implements ConfigOption<T> {
    @NotNull
    private final T defaultValue;
    private final Identifier category;
    private final Identifier id;
    private final Component name;
    private final Component description;
    private final Class<T> type;
    @NotNull
    private T value;

    public SimpleOption(Identifier category, Identifier id, @NotNull T defaultValue) {
        this.category = category;
        this.id = id;
        this.name = Component.translatable("config.%s.option.%s".formatted(id.getNamespace(), id.getPath()));
        this.description = Component.translatable("config.%s.option.%s.desc".formatted(id.getNamespace(), id.getPath()));
        this.value = this.defaultValue = defaultValue;
        //noinspection unchecked
        this.type = (Class<T>) defaultValue.getClass();
    }

    @Override
    public T validate(T oldValue, T newValue) {
        if (newValue == null) {
            AyamePaperDoll.LOGGER.warn("The new value for option {} is null, reset to the old value", this.getId().toString());
            return oldValue;
        }
        return newValue;
    }

    @Override
    public boolean setValue(T value) {
        var validated = this.validate(this.value, value);
        if (!Objects.equals(value, this.value)) {
            this.value = validated;
            return true;
        }
        return false;
    }

    @NotNull
    @Override
    public T getValue() {
        return value;
    }

    @NotNull
    @Override
    public T getDefaultValue() {
        return defaultValue;
    }

    @Override
    public Identifier getCategory() {
        return category;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public Component getName() {
        return name;
    }

    @Override
    public Component getDescription() {
        return description;
    }

    @Override
    public Class<T> getType() {
        return type;
    }
}
