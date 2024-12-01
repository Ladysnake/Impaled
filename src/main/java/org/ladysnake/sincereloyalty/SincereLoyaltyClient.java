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
package org.ladysnake.sincereloyalty;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

public final class SincereLoyaltyClient implements ClientModInitializer {
    public static final SincereLoyaltyClient INSTANCE = new SincereLoyaltyClient();
    public static final int RECALL_ANIMATION_START = 10;
    public static final int RECALL_TIME = 35;   // + count 5 forced ticks serverside to load chunks

    private int useTime = 0;
    private int failedUseCountdown = 0;

    public void setFailedUse(int itemUseCooldown) {
        this.failedUseCountdown = itemUseCooldown + 1;
    }

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            TridentRecaller.RecallStatus recalling = tickTridentRecalling(mc);
            if (recalling != null) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeEnumConstant(recalling);
                ClientPlayNetworking.send(SincereLoyalty.RECALL_TRIDENTS_MESSAGE_ID, buf);
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(SincereLoyalty.RECALLING_MESSAGE_ID, (MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) -> {
            int playerId = buf.readInt();
            TridentRecaller.RecallStatus recalling = buf.readEnumConstant(TridentRecaller.RecallStatus.class);
            client.execute(() -> {
                Entity player = client.world.getEntityById(playerId);
                if (player instanceof TridentRecaller) {
                    ((TridentRecaller) player).updateRecallStatus(recalling);
                }
            });
        });
    }

    @Nullable
    private TridentRecaller.RecallStatus tickTridentRecalling(MinecraftClient mc) {
        if (mc.player == null) {
            this.failedUseCountdown = 0;
            this.useTime = 0;
            return null;
        } else if (this.failedUseCountdown > 0) {
            PlayerEntity player = mc.player;

            if (player.getMainHandStack().isEmpty()) {
                ++this.useTime;

                if (this.useTime == RECALL_ANIMATION_START) {
                    return TridentRecaller.RecallStatus.CHARGING;
                } else if (this.useTime == RECALL_TIME) {
                    this.useTime = 0;
                    return TridentRecaller.RecallStatus.RECALLING;
                }
            }
            this.failedUseCountdown--;
        } else if (this.useTime > 0) {
            this.useTime = 0;
            return TridentRecaller.RecallStatus.NONE;
        }
        return null;
    }
}
