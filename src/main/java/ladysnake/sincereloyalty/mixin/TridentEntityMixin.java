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
package ladysnake.sincereloyalty.mixin;

import ladysnake.sincereloyalty.LoyalTrident;
import ladysnake.sincereloyalty.storage.LoyalTridentStorage;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.UUID;

@Mixin(TridentEntity.class)
public abstract class TridentEntityMixin extends PersistentProjectileEntity implements LoyalTrident {
    private static final TrackedData<Boolean> sincereLoyalty$SITTING = DataTracker.registerData(TridentEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    @Shadow
    private ItemStack tridentStack;
    private @Nullable Optional<UUID> sincereLoyalty_trueOwner;

    protected TridentEntityMixin(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initDataTracker", at = @At("RETURN"))
    private void initDataTracker(CallbackInfo ci) {
        this.getDataTracker().startTracking(sincereLoyalty$SITTING, false);
    }

    @Override
    public UUID loyaltrident_getTridentUuid() {
        return LoyalTrident.getTridentUuid(this.tridentStack);
    }

    @Override
    public void loyaltrident_sit() {
        this.getDataTracker().set(sincereLoyalty$SITTING, true);
    }

    @Override
    public void loyaltrident_wakeUp() {
        this.getDataTracker().set(sincereLoyalty$SITTING, false);
    }

    @Override
    public void loyaltrident_setReturnSlot(int slot) {
        LoyalTrident.setPreferredSlot(this.tridentStack, slot);
    }

    /**
     * If the trident was dropped as an item, we want it to stay in place and not immediately return to its owner.
     *
     * <p> This redirects the loyalty check, preventing the trident from going back after it hits something,
     * and preventing it from dropping if the owner dies.
     */
    @ModifyVariable(
            method = "tick",
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/entity/projectile/TridentEntity;LOYALTY:Lnet/minecraft/entity/data/TrackedData;"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/TridentEntity;isOwnerAlive()Z")
            ),
            at = @At("STORE")
    )
    private int sit(int loyaltyLevel) {
        // If your owner told you to sit, you sit (fake no loyalty)
        if (this.getDataTracker().get(sincereLoyalty$SITTING)) {
            return 0;
        }
        return loyaltyLevel;
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void tickTrident(CallbackInfo ci) {
        if (!this.world.isClient) {
            this.getTrueTridentOwner().ifPresent(trueOwnerUuid -> {
                // Keep track of this trident's position at all time, in case the chunk goes unloaded
                LoyalTridentStorage.get((ServerWorld) this.world)
                        .memorizeTrident(trueOwnerUuid, ((TridentEntity) (Object) this));
            });
        }
    }

    @Unique
    private Optional<UUID> getTrueTridentOwner() {
        //noinspection OptionalAssignedToNull
        if (this.sincereLoyalty_trueOwner == null) {
            this.sincereLoyalty_trueOwner = Optional.ofNullable(LoyalTrident.getTrueOwner(this.tridentStack));
            // Not the owner == no loyalty
            if (this.sincereLoyalty_trueOwner.isPresent() && !sincereLoyalty_trueOwner.get().equals(((ProjectileAccessor) this).getOwnerUuid())) {
                this.loyaltrident_sit();
            }
        }
        return this.sincereLoyalty_trueOwner;
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        if (!world.isClient && !reason.shouldSave()) {
            this.getTrueTridentOwner().ifPresent(uuid ->
                    LoyalTridentStorage.get(((ServerWorld) this.world)).forgetTrident(uuid, ((TridentEntity) (Object) this)));
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    private void writeCustomDataToNbt(NbtCompound tag, CallbackInfo ci) {
        if (this.getDataTracker().get(sincereLoyalty$SITTING)) {
            tag.putBoolean(LoyalTrident.TRIDENT_SIT_NBT_KEY, true);
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
    private void readCustomDataFromNbt(NbtCompound tag, CallbackInfo ci) {
        if (tag.contains(TRIDENT_SIT_NBT_KEY)) {
            this.getDataTracker().set(sincereLoyalty$SITTING, tag.getBoolean(TRIDENT_SIT_NBT_KEY));
        }
    }
}
