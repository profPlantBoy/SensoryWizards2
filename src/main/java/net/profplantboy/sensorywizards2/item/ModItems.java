// src/main/java/net/profplantboy/sensorywizards2/item/ModItems.java
package net.profplantboy.sensorywizards2.item;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.profplantboy.sensorywizards2.SensoryWizards2;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class ModItems {
    private static Identifier id(String path) { return Identifier.of(SensoryWizards2.MOD_ID, path); }
    private static RegistryKey<Item> key(String path) { return RegistryKey.of(RegistryKeys.ITEM, id(path)); }
    private static Item register(String path, Item item) { return Registry.register(Registries.ITEM, id(path), item); }

    // --- Held item ----------------------------------------------------------
    public static final Item WAND = register("wand",
            new WandItem(new Item.Settings().maxCount(1).registryKey(key("wand"))));

    // --- Base scroll item (all variants use this one class) -----------------
    public static final Item SPELL_SCROLL = register(
            "spell_scroll",
            new SpellScrollItem(new Item.Settings().registryKey(key("spell_scroll")))
    );

    // --- Wand part name lists -----------------------------------------------
    private static final String[] HANDLE_NAMES = {
            "leather","carved_bone","moss","copper_band","iron_band","gold_band","diamond_band","netherite_band"
    };
    private static final String[] ROD_NAMES = {
            "oak","dark_oak","spruce","mangrove","acacia","birch","cherry","bamboo","jungle","crimson","warped","pale_oak",
            "alder","ash","chestnut","hawthorn","holly","yew"
    };
    private static final String[] TIP_NAMES = {
            "amethyst","diamond","iron","copper","gold","netherite","mushroom_cap","sprouting_bud","blazing_cap"
    };
    private static final String[] RUNE_NAMES = {
            "glowing_blue","glowing_red","glowing_green","glowing_purple"
    };

    // --- Spell IDs shown in your custom tab (variants) ----------------------
    public static final String[] SPELL_IDS = {
            "aguamenti","alarte_ascendare","appare_vestigium","apparition","arania_exumai","ascendio","avada_kedavra",
            "avifors","baubillious","bombarda","bombarda_maxima","cave_inimicum","circumrota","colovaria","confundo",
            "crucio","depulso","evanesco","glacius","confringo","episky"
    };

    // --- Lookups for the registered part items ------------------------------
    public static final Map<String, Item> HANDLES = new LinkedHashMap<>();
    public static final Map<String, Item> RODS    = new LinkedHashMap<>();
    public static final Map<String, Item> TIPS    = new LinkedHashMap<>();
    public static final Map<String, Item> RUNES   = new LinkedHashMap<>();

    static {
        registerParts(HANDLES, "handle", HANDLE_NAMES);
        registerParts(RODS,    "rod",    ROD_NAMES);
        registerParts(TIPS,    "tip",    TIP_NAMES);
        registerParts(RUNES,   "rune",   RUNE_NAMES);
    }

    private static void registerParts(Map<String, Item> bucket, String prefix, String[] names) {
        for (String n : names) {
            String path = prefix + "_" + n; // e.g., handle_leather
            Item item = register(path, new Item(new Item.Settings().registryKey(key(path))));
            bucket.put(n, item);
        }
    }

    // --- Registration entry point (no vanilla tab edits here) ---------------
    public static void registerModItems() {
        SensoryWizards2.LOGGER.info("Registering items for {}", SensoryWizards2.MOD_ID);
        // No ItemGroupEvents here — your custom tab (ModItemGroups) handles display.
    }

    public static Item makeTestWandToEntries(String handle, String rod, String tip, Set<String> runes) {
        return WAND;
    }
}
