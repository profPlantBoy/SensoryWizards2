// src/main/java/net/profplantboy/sensorywizards2/item/WandItem.java
package net.profplantboy.sensorywizards2.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.profplantboy.sensorywizards2.component.ModComponents;
import net.profplantboy.sensorywizards2.component.ModComponents.WandParts;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class WandItem extends Item {
    public WandItem(Settings settings) { super(settings); }

    /** Build a wand stack from chosen part ids (e.g., "leather","oak","amethyst", Set.of("glowing_blue")) */
    public static ItemStack make(String handle, String rod, String tip, Set<String> runes) {
        ItemStack stack = new ItemStack(ModItems.WAND);

        // 1) Store the functional parts (gameplay logic can read these)
        stack.set(ModComponents.WAND_PARTS, new WandParts(handle, rod, tip, runes));

        // 2) Drive visuals using the new custom_model_data (strings & flags)
        // strings[0] = "handle/leather", strings[1] = "rod/oak", strings[2] = "tip/amethyst"
        // flags[0..] = booleans for rune overlays
        List<String> strings = List.of("handle/" + handle, "rod/" + rod, "tip/" + tip);
        List<Boolean> flags = List.of(
                runes.contains("glowing_blue"),
                runes.contains("glowing_red"),
                runes.contains("glowing_green"),
                runes.contains("glowing_purple")
        );

        stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(
                List.of(),     // floats (unused)
                flags,         // flags
                strings,       // strings
                List.of()      // colors (optional)
        ));
        return stack;
    }

    // Nice name like: Wand (leather, oak, amethyst)
    @Override
    public Text getName(ItemStack stack) {
        WandParts p = stack.getOrDefault(
                ModComponents.WAND_PARTS,
                new WandParts("?", "?", "?", Set.of())
        );
        return Text.literal("Wand (" + p.handle() + ", " + p.rod() + ", " + p.tip() + ")");
    }

    // 1.21.8 signature (deprecated but still valid). Use Fabric's registry later if you want.
    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack,
                              TooltipContext context,
                              TooltipDisplayComponent displayComponent,
                              Consumer<Text> textConsumer,
                              TooltipType type) {
        WandParts p = stack.getOrDefault(
                ModComponents.WAND_PARTS,
                new WandParts("?", "?", "?", Set.of())
        );
        textConsumer.accept(Text.literal("Handle: " + p.handle()));
        textConsumer.accept(Text.literal("Rod: " + p.rod()));
        textConsumer.accept(Text.literal("Tip: " + p.tip()));
        if (!p.runes().isEmpty()) {
            textConsumer.accept(Text.literal("Runes: " + String.join(", ", p.runes())));
        }
    }
}
