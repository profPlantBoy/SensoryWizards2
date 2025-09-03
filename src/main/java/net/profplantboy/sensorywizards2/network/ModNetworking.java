package net.profplantboy.sensorywizards2.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.profplantboy.sensorywizards2.SensoryWizards2;
import net.profplantboy.sensorywizards2.component.SpellComponents;

public final class ModNetworking {
    private ModNetworking() {}

    // -------- Payload: select spell --------
    public record SelectSpellC2SPayload(String spellId) implements CustomPayload {
        public static final Id<SelectSpellC2SPayload> ID =
                new Id<>(Identifier.of(SensoryWizards2.MOD_ID, "select_spell"));
        public static final PacketCodec<RegistryByteBuf, SelectSpellC2SPayload> CODEC =
                PacketCodec.of((payload, buf) -> buf.writeString(payload.spellId),
                        buf -> new SelectSpellC2SPayload(buf.readString()));

        @Override public Id<? extends CustomPayload> getId() { return ID; }
    }

    /** Server-side receiver: set selected spell on the wand in hand. */
    private static void handleSelect(ServerPlayerEntity player, SelectSpellC2SPayload msg) {
        ItemStack stack = player.getMainHandStack();
        if (stack.isEmpty()) stack = player.getOffHandStack();
        if (!stack.isEmpty()) {
            stack.set(SpellComponents.SELECTED_SPELL, msg.spellId());
            player.getItemCooldownManager().set(stack, 5); // tiny feedback; optional
        }
    }

    // -------- Register both ends --------
    public static void initServer() {
        ServerPlayNetworking.registerGlobalReceiver(
                SelectSpellC2SPayload.ID,
                (payload, context) -> context.server().execute(() ->
                        handleSelect(context.player(), payload))
        );
    }

    public static void initClient() {
        ClientPlayNetworking.registerGlobalReceiver(SelectSpellC2SPayload.ID, (p, c) -> {}); // not used
        ClientPlayNetworking.registerGlobalPayloadType(SelectSpellC2SPayload.ID, SelectSpellC2SPayload.CODEC);
    }
}
