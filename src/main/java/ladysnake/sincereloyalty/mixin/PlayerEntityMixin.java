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
package ladysnake.sincereloyalty.mixin;

import ladysnake.sincereloyalty.SincereLoyalty;
import ladysnake.sincereloyalty.TridentRecaller;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements TridentRecaller {
    @NotNull
    @Unique
    private RecallStatus recallingTrident = RecallStatus.NONE;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public RecallStatus getCurrentRecallStatus() {
        return this.recallingTrident;
    }

    @Override
    public void updateRecallStatus(RecallStatus recallingTrident) {
        if (this.recallingTrident != recallingTrident) {
            this.recallingTrident = recallingTrident;
            if (!this.world.isClient) {
                PacketByteBuf res = PacketByteBufs.create();
                res.writeInt(this.getId());
                res.writeEnumConstant(recallingTrident);
                Packet<?> packet = ServerPlayNetworking.createS2CPacket(SincereLoyalty.RECALLING_MESSAGE_ID, res);
                ((ServerPlayerEntity) (Object) this).networkHandler.sendPacket(packet);
                for (ServerPlayerEntity player : PlayerLookup.tracking(this)) {
                    player.networkHandler.sendPacket(packet);
                }
            }
        }
    }
}
