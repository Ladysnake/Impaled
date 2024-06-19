package org.ladysnake.sincereloyalty;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public interface SincereLoyaltyPackets {
    record RecallTridentsPacket(TridentRecaller.RecallStatus status) implements CustomPayload {
        public static final PacketCodec<PacketByteBuf, RecallTridentsPacket> CODEC = PacketCodec.tuple(
                TridentRecaller.NET_STATUS_CODEC,
                RecallTridentsPacket::status,
                RecallTridentsPacket::new
        );
        public static final CustomPayload.Id<RecallTridentsPacket> ID = new CustomPayload.Id<>(SincereLoyalty.id("recall_tridents"));

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    record RecallingTridentsPacket(TridentRecaller.RecallStatus status, Integer playerId) implements CustomPayload {
        public static final PacketCodec<PacketByteBuf, RecallingTridentsPacket> CODEC = PacketCodec.tuple(
                TridentRecaller.NET_STATUS_CODEC,
                RecallingTridentsPacket::status,
                PacketCodecs.INTEGER,
                RecallingTridentsPacket::playerId,
                RecallingTridentsPacket::new
        );
        public static final CustomPayload.Id<RecallingTridentsPacket> ID = new CustomPayload.Id<>(SincereLoyalty.id("recalling_tridents"));

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }

    }
}
