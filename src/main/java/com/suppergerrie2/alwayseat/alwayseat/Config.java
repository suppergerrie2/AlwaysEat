package com.suppergerrie2.alwayseat.alwayseat;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class Config {

    public static final String CATEGORY_EATABLE = "eatable";

    public static ForgeConfigSpec SERVER_CONFIG;

    public static ForgeConfigSpec.ConfigValue<List<?>> ITEM_LIST;
    public static ForgeConfigSpec.ConfigValue<List<?>> UNEATABLE_ITEMS;
    public static ForgeConfigSpec.EnumValue<Mode> MODE;

    enum Mode {
        BLACKLIST,
        WHITELIST
    }

    static {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

        SERVER_BUILDER.comment("Eatable settings").push(CATEGORY_EATABLE);

        ITEM_LIST = SERVER_BUILDER
                .comment(
                        "List of items",
                        "Depending on the mode only these items will be made eatable (WHITELIST) or these items will keep their vanilla behaviour (BLACKLIST)",
                        "If an item is not affected according to the rules above they will keep their vanilla behaviour"
                )
                .defineList("item_list", new ArrayList<>(), Config::isValidResourceLocation);
        UNEATABLE_ITEMS = SERVER_BUILDER
                .comment(
                        "List of items",
                        "These items will be made uneatable while full (Overrides vanilla behaviour)"
                )
                .defineList("uneatable_list", new ArrayList<>(), Config::isValidResourceLocation);
        MODE = SERVER_BUILDER
                .comment("Mode as explained in other settings")
                .defineEnum("mode", Mode.BLACKLIST);

        SERVER_BUILDER.pop();

        SERVER_CONFIG = SERVER_BUILDER.build();
    }

    static boolean isValidResourceLocation(Object o) {
        if(o instanceof ResourceLocation) return true;

        if(o instanceof String resourceName) {
            return ResourceLocation.tryParse(resourceName) != null;
        }

        return false;
    }

}
