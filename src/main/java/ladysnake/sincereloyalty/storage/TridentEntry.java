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
package ladysnake.sincereloyalty.storage;

import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class TridentEntry {
    @Nullable
    public static TridentEntry fromNbt(ServerWorld world, CompoundTag tag) {
        try {
            switch (tag.getString("type")) {
                case "world": return new WorldTridentEntry(world, tag);
                case "inventory": return new InventoryTridentEntry(world, tag);
                default: // pass
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected final ServerWorld world;
    protected final UUID tridentUuid;

    TridentEntry(ServerWorld world, UUID tridentUuid) {
        this.world = world;
        this.tridentUuid = tridentUuid;
    }

    TridentEntry(ServerWorld world, CompoundTag nbt) {
        this(world, nbt.getUuid("trident_uuid"));
    }

    public UUID getTridentUuid() {
        return tridentUuid;
    }

    @Nullable
    public abstract TridentEntity findTrident();

    public CompoundTag toNbt(CompoundTag nbt) {
        nbt.putUuid("trident_uuid", this.tridentUuid);
        return nbt;
    }
}
