package com.suppergerrie2.alwayseat.mixins;

import com.suppergerrie2.alwayseat.alwayseat.AlwaysEat;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void use(Level level, Player player, InteractionHand usedHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack itemstack = player.getItemInHand(usedHand);

        if(player.canEat(AlwaysEat.canEatItemWhenFull(itemstack, player))) {
            player.startUsingItem(usedHand);
            cir.setReturnValue(InteractionResultHolder.consume(itemstack));
        } else {
            cir.setReturnValue(InteractionResultHolder.fail(itemstack));
        }
    }

}
