/*
 * Sincere-Loyalty
 * Copyright (C) 2020 Ladysnake
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; If not, see <https://www.gnu.org/licenses>.
 */
package org.ladysnake.sincereloyalty.storage;

import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class TridentEntry {
    protected final ServerWorld world;
    protected final UUID tridentUuid;

    TridentEntry(ServerWorld world, UUID tridentUuid) {
        this.world = world;
        this.tridentUuid = tridentUuid;
    }

    TridentEntry(ServerWorld world, NbtCompound nbt) {
        this(world, nbt.getUuid("trident_uuid"));
    }

    @Nullable
    public static TridentEntry fromNbt(ServerWorld world, NbtCompound tag) {
        try {
            switch (tag.getString("type")) {
                case "world":
                    return new WorldTridentEntry(world, tag);
                case "inventory":
                    return new InventoryTridentEntry(world, tag);
                default: // pass
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    public UUID getTridentUuid() {
        return tridentUuid;
    }

    public abstract void preloadTrident();

    @Nullable
    public abstract TridentEntity findTrident();

    public NbtCompound toNbt(NbtCompound nbt) {
        nbt.putUuid("trident_uuid", this.tridentUuid);
        return nbt;
    }
}
