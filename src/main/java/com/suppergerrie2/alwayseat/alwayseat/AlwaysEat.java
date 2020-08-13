package com.suppergerrie2.alwayseat.alwayseat;

import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;

@Mod(AlwaysEat.MOD_ID)
public class AlwaysEat {

    public static final String MOD_ID = "salwayseat";

    static HashMap<Item, Boolean> defaultValue = new HashMap<>(39);

    public AlwaysEat() {
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerLoggedInEvent);

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientEvents::new);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);
    }

    public void onPlayerLoggedInEvent(FMLServerStartedEvent event) {
        updateFoodItems();
    }

    public static void updateFoodItems() {
        for (Item item : ForgeRegistries.ITEMS) {
            if (item.isFood()) {

                Food food = item.getFood();

                if(!defaultValue.containsKey(item)) {
                    defaultValue.put(item, food.canEatWhenFull);
                }

                // In blacklist mode all items except the ones in the list will be set to true
                if (Config.MODE.get() == Config.Mode.BLACKLIST) {
                    if (!Config.ITEM_LIST.get().contains(item.getRegistryName().toString())) {
                        food.canEatWhenFull = true;
                    } else {
                        food.canEatWhenFull = defaultValue.get(item);
                    }
                } else {
                    // In whitelist mode only items in the list will be set to true
                    if (Config.ITEM_LIST.get().contains(item.getRegistryName().toString())) {
                        food.canEatWhenFull = true;
                    } else {
                        food.canEatWhenFull = defaultValue.get(item);
                    }
                }

                // If an item is in the uneatable items list always set it to false
                if (Config.UNEATABLE_ITEMS.get().contains(item.getRegistryName().toString())) {
                    food.canEatWhenFull = false;
                }
            }
        }
    }

}
