// src/main/java/net/profplantboy/sensorywizards2/component/ModComponents.java
package net.profplantboy.sensorywizards2.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Defines data components used by the mod.
 * In 1.21.x, components need a Codec (for saving) and a PacketCodec (for network sync).
 */
public final class ModComponents {

    /** Record that describes an assembled wand’s parts (no runes). */
    public record WandParts(String handle, String rod, String tip) {

        // ---------- Storage codec (disk / data pack) ----------
        public static final Codec<WandParts> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.STRING.fieldOf("handle").forGetter(WandParts::handle),
                        Codec.STRING.fieldOf("rod").forGetter(WandParts::rod),
                        Codec.STRING.fieldOf("tip").forGetter(WandParts::tip)
                ).apply(instance, WandParts::new)
        );

        // ---------- Network codec (client sync) ----------
        public static final PacketCodec<RegistryByteBuf, WandParts> PACKET_CODEC = new PacketCodec<>() {
            @Override
            public WandParts decode(RegistryByteBuf buf) {
                String handle = PacketCodecs.STRING.decode(buf);
                String rod    = PacketCodecs.STRING.decode(buf);
                String tip    = PacketCodecs.STRING.decode(buf);
                return new WandParts(handle, rod, tip);
            }

            @Override
            public void encode(RegistryByteBuf buf, WandParts value) {
                PacketCodecs.STRING.encode(buf, value.handle());
                PacketCodecs.STRING.encode(buf, value.rod());
                PacketCodecs.STRING.encode(buf, value.tip());
            }
        };
    }

    /** Component type that attaches WandParts to an ItemStack. */
    public static final ComponentType<WandParts> WAND_PARTS = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of("sensorywizards2", "wand_parts"),
            ComponentType.<WandParts>builder()
                    .codec(WandParts.CODEC)              // save/load
                    .packetCodec(WandParts.PACKET_CODEC) // network sync
                    .build()
    );

    public static final ComponentType<String> SPELL_ID = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of("sensorywizards2", "spell_id"),
            ComponentType.<String>builder()
                    .codec(Codec.STRING)
                    .packetCodec(PacketCodecs.STRING)
                    .build()
    );

    private ModComponents() {}

    /** Call once from your mod initializer (onInitialize). */
    public static void init() {}
}
