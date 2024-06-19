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

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import org.ladysnake.impaled.compat.EnchancementCompat;
import org.ladysnake.sincereloyalty.mixin.ForgingScreenHandlerAccessor;
import org.ladysnake.sincereloyalty.mixin.ForgingScreenHandlerInputInventoryAccessor;

import java.util.stream.Stream;

public class LoyaltyBindingRecipe implements SmithingRecipe {
    public static void register() {
        Registry.register(Registries.RECIPE_SERIALIZER, SincereLoyalty.id("loyalty_binding"), Serializer.INSTANCE);
    }

    final Ingredient template;
    final Ingredient base;
    final Ingredient addition;

    public LoyaltyBindingRecipe(Ingredient template, Ingredient base, Ingredient addition) {
        this.template = template;
        this.base = base;
        this.addition = addition;
    }

    @Override
    public boolean testTemplate(ItemStack stack) {
        return this.template.test(stack);
    }

    @Override
    public boolean testBase(ItemStack stack) {
        return this.base.test(stack);
    }

    @Override
    public boolean testAddition(ItemStack stack) {
        return this.addition.test(stack);
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        return this.template.test(inventory.getStack(0)) && this.base.test(inventory.getStack(1)) && this.addition.test(inventory.getStack(2)) && isLoyalEnough(inventory.getStack(1));
    }

    @Override
    public boolean isEmpty() {
        return Stream.of(this.template, this.base, this.addition).anyMatch(Ingredient::isEmpty);
    }

    @Override
    public ItemStack craft(Inventory inventory, RegistryWrapper.WrapperLookup lookup) {
        ItemStack item = inventory.getStack(1);
        if (this.base.test(item)) {
            ItemEnchantmentsComponent enchantments = EnchantmentHelper.getEnchantments(item);
            if (isLoyalEnough(enchantments)) {
                ItemStack result = item.copy();
                if (!EnchancementCompat.areTridentsLoyal()) {
                    // we can mutate the map as it is recreated with every call to getEnchantments
                    ItemEnchantmentsComponent.Builder builder =
                            new ItemEnchantmentsComponent.Builder(enchantments);
                    builder.set(Enchantments.LOYALTY, Enchantments.LOYALTY.getMaxLevel() + 1);
                    enchantments = builder.build();
                }
                result.set(DataComponentTypes.ENCHANTMENTS, enchantments);
                if (inventory instanceof ForgingScreenHandlerInputInventoryAccessor accessor) {
                    PlayerEntity player = ((ForgingScreenHandlerAccessor) accessor.impaled$screenHandler()).impaled$player();
                    result.set(SLDataComponents.TRIDENT_OWNER, player.getUuid());
                    result.set(SLDataComponents.OWNER_NAME, player.getName().getString());
                }
                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    public static boolean isLoyalEnough(ItemStack stack) {
        return isLoyalEnough(EnchantmentHelper.getEnchantments(stack));
    }

    private static boolean isLoyalEnough(ItemEnchantmentsComponent enchantments) {
        return enchantments.getLevel(Enchantments.LOYALTY) >= Enchantments.LOYALTY.getMaxLevel() || EnchancementCompat.areTridentsLoyal();
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return new ItemStack(Items.TRIDENT);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Serializer implements RecipeSerializer<LoyaltyBindingRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        private static final MapCodec<LoyaltyBindingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                Ingredient.ALLOW_EMPTY_CODEC.fieldOf("template").forGetter(recipe -> recipe.template),
                                Ingredient.ALLOW_EMPTY_CODEC.fieldOf("base").forGetter(recipe -> recipe.base),
                                Ingredient.ALLOW_EMPTY_CODEC.fieldOf("addition").forGetter(recipe -> recipe.addition)
                        )
                        .apply(instance, LoyaltyBindingRecipe::new)
        );
        public static final PacketCodec<RegistryByteBuf, LoyaltyBindingRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                LoyaltyBindingRecipe.Serializer::write, LoyaltyBindingRecipe.Serializer::read
        );

        @Override
        public MapCodec<LoyaltyBindingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, LoyaltyBindingRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static LoyaltyBindingRecipe read(RegistryByteBuf buf) {
            Ingredient ingredient = Ingredient.PACKET_CODEC.decode(buf);
            Ingredient ingredient2 = Ingredient.PACKET_CODEC.decode(buf);
            Ingredient ingredient3 = Ingredient.PACKET_CODEC.decode(buf);
            return new LoyaltyBindingRecipe(ingredient, ingredient2, ingredient3);
        }

        private static void write(RegistryByteBuf buf, LoyaltyBindingRecipe recipe) {
            Ingredient.PACKET_CODEC.encode(buf, recipe.template);
            Ingredient.PACKET_CODEC.encode(buf, recipe.base);
            Ingredient.PACKET_CODEC.encode(buf, recipe.addition);
        }
    }
}
