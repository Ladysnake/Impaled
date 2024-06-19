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

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.item.SmithingTemplateItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.ladysnake.impaled.common.Impaled;
import org.ladysnake.sincereloyalty.storage.LoyalTridentStorage;

import java.util.List;
import java.util.UUID;

public final class SincereLoyalty implements ModInitializer {

    public static final String MOD_ID = Impaled.MODID;

    public static final TagKey<Item> TRIDENTS = TagKey.of(RegistryKeys.ITEM, id("tridents"));
    public static final Item LOYALTY_UPGRADE_SMITHING_TEMPLATE = new SmithingTemplateItem(
            Text.translatable(
                    Util.createTranslationKey("item", id("smithing_template.loyalty_upgrade.applies_to"))
            ).formatted(Formatting.BLUE),
            Text.translatable(
                    Util.createTranslationKey("item", id("smithing_template.loyalty_upgrade.ingredients"))
            ).formatted(Formatting.BLUE),
            Text.translatable(
                    Util.createTranslationKey("upgrade", id("loyalty_upgrade"))
            ).formatted(Formatting.GRAY),
            Text.translatable(
                    Util.createTranslationKey("item", id("smithing_template.loyalty_upgrade.base_slot_description"))
            ),
            Text.translatable(
                    Util.createTranslationKey("item", id("smithing_template.loyalty_upgrade.additions_slot_description"))
            ),
            List.of(new Identifier("item/empty_slot_sword")),
            List.of(new Identifier("item/empty_slot_amethyst_shard"))
    );

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        SLDataComponents.init();
        Object2IntMap<UUID> recallingPlayers = new Object2IntOpenHashMap<>();
        ServerTickEvents.START_SERVER_TICK.register(server -> recallingPlayers.object2IntEntrySet().removeIf(entry -> {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(entry.getKey());
            if (player == null) return true;

            if (entry.getIntValue() > 0) {
                entry.setValue(entry.getIntValue() - 1);
                return false;
            }

            LoyalTridentStorage loyalTridentStorage = LoyalTridentStorage.get(player.getServerWorld());
            TridentRecaller.RecallStatus newRecallStatus;
            if (loyalTridentStorage.recallTridents(player)) {
                newRecallStatus = TridentRecaller.RecallStatus.RECALLING;
            } else {
                player.sendMessage(Text.translatable("impaled:trident_recall_fail"), true);
                // if there is no trident to recall, reset the player's animation
                newRecallStatus = TridentRecaller.RecallStatus.NONE;
            }
            ((TridentRecaller) player).updateRecallStatus(newRecallStatus);
            return true;
        }));
        PayloadTypeRegistry.playC2S().register(SincereLoyaltyPackets.RecallTridentsPacket.ID, SincereLoyaltyPackets.RecallTridentsPacket.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(SincereLoyaltyPackets.RecallTridentsPacket.ID, (payload, context) -> {
            TridentRecaller.RecallStatus requested = payload.status();

            context.player().getServer().execute(() -> {
                LoyalTridentStorage loyalTridentStorage = LoyalTridentStorage.get(context.player().getServerWorld());
                TridentRecaller.RecallStatus currentRecallStatus = ((TridentRecaller) context.player()).getCurrentRecallStatus();
                TridentRecaller.RecallStatus newRecallStatus;

                if (loyalTridentStorage.hasTridents(context.player())) {
                    if (currentRecallStatus != requested && requested == TridentRecaller.RecallStatus.RECALLING) {
                        loyalTridentStorage.loadTridents(context.player());
                        recallingPlayers.put(context.player().getUuid(), 4);  // wait a few ticks to make sure the entity gets loaded
                    }
                    newRecallStatus = requested;
                } else {
                    newRecallStatus = TridentRecaller.RecallStatus.NONE;
                }

                ((TridentRecaller) context.player()).updateRecallStatus(newRecallStatus);
            });
        });
        Registry.register(Registries.ITEM, id("loyalty_upgrade_smithing_template"), LOYALTY_UPGRADE_SMITHING_TEMPLATE);
        LoyaltyBindingRecipe.register();
    }
}
