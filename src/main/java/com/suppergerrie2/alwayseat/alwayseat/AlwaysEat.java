package com.suppergerrie2.alwayseat.alwayseat;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Objects;

@Mod(AlwaysEat.MOD_ID)
public class AlwaysEat {

    public static final String MOD_ID = "salwayseat";
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public AlwaysEat() {
        DistExecutor.safeRunWhenOn(Dist.DEDICATED_SERVER, () -> ServerEvents::new);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);

        INSTANCE.registerMessage(0, SyncSettings.class, SyncSettings::encode, SyncSettings::decode, SyncSettings::handle);

        MinecraftForge.EVENT_BUS.addListener(this::rightClickItemEvent);
    }

    public void rightClickItemEvent(PlayerInteractEvent.RightClickItem event) {
        ItemStack itemstack = event.getItemStack();
        if(!itemstack.isEdible()) return;

        Player player = event.getPlayer();

        if(player.canEat(AlwaysEat.canEatItemWhenFull(itemstack, player))) {
            player.startUsingItem(event.getHand());
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.CONSUME);
        } else {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
        }
    }

    public static boolean canEatItemWhenFull(ItemStack item, LivingEntity livingEntity) {

        String registryName = Objects.requireNonNull(item.getItem().getRegistryName()).toString();

        // If an item is in the uneatable items list always set it to false
        if (!item.isEdible() || Config.UNEATABLE_ITEMS.get().contains(registryName)) {
            return false;
        }

        // In blacklist mode all items except the ones in the list will be set to true
        if (Config.MODE.get() == Config.Mode.BLACKLIST) {
            if (!Config.ITEM_LIST.get().contains(registryName)) {
                return true;
            } else {
                return item.getFoodProperties(livingEntity).canAlwaysEat();
            }
        } else {
            // In whitelist mode only items in the list will be set to true
            if (Config.ITEM_LIST.get().contains(registryName)) {
                return true;
            } else {
                return item.getFoodProperties(livingEntity).canAlwaysEat();
            }
        }
    }


}
