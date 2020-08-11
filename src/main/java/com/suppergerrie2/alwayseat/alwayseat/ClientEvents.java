package com.suppergerrie2.alwayseat.alwayseat;

import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;

public class ClientEvents {

    public ClientEvents() {
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerLoggedInClientEvent);
    }

    public void onPlayerLoggedInClientEvent(ClientPlayerNetworkEvent.LoggedInEvent event) {
        AlwaysEat.updateFoodItems();
    }

}
