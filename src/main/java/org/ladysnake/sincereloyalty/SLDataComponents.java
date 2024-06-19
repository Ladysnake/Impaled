package org.ladysnake.sincereloyalty;

import com.mojang.serialization.Codec;
import net.minecraft.component.DataComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Uuids;

import java.util.UUID;
import java.util.function.UnaryOperator;

public interface SLDataComponents {
    DataComponentType<UUID> TRIDENT_UUID = registerComponent("trident_uuid", builder -> builder.codec(Uuids.CODEC).packetCodec(Uuids.PACKET_CODEC));
    DataComponentType<String> OWNER_NAME = registerComponent("owner_name", builder -> builder.codec(Codec.STRING).packetCodec(PacketCodecs.STRING));
    DataComponentType<UUID> TRIDENT_OWNER = registerComponent("trident_owner", builder -> builder.codec(Uuids.CODEC).packetCodec(Uuids.PACKET_CODEC));
    DataComponentType<Boolean> TRIDENT_SIT = registerComponent("trident_sit", builder -> builder.codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL));
    DataComponentType<Integer> RETURN_SLOT = registerComponent("return_slot", builder -> builder.codec(Codec.INT).packetCodec(PacketCodecs.INTEGER));

    static void init() {}

    private static <T> DataComponentType<T> registerComponent(String id, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, SincereLoyalty.id(id), builderOperator.apply(DataComponentType.builder()).build());
    }
}
