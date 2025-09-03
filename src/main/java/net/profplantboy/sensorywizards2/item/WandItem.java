// src/main/java/net/profplantboy/sensorywizards2/item/WandItem.java
package net.profplantboy.sensorywizards2.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.profplantboy.sensorywizards2.component.ModComponents;
import net.profplantboy.sensorywizards2.component.ModComponents.WandParts;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class WandItem extends Item {
    public WandItem(Settings settings) {
        super(settings);
    }

    /** Build a wand stack from chosen part ids (e.g., "leather","oak","amethyst"). */
    public static ItemStack make(String handle, String rod, String tip) {
        ItemStack stack = new ItemStack(ModItems.WAND); // Always a new stack

        // Store your own parts component
        stack.set(ModComponents.WAND_PARTS, new WandParts(handle, rod, tip));

        // Drive vanilla CustomModelData:
        // SIGNATURE (in your mappings): floats, flags, strings, colors
        CustomModelDataComponent cmd = new CustomModelDataComponent(
                List.<Float>of(),                                                // floats
                List.<Boolean>of(),                                             // flags
                List.<String>of("handle/" + handle, "rod/" + rod, "tip/" + tip),// strings (indices 0,1,2)
                List.<Integer>of()                                              // colors (ARGB ints)
        );
        stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, cmd);

        return stack;
    }

    /** Back-compat: old signature that used to carry runes. Now ignored. */
    public static ItemStack make(String handle, String rod, String tip, Set<?> ignoredRunes) {
        return make(handle, rod, tip);
    }

    /** Safe accessor for parts (returns "?, ?, ?" if the component is missing). */
    public static WandParts getParts(ItemStack stack) {
        return stack.getOrDefault(ModComponents.WAND_PARTS, new WandParts("?", "?", "?"));
    }

    /** Nice name like: Wand (leather, oak, amethyst) */
    @Override
    public Text getName(ItemStack stack) {
        WandParts p = getParts(stack);
        return Text.literal("Wand (" + p.handle() + ", " + p.rod() + ", " + p.tip() + ")");
    }

    // 1.21.x tooltip signature
    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack,
                              TooltipContext context,
                              TooltipDisplayComponent displayComponent,
                              Consumer<Text> textConsumer,
                              TooltipType type) {
        WandParts p = getParts(stack);
        textConsumer.accept(Text.literal("Handle: " + p.handle()));
        textConsumer.accept(Text.literal("Rod: " + p.rod()));
        textConsumer.accept(Text.literal("Tip: " + p.tip()));
    }
}
