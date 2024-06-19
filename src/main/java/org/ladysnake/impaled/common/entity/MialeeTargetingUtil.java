package org.ladysnake.impaled.common.entity;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public interface MialeeTargetingUtil {
    record MialeeTargetPayload(Integer entityID) implements CustomPayload {
        public static final PacketCodec<PacketByteBuf, MialeeTargetPayload> CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, MialeeTargetPayload::entityID, MialeeTargetPayload::new);
        public static final Id<MialeeTargetPayload> ID = new Id<>(new Identifier("mialee_misc", "target"));

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    static void init() {
        PayloadTypeRegistry.playC2S().register(MialeeTargetPayload.ID, MialeeTargetPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(MialeeTargetPayload.ID, (payload, context) -> {
            ServerPlayerEntity serverPlayer = context.player();
            serverPlayer.getServer().execute(() -> {
                if (serverPlayer instanceof IPlayerTargeting targeting) {
                    if (serverPlayer.getWorld().getEntityById(payload.entityID()) instanceof LivingEntity living) {
                        targeting.mialeeMisc$setLastTarget(living);
                    }
                }
            });
        });
    }
}
