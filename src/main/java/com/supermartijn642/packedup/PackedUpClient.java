package com.supermartijn642.packedup;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.gui.WidgetContainerScreen;
import com.supermartijn642.core.gui.WidgetScreen;
import com.supermartijn642.core.registry.ClientRegistrationHandler;
import com.supermartijn642.packedup.packets.PacketOpenBag;
import com.supermartijn642.packedup.screen.BackpackContainerScreen;
import com.supermartijn642.packedup.screen.customization.BackpackCustomizationScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
@EventBusSubscriber(value = Dist.CLIENT)
public class PackedUpClient {

    public static KeyMapping OPEN_BAG_KEY;

    public static void register(){
        ClientRegistrationHandler handler = ClientRegistrationHandler.get("packedup");
        // Register screen for the backpack inventory
        handler.registerContainerScreen(() -> PackedUp.container, container -> WidgetContainerScreen.of(new BackpackContainerScreen(), container, true));
        // Register icon render model
        handler.registerItemModelType("icon_renderer", BackpackIconRenderer.CODEC);
    }

    @SubscribeEvent
    public static void registerKeyBindings(RegisterKeyMappingsEvent e){
        // Register key to open backpack in inventory or curious slot
        OPEN_BAG_KEY = new KeyMapping("packedup.keys.openbag", 79/*'o'*/, new KeyMapping.Category(ResourceLocation.fromNamespaceAndPath("packedup", "keys")));
        e.register(OPEN_BAG_KEY);
        NeoForge.EVENT_BUS.addListener(PackedUpClient::onKey);
    }

    public static void openBackpackRenameScreen(InteractionHand hand){
        ClientUtils.displayScreen(WidgetScreen.of(new BackpackCustomizationScreen(hand)));
    }

    public static void onKey(InputEvent.Key e){
        if(OPEN_BAG_KEY != null && OPEN_BAG_KEY.consumeClick() && ClientUtils.getWorld() != null && ClientUtils.getMinecraft().screen == null)
            PackedUp.CHANNEL.sendToServer(new PacketOpenBag());
    }

    public static Component getKeyBindCharacter(){
        return OPEN_BAG_KEY == null || OPEN_BAG_KEY.isUnbound() ? null : OPEN_BAG_KEY.getTranslatedKeyMessage();
    }
}
