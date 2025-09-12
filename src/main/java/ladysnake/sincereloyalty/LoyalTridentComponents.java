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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.Optional;
import java.util.UUID;

public class LoyalTridentComponents {
    
    public record LoyalTridentData(
            UUID tridentUuid,
            String ownerName,
            UUID tridentOwner,
            Optional<Integer> returnSlot
    ) {
        public static final Codec<LoyalTridentData> CODEC = RecordCodecBuilder.create(builder ->
                builder.group(
                        Uuids.CODEC.fieldOf("trident_uuid").forGetter(LoyalTridentData::tridentUuid),
                        Codec.STRING.fieldOf("owner_name").forGetter(LoyalTridentData::ownerName),
                        Uuids.CODEC.fieldOf("trident_owner").forGetter(LoyalTridentData::tridentOwner),
                        Codec.INT.optionalFieldOf("return_slot").forGetter(LoyalTridentData::returnSlot)
                ).apply(builder, LoyalTridentData::new)
        );
    }
    
    public static final ComponentType<LoyalTridentData> LOYAL_TRIDENT_DATA = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(SincereLoyalty.MOD_ID, "loyal_trident_data"),
            ComponentType.<LoyalTridentData>builder()
                    .codec(LoyalTridentData.CODEC)
                    .build()
    );

    public static void initialize() {
        // This method is called to ensure the class is loaded and components are registered
    }
}