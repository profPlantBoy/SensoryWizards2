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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Defines data components used by the mod.
 * In 1.21.8, components need a Codec (for saving) and a PacketCodec (for network sync).
 */
public final class ModComponents {

    /** Record that describes an assembled wand’s parts. */
    public record WandParts(String handle, String rod, String tip, Set<String> runes) {

        // ---------- Storage codec (disk / data pack) ----------
        // We store runes as a Set<String>. CODEC maps JSON <-> WandParts.
        public static final Codec<WandParts> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("handle").forGetter(WandParts::handle),
                Codec.STRING.fieldOf("rod").forGetter(WandParts::rod),
                Codec.STRING.fieldOf("tip").forGetter(WandParts::tip),
                // Use xmap to store a Set in JSON (backed by a list); IMPORTANT: getter must return a Set here.
                Codec.STRING.listOf().xmap(Set::copyOf, List::copyOf).fieldOf("runes").forGetter(WandParts::runes)
        ).apply(instance, (h, r, t, runes) -> new WandParts(h, r, t, Set.copyOf(runes))));

        // ---------- Network codec (client sync) ----------
        // Some Yarn versions/mappings don’t expose a convenient tuple builder; so we write a tiny manual codec.
        public static final PacketCodec<RegistryByteBuf, WandParts> PACKET_CODEC = new PacketCodec<>() {
            @Override
            public WandParts decode(RegistryByteBuf buf) {
                String handle = PacketCodecs.STRING.decode(buf);
                String rod    = PacketCodecs.STRING.decode(buf);
                String tip    = PacketCodecs.STRING.decode(buf);

                int n = buf.readVarInt(); // number of runes
                Set<String> runes = new LinkedHashSet<>(Math.max(0, n));
                for (int i = 0; i < n; i++) {
                    runes.add(PacketCodecs.STRING.decode(buf));
                }
                return new WandParts(handle, rod, tip, Set.copyOf(runes));
            }

            @Override
            public void encode(RegistryByteBuf buf, WandParts value) {
                PacketCodecs.STRING.encode(buf, value.handle());
                PacketCodecs.STRING.encode(buf, value.rod());
                PacketCodecs.STRING.encode(buf, value.tip());

                Set<String> runes = value.runes();
                buf.writeVarInt(runes.size());
                for (String r : runes) {
                    PacketCodecs.STRING.encode(buf, r);
                }
            }
        };
    }

    /** Component type that attaches WandParts to an ItemStack. */
    public static final ComponentType<WandParts> WAND_PARTS = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of("sensorywizards2", "wand_parts"),
            ComponentType.<WandParts>builder()
                    .codec(WandParts.CODEC)               // save/load
                    .packetCodec(WandParts.PACKET_CODEC)  // network sync
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
