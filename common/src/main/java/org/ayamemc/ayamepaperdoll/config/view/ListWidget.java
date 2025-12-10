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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.resources.Identifier;
import org.ayamemc.ayamepaperdoll.AyamePaperDoll;

public class ListWidget extends ContainerObjectSelectionList<ListWidget.ListEntry> implements Retextured {

    private int rowWidth;

    public ListWidget(int width, int height, int top, int entryHeight) {
        super(Minecraft.getInstance(), width, height, top, entryHeight);
        this.rowWidth = super.getRowWidth();
    }

    @Override
    public int addEntry(ListEntry entry) {
        return super.addEntry(entry);
    }

    @Override
    public int getRowWidth() {
        return this.rowWidth;
    }

    public void setRowWidth(int rowWidth) {
        this.rowWidth = rowWidth;
        for(ListEntry entry : this.children()){
            entry.setX(getRowLeft());
            entry.setWidth(rowWidth);
        }
    }

    @Override
    public Identifier retexture(Identifier oldTexture) {
        return AyamePaperDoll.path(oldTexture.getPath());
    }

    public static abstract class ListEntry extends ContainerObjectSelectionList.Entry<ListEntry> {
    }
}
