package net.profplantboy.sensorywizards2.item;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.profplantboy.sensorywizards2.SensoryWizards2;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ModItems {
    private ModItems() {}

    // Helpers
    private static Identifier id(String path) {
        return Identifier.of(SensoryWizards2.MOD_ID, path);
    }
    private static RegistryKey<Item> key(String path) {
        return RegistryKey.of(RegistryKeys.ITEM, id(path));
    }

    // --- Keys (handy if anything else needs them) ---------------------------
    public static final RegistryKey<Item> WAND_KEY        = key("wand");
    public static final RegistryKey<Item> SPELL_SCROLL_KEY = key("spell_scroll");

    // --- Items --------------------------------------------------------------
    // Items.register(Key, factory, Settings). The factory gets the Settings.
    public static final WandItem WAND = (WandItem) Items.register(
            WAND_KEY, WandItem::new, new Item.Settings().maxCount(1)
    );

    public static final Item SPELL_SCROLL = Items.register(
            SPELL_SCROLL_KEY, SpellScrollItem::new, new Item.Settings()
    );
    public static final String[] SPELL_IDS = {
            "aguamenti","alarte_ascendare","appare_vestigium","apparition","arania_exumai","ascendio","avada_kedavra",
            "avifors","baubillious","bombarda","bombarda_maxima","cave_inimicum","circumrota","colovaria","confundo",
            "crucio","depulso","evanesco","glacius","confringo","episky"
    };
    // --- Wand part name lists ----------------------------------------------
    private static final String[] HANDLE_NAMES = {
            "leather"
    };
    private static final String[] ROD_NAMES = {
            "oak","dark_oak","spruce","mangrove","birch","cherry","bamboo","jungle","crimson","warped","pale_oak",
            "alder","ash","chestnut","hawthorn","holly","yew"
    };
    private static final String[] TIP_NAMES = {
            "amethyst","diamond","iron","copper","gold","netherite","mushroom_cap","sprouting_bud"
    };

    // --- Lookups for the registered part items -----------------------------
    public static final Map<String, Item> HANDLES = new LinkedHashMap<>();
    public static final Map<String, Item> RODS    = new LinkedHashMap<>();
    public static final Map<String, Item> TIPS    = new LinkedHashMap<>();

    // Static init: register all the part items
    static {
        registerParts(HANDLES, "handle", HANDLE_NAMES);
        registerParts(RODS,    "rod",    ROD_NAMES);
        registerParts(TIPS,    "tip",    TIP_NAMES);
    }

    private static void registerParts(Map<String, Item> bucket, String prefix, String[] names) {
        for (String n : names) {
            String path = prefix + "_" + n; // e.g., handle_leather
            RegistryKey<Item> k = key(path);
            Item item = Items.register(k, Item::new, new Item.Settings());
            bucket.put(n, item);
        }
    }

    /** Call once during mod init to ensure class loads and logs. */
    public static void init() {
        SensoryWizards2.LOGGER.info("Registered items for {}", SensoryWizards2.MOD_ID);
        // Nothing else needed; static block already ran.
    }
}
