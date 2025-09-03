package net.profplantboy.sensorywizards2.component;

import com.mojang.serialization.Codec;
import net.minecraft.component.DataComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.profplantboy.sensorywizards2.SensoryWizards2;

/** String components used by scrolls/wands for spell identity. */
public final class SpellComponents {
    private SpellComponents() {}

    /** On WAND stacks: which spell is currently selected. */
    public static final DataComponentType<String> SELECTED_SPELL = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(SensoryWizards2.MOD_ID, "selected_spell"),
            DataComponentType.<String>builder().codec(Codec.STRING).build()
    );

    /** On SCROLL stacks (optional): which spell this scroll represents (for UI/tooltip). */
    public static final DataComponentType<String> SCROLL_SPELL_ID = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(SensoryWizards2.MOD_ID, "scroll_spell_id"),
            DataComponentType.<String>builder().codec(Codec.STRING).build()
    );

    public static void init() {} // call in onInitialize
}
