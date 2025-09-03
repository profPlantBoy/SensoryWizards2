// src/main/java/net/profplantboy/sensorywizards2/item/SpellScrollItem.java
package net.profplantboy.sensorywizards2.item;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.profplantboy.sensorywizards2.component.ModComponents;
import net.profplantboy.sensorywizards2.data.ModAttachments;

import java.util.List;
import java.util.Set;

public class SpellScrollItem extends Item {
    public SpellScrollItem(Settings settings) { super(settings); }

    /** Factory: make a stack for a specific spell id (e.g. "fireball"). */
    public static ItemStack of(String spellId) {
        ItemStack stack = new ItemStack(ModItems.SPELL_SCROLL);

        // Which spell this scroll teaches
        stack.set(ModComponents.SPELL_ID, spellId);

        // Drive model/texture via CustomModelData strings
        stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(
                List.of(),                      // floats
                List.of(),                      // flags
                List.of("spell/" + spellId),    // strings[0]
                List.of()                       // colors
        ));
        return stack;
    }

    /** Display name per-variant (fallback to generic if not present in lang). */
    @Override
    public Text getName(ItemStack stack) {
        String id = stack.getOrDefault(ModComponents.SPELL_ID, "unknown");
        // If the specific key is missing, you'll just see the key; also add a generic key in lang if you want.
        return Text.translatable("item.sensorywizards2.spell_scroll." + id);
    }

    /** Right-click to learn + (if not creative) consume the scroll. */
    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        String spellId = stack.get(ModComponents.SPELL_ID);
        if (spellId == null || spellId.isEmpty()) {
            return ActionResult.PASS;
        }

        if (!world.isClient) {
            @SuppressWarnings("unchecked")
            Set<String> learned = ((AttachmentTarget) user)
                    .getAttachedOrSet(ModAttachments.LEARNED_SPELLS, new java.util.HashSet<>());

            if (learned.add(spellId)) {
                user.sendMessage(Text.literal("You learned: " + spellId), true);
                user.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                if (!user.isCreative()) {
                    stack.decrement(1); // consume
                }
            } else {
                user.sendMessage(Text.literal("You already know: " + spellId), true);
                user.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.7f, 0.9f);
            }
        }

        return ActionResult.SUCCESS;
    }
}
