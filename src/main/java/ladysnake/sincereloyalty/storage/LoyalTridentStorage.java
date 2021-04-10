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

import com.google.common.base.Preconditions;
import ladysnake.sincereloyalty.LoyalTrident;
import ladysnake.sincereloyalty.SincereLoyalty;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.PersistentState;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public final class LoyalTridentStorage extends PersistentState {

    public static LoyalTridentStorage get(ServerWorld world) {
        final String id = SincereLoyalty.MOD_ID + "_trident_storage";
        return world.getPersistentStateManager().getOrCreate(tag -> fromNbt(world, tag), () -> new LoyalTridentStorage(world), id);
    }

    /** Player UUID -> Trident UUID -> Trident Position */
    private final Map<UUID, OwnedTridents> tridents = new HashMap<>();
    final ServerWorld world;

    public LoyalTridentStorage(ServerWorld world) {
        super();
        this.world = world;
    }

    public boolean hasTridents(PlayerEntity player) {
        return !this.tridents.getOrDefault(player.getUuid(), OwnedTridents.EMPTY).isEmpty();
    }

    /**
     * Memorizes a loyal trident that is currently existing in the world
     */
    public void memorizeTrident(UUID owner, TridentEntity trident) {
        BlockPos tridentPos = trident.getBlockPos();
        this.tridents.computeIfAbsent(owner, o -> new OwnedTridents(this)).storeTridentPosition(LoyalTrident.of(trident).loyaltrident_getTridentUuid(), trident.getUuid(), tridentPos);
    }

    /**
     * Memorizes a loyal trident that is being held in another player's inventory
     */
    public void memorizeTrident(UUID owner, UUID tridentUuid, PlayerEntity holder) {
        Preconditions.checkNotNull(owner);
        Preconditions.checkNotNull(tridentUuid);
        this.tridents.computeIfAbsent(owner, o -> new OwnedTridents(this)).storeTridentHolder(tridentUuid, holder);
    }

    public void forgetTrident(UUID owner, TridentEntity trident) {
        this.tridents.getOrDefault(owner, OwnedTridents.EMPTY).clearTridentPosition(LoyalTrident.of(trident).loyaltrident_getTridentUuid());
    }

    public void loadTridents(PlayerEntity player) {
        for (TridentEntry entry : this.tridents.getOrDefault(player.getUuid(), OwnedTridents.EMPTY)) {
            entry.preloadTrident();
        }
    }

    /**
     * @return {@code true} if at least one trident was recalled
     */
    public boolean recallTridents(PlayerEntity player) {
        boolean foundAny = false;
        for (Iterator<TridentEntry> it = this.tridents.getOrDefault(player.getUuid(), OwnedTridents.EMPTY).iterator(); it.hasNext(); ) {
            TridentEntity trident = it.next().findTrident();

            if (trident == null) {
                it.remove();
                continue;
            }

            float initialDistance = trident.distanceTo(player);
            ((LoyalTrident) trident).loyaltrident_wakeUp();

            if (initialDistance > 64) {
                // reposition the trident at the same angle to the player but only 64 blocks away
                Vec3d newPos = player.getPos().add(trident.getPos().subtract(player.getPos()).normalize().multiply(64));
                trident.refreshPositionAfterTeleport(newPos);
            }

            ((LoyalTrident) trident).loyaltrident_setReturnSlot(player.getInventory().selectedSlot);
            this.world.playSound(player, trident.getX(), trident.getY(), trident.getZ(), SoundEvents.ITEM_TRIDENT_RETURN, trident.getSoundCategory(), 2.0f, 0.7f);
            ((ServerPlayerEntity) player).networkHandler.connection.send(new PlaySoundIdS2CPacket(new Identifier("item.trident.return"), trident.getSoundCategory(), trident.getPos(), trident.distanceTo(player) / 8, 0.7f));
            foundAny = true;
        }
        return foundAny;
    }

    public static LoyalTridentStorage fromNbt(ServerWorld world, NbtCompound tag) {
        LoyalTridentStorage ret = new LoyalTridentStorage(world);
        NbtList ownersNbt = tag.getList("trident_owners", NbtType.COMPOUND);
        for (int i = 0; i < ownersNbt.size(); i++) {
            OwnedTridents tridents = new OwnedTridents(ret);
            NbtCompound ownerNbt = ownersNbt.getCompound(i);
            UUID ownerUuid = ownerNbt.getUuid("owner_uuid");
            tridents.fromTag(ownerNbt);
            ret.tridents.put(ownerUuid, tridents);
        }
        return ret;
    }

    @NotNull
    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        if (!this.tridents.isEmpty()) {
            NbtList ownersNbt = new NbtList();
            this.tridents.forEach((ownerUuid, tridents) -> {
                if (!tridents.isEmpty()) {
                    NbtCompound ownerNbt = new NbtCompound();
                    ownerNbt.putUuid("owner_uuid", ownerUuid);
                    tridents.toTag(ownerNbt);
                    ownersNbt.add(ownerNbt);
                }
            });
            tag.put("trident_owners", ownersNbt);
        }
        return tag;
    }

}
