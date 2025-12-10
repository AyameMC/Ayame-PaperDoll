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

import net.minecraft.resources.Identifier;
import org.ayamemc.ayamepaperdoll.AyamePaperDoll;
import org.jetbrains.annotations.NotNull;

/**
 * Checks for non-values within range {@code [minValue, maxValue]}
 */
public class SimpleNumericOption<T extends Number & Comparable<T>> extends SimpleOption<T> implements RangedConfigOption<T> {
    @NotNull
    private final T min;
    @NotNull
    private final T max;

    public SimpleNumericOption(Identifier category, Identifier id, @NotNull T defaultValue, @NotNull T min, @NotNull T max) {
        super(category, id, defaultValue);
        this.min = min;
        this.max = max;
        if (max.compareTo(min) < 0) {
            throw new IllegalArgumentException("The maximum value must be greater than the minimum value");
        }
        if (!withinRangeInclusive(defaultValue, min, max)) {
            throw new IllegalArgumentException("The default value must be in range [minValue] to [maxValue]");
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static <T extends Number & Comparable<T>> boolean withinRangeInclusive(T value, T minValue, T maxValue) {
        return minValue.compareTo(value) <= 0 && maxValue.compareTo(value) >= 0;
    }

    @Override
    public T validate(T oldValue, T newValue) {
        var validated = super.validate(oldValue, newValue);
        if (!withinRangeInclusive(validated, min, max)) {
            AyamePaperDoll.LOGGER.warn("The new value for option {} is outside the range [{}, {}], reset to the old value", this.getId().toString(), min, max);
            return oldValue;
        }
        return newValue;
    }

    @Override
    @NotNull
    public T getMax() {
        return max;
    }

    @Override
    @NotNull
    public T getMin() {
        return min;
    }
}
