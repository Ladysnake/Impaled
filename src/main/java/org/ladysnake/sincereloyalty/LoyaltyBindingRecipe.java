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

import com.google.gson.JsonObject;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;
import org.ladysnake.impaled.compat.EnchancementCompat;
import org.ladysnake.sincereloyalty.mixin.ForgingScreenHandlerAccessor;
import org.ladysnake.sincereloyalty.mixin.ForgingScreenHandlerInputInventoryAccessor;

import java.util.Map;
import java.util.stream.Stream;

public class LoyaltyBindingRecipe implements SmithingRecipe {
    public static void register() {
        Registry.register(Registries.RECIPE_SERIALIZER, SincereLoyalty.id("loyalty_binding"), Serializer.INSTANCE);
    }
    
    private final Identifier id;
    final Ingredient template;
    final Ingredient base;
    final Ingredient addition;

    public LoyaltyBindingRecipe(Identifier id, Ingredient template, Ingredient base, Ingredient addition) {
        this.id = id;
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
    public Identifier getId() {
        return this.id;
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
    public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
        ItemStack item = inventory.getStack(1);
        if (this.base.test(item)) {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(item);
            if (isLoyalEnough(enchantments)) {
                ItemStack result = item.copy();
                if (!EnchancementCompat.areTridentsLoyal()) {
                    // we can mutate the map as it is recreated with every call to getEnchantments
                    enchantments.put(Enchantments.LOYALTY, Enchantments.LOYALTY.getMaxLevel() + 1);
                }
                EnchantmentHelper.set(enchantments, result);
                NbtCompound loyaltyData = result.getOrCreateSubNbt(LoyalTrident.MOD_NBT_KEY);
                if (inventory instanceof ForgingScreenHandlerInputInventoryAccessor accessor) {
                    PlayerEntity player = ((ForgingScreenHandlerAccessor) accessor.impaled$screenHandler()).impaled$player();
                    loyaltyData.putUuid(LoyalTrident.TRIDENT_OWNER_NBT_KEY, player.getUuid());
                    loyaltyData.putString(LoyalTrident.OWNER_NAME_NBT_KEY, player.getName().getString());
                }
                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    public static boolean isLoyalEnough(ItemStack stack) {
        return isLoyalEnough(EnchantmentHelper.get(stack));
    }

    private static boolean isLoyalEnough(Map<Enchantment, Integer> enchantments) {
        return enchantments.getOrDefault(Enchantments.LOYALTY, 0) >= Enchantments.LOYALTY.getMaxLevel() || EnchancementCompat.areTridentsLoyal();
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return new ItemStack(Items.TRIDENT);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Serializer implements RecipeSerializer<LoyaltyBindingRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        
        public LoyaltyBindingRecipe read(Identifier identifier, JsonObject jsonObject) {
            Ingredient template = Ingredient.fromJson(JsonHelper.getElement(jsonObject, "template"));
            Ingredient base = Ingredient.fromJson(JsonHelper.getElement(jsonObject, "base"));
            Ingredient addition = Ingredient.fromJson(JsonHelper.getElement(jsonObject, "addition"));
            return new LoyaltyBindingRecipe(identifier, template, base, addition);
        }

        public LoyaltyBindingRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
            Ingredient template = Ingredient.fromPacket(packetByteBuf);
            Ingredient base = Ingredient.fromPacket(packetByteBuf);
            Ingredient addition = Ingredient.fromPacket(packetByteBuf);
            return new LoyaltyBindingRecipe(identifier, template, base, addition);
        }

        public void write(PacketByteBuf packetByteBuf, LoyaltyBindingRecipe recipe) {
            recipe.template.write(packetByteBuf);
            recipe.base.write(packetByteBuf);
            recipe.addition.write(packetByteBuf);
        }
    }
}
