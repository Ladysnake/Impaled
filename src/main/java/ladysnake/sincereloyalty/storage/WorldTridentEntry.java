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

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public final class WorldTridentEntry extends TridentEntry {
    private UUID tridentEntityUuid;
    private BlockPos lastPos;

    public WorldTridentEntry(ServerWorld world, UUID tridentUuid, UUID tridentEntityUuid, BlockPos lastPos) {
        super(world, tridentUuid);
        this.tridentEntityUuid = tridentEntityUuid;
        this.lastPos = lastPos;
    }

    public WorldTridentEntry(ServerWorld world, CompoundTag tag) {
        super(world, tag);
        this.tridentEntityUuid = tag.getUuid("trident_entity_uuid");
        this.lastPos = NbtHelper.toBlockPos(tag.getCompound("last_pos"));
    }

    @Override
    public CompoundTag toNbt(CompoundTag nbt) {
        super.toNbt(nbt);
        nbt.putUuid("trident_entity_uuid", this.tridentEntityUuid);
        nbt.put("last_pos", NbtHelper.fromBlockPos(this.lastPos));
        return nbt;
    }

    public void updateLastPos(UUID tridentEntityUuid, BlockPos pos) {
        this.tridentEntityUuid = tridentEntityUuid;
        this.lastPos = pos;
    }

    @Override
    public TridentEntity findTrident() {
        // preload the chunk
        this.world.getChunk(this.lastPos);
        Entity trident = this.world.getEntity(this.tridentEntityUuid);
        if (trident instanceof TridentEntity) {
            return (TridentEntity) trident;
        }
        return null;
    }

}
