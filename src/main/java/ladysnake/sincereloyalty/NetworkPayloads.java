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
package ladysnake.sincereloyalty;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class NetworkPayloads {
    
    // Client-to-Server: Recall Tridents Request
    public record RecallTridentsPayload(TridentRecaller.RecallStatus requested) implements CustomPayload {
        public static final Id<RecallTridentsPayload> ID = new Id<>(SincereLoyalty.RECALL_TRIDENTS_MESSAGE_ID);
        
        public static final PacketCodec<RegistryByteBuf, RecallTridentsPayload> CODEC = PacketCodec.tuple(
                PacketCodecs.codec(TridentRecaller.RecallStatus.CODEC), RecallTridentsPayload::requested,
                RecallTridentsPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
    
    // Server-to-Client: Recalling Status Update
    public record RecallingStatusPayload(int playerId, TridentRecaller.RecallStatus status) implements CustomPayload {
        public static final Id<RecallingStatusPayload> ID = new Id<>(SincereLoyalty.RECALLING_MESSAGE_ID);
        
        public static final PacketCodec<RegistryByteBuf, RecallingStatusPayload> CODEC = PacketCodec.tuple(
                PacketCodecs.INTEGER, RecallingStatusPayload::playerId,
                PacketCodecs.codec(TridentRecaller.RecallStatus.CODEC), RecallingStatusPayload::status,
                RecallingStatusPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
}