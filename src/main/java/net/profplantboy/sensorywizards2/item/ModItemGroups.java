// src/main/java/net/profplantboy/sensorywizards2/item/ModItemGroups.java
package net.profplantboy.sensorywizards2.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.profplantboy.sensorywizards2.SensoryWizards2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ModItemGroups {
    private ModItemGroups() {}

    public static final ItemGroup SENSORYWIZARDS2 = Registry.register(
            Registries.ITEM_GROUP,
            Identifier.of(SensoryWizards2.MOD_ID, "sensorywizards2"),
            FabricItemGroup.builder()
                    .icon(() -> WandItem.make("diamond_band", "yew", "amethyst", Set.of())) // any nice preview
                    .displayName(Text.translatable("itemGroup.sensorywizards2"))
                    .entries((ctx, entries) -> {
                        // Main tab: everything except exhaustive wand variants
                        entries.add(ModItems.WAND);

                        entries.add(ModItems.SPELL_SCROLL);
                        for (String id : ModItems.SPELL_IDS) entries.add(SpellScrollItem.of(id));

                        ModItems.HANDLES.values().forEach(entries::add);
                        ModItems.RODS.values().forEach(entries::add);
                        ModItems.TIPS.values().forEach(entries::add);
                        ModItems.RUNES.values().forEach(entries::add);
                    })
                    .build()
    );

    // NEW: Tab that lists all wand combinations
    public static final ItemGroup SENSORYWIZARDS2_WANDS = Registry.register(
            Registries.ITEM_GROUP,
            Identifier.of(SensoryWizards2.MOD_ID, "sensorywizards2_wands"),
            FabricItemGroup.builder()
                    .icon(() -> WandItem.make("netherite_band", "pale_oak", "amethyst", Set.of("glowing_blue")))
                    .displayName(Text.translatable("itemGroup.sensorywizards2_wands"))
                    .entries((ctx, entries) -> {
                        final boolean INCLUDE_RUNE_VARIANTS = false; // set true to include rune combos (HUGE)
                        final int MAX_WANDS_PREVIEW = 5000;          // safeguard; adjust or remove if you dare
                        int added = 0;

                        // Collect rune keys once
                        List<String> runeKeys = new ArrayList<>(ModItems.RUNES.keySet());

                        outer:
                        for (String handle : ModItems.HANDLES.keySet()) {
                            for (String rod : ModItems.RODS.keySet()) {
                                for (String tip : ModItems.TIPS.keySet()) {
                                    if (!INCLUDE_RUNE_VARIANTS) {
                                        entries.add(WandItem.make(handle, rod, tip, Set.of()));
                                        if (++added >= MAX_WANDS_PREVIEW) break outer;
                                    } else {
                                        // All subsets of runes (2^N)
                                        int m = runeKeys.size();
                                        int combos = 1 << m;
                                        for (int mask = 0; mask < combos; mask++) {
                                            Set<String> runes = new HashSet<>();
                                            for (int i = 0; i < m; i++) {
                                                if ((mask & (1 << i)) != 0) runes.add(runeKeys.get(i));
                                            }
                                            entries.add(WandItem.make(handle, rod, tip, runes));
                                            if (++added >= MAX_WANDS_PREVIEW) break outer;
                                        }
                                    }
                                }
                            }
                        }
                    })
                    .build()
    );

    public static void init() {}
}
