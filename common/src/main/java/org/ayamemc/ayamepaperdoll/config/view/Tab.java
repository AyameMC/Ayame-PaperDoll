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

package org.ayamemc.ayamepaperdoll.config.view;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A container that does not render but provides inner widgets to
 * {@link net.minecraft.client.gui.components.tabs.TabNavigationBar} instances
 */
public class Tab implements net.minecraft.client.gui.components.tabs.Tab {
    private final Component title;
    private final List<AbstractWidget> children;

    public Tab(Component title) {
        this.title = title;
        this.children = new ArrayList<>();
    }

    public void addChild(AbstractWidget child) {
        this.children.add(child);
    }

    @Override
    public @NotNull Component getTabTitle() {
        return this.title;
    }

    @Override
    public @NotNull Component getTabExtraNarration() {
        return title;
    }

    /**
     * Used to load/unload children when switching tab
     */
    @Override
    public void visitChildren(Consumer<AbstractWidget> consumer) {
        children.forEach(consumer);
    }

    /**
     * Seems useless here
     */
    @Override
    public void doLayout(ScreenRectangle tabArea) {
    }
}
