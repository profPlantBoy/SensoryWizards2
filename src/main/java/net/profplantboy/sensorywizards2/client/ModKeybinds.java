package net.profplantboy.sensorywizards2.client;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public final class ModKeybinds {
    private static KeyBinding openSpellMenu;

    private ModKeybinds() {}

    public static void init() {
        openSpellMenu = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("key.sensorywizards2.open_spell_menu", GLFW.GLFW_KEY_R, "key.categories.gameplay")
        );
    }

    /** Call every client tick from your Client ModInitializer. */
    public static void clientTick() {
        while (openSpellMenu.wasPressed()) {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null) return;
            mc.setScreen(new SpellSelectScreen());
        }
    }
}
