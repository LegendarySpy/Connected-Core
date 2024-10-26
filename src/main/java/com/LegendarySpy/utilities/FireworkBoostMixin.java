package com.LegendarySpy.utilities;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireworkRocketItem.class)
public class FireworkBoostMixin {
    @Inject(at = @At("HEAD"), method = "use", cancellable = true)
    private void nrb_init(Level level, Player player, InteractionHand hand,
                          CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        // Only cancel the firework boost if it's disabled in config
        if (!Config.fireworkBoostEnabled) {
            // Check if player is wearing elytra (to only disable boost functionality)
            if (player.isFallFlying()) {
                cir.setReturnValue(InteractionResultHolder.pass(player.getItemInHand(hand)));
            }
        }
    }
}