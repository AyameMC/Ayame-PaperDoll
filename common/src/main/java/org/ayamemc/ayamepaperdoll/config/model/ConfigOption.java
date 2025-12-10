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

import java.util.Objects;

@SuppressWarnings({"BooleanMethodIsAlwaysInverted", "UnusedReturnValue"})
public interface ConfigOption<T> {
    T getDefaultValue();

    /**
     * Invalid {@code value} must be logged as an {@link org.slf4j.event.Level#WARN}
     *
     * @return a valid new value
     */
    T validate(T oldValue, T newValue);

    /**
     * @return if the value is changed
     */
    boolean setValue(T newValue);

    default boolean isValueDefault() {
        return Objects.equals(this.getValue(), this.getDefaultValue());
    }

    T getValue();

    Identifier getCategory();

    Identifier getId();

    Component getName();

    Component getDescription();

    Class<T> getType();
}
