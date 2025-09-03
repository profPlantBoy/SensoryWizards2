// src/main/java/net/profplantboy/sensorywizards2/item/ModItemGroups.java
package net.profplantboy.sensorywizards2.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.profplantboy.sensorywizards2.SensoryWizards2;

public final class ModItemGroups {
    private ModItemGroups() {}

    public static final ItemGroup SENSORYWIZARDS2 = Registry.register(
            Registries.ITEM_GROUP,
            Identifier.of(SensoryWizards2.MOD_ID, "sensorywizards2"),
            FabricItemGroup.builder()
                    .icon(() -> WandItem.make("leather", "yew", "amethyst")) // simple preview, no runes
                    .displayName(Text.translatable("itemGroup.sensorywizards2"))
                    .entries((ctx, entries) -> {
                        // Main tab
                        entries.add(ModItems.WAND);

                        entries.add(ModItems.SPELL_SCROLL);
                        for (String id : ModItems.SPELL_IDS) {
                            entries.add(SpellScrollItem.of(id));
                        }

                        ModItems.HANDLES.values().forEach(entries::add);
                        ModItems.RODS.values().forEach(entries::add);
                        ModItems.TIPS.values().forEach(entries::add);
                    })
                    .build()
    );

    // Tab that lists all wand combinations (handle × rod × tip), no rune variants
    public static final ItemGroup SENSORYWIZARDS2_WANDS = Registry.register(
            Registries.ITEM_GROUP,
            Identifier.of(SensoryWizards2.MOD_ID, "sensorywizards2_wands"),
            FabricItemGroup.builder()
                    .icon(() -> WandItem.make("leather", "pale_oak", "amethyst"))
                    .displayName(Text.translatable("itemGroup.sensorywizards2_wands"))
                    .entries((ctx, entries) -> {
                        final int MAX_WANDS_PREVIEW = 5000; // safeguard
                        int added = 0;

                        outer:
                        for (String handle : ModItems.HANDLES.keySet()) {
                            for (String rod : ModItems.RODS.keySet()) {
                                for (String tip : ModItems.TIPS.keySet()) {
                                    entries.add(WandItem.make(handle, rod, tip));
                                    if (++added >= MAX_WANDS_PREVIEW) break outer;
                                }
                            }
                        }
                    })
                    .build()
    );

    public static void init() {}
}
