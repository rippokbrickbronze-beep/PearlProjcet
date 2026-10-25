package net.slix.pearlcatch;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class PearlCatchMod implements ClientModInitializer {
    // previous key states, used to detect a fresh press
    static boolean prevGui, prevBind;

    @Override
    public void onInitializeClient() {
        Settings.load();
        ClientTickEvents.END_CLIENT_TICK.register(PearlCatchMod::tick);
    }

    private static void tick(MinecraftClient mc) {
        boolean guiDown = Keys.isDown(Settings.guiKey);
        boolean bindDown = Keys.isDown(Settings.moduleBind);

        // only react to keys while in-game with no screen open (so chat typing is safe)
        if (mc.currentScreen == null && mc.player != null) {
            if (guiDown && !prevGui) {
                mc.setScreen(new ClickGui());
            } else if (bindDown && !prevBind) {
                Settings.enabled = !Settings.enabled;
            }
        }
        prevGui = guiDown;
        prevBind = bindDown;

        // the actual pearl catch logic (your PearlCatch.java), only when the module is on
        if (Settings.enabled) {
            PearlCatch.onTick();
        }
    }
}
