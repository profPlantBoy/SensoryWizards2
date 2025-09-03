package net.profplantboy.sensorywizards2.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.profplantboy.sensorywizards2.component.ModComponents;
import net.profplantboy.sensorywizards2.component.ModComponents.WandParts;
import net.profplantboy.sensorywizards2.component.SpellComponents;

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

        // Drive vanilla CustomModelData (strings[0]=handle, [1]=rod, [2]=tip)
        CustomModelDataComponent cmd = new CustomModelDataComponent(
                List.of(),                                   // floats
                List.of(false, false, false),                // flags
                List.of("handle/" + handle, "rod/" + rod, "tip/" + tip), // strings
                List.of()                                    // colors
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

        String sel = stack.getOrDefault(SpellComponents.SELECTED_SPELL, "");
        if (!sel.isEmpty()) {
            textConsumer.accept(Text.literal("Selected Spell: " + sel));
        }
    }

    // ====== NEW: Right-click to cast the selected spell =====================

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        // Get the selected spell stored on this wand (set via your selection UI/packet)
        String spell = stack.getOrDefault(SpellComponents.SELECTED_SPELL, "");
        if (!spell.isEmpty()) {
            castSpell(world, user, spell);
            user.getItemCooldownManager().set(stack, 20); // ~1s cooldown
            return TypedActionResult.success(stack, world.isClient);
        }
        return TypedActionResult.pass(stack);
    }

    /** Minimal sample effects — swap these for your real spell logic later. */
    private static void castSpell(World world, PlayerEntity player, String id) {
        if (world.isClient) return;

        switch (id) {
            case "aguamenti" -> {
                HitResult r = player.raycast(5.0, 0, false);
                if (r.getType() == HitResult.Type.BLOCK) {
                    BlockHitResult bhr = (BlockHitResult) r;
                    var pos = bhr.getBlockPos().offset(bhr.getSide());
                    if (world.getBlockState(pos).isAir()) {
                        world.setBlockState(pos, Blocks.WATER.getDefaultState());
                        world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.PLAYERS, 1f, 1f);
                    }
                }
            }
            case "depulso" -> {
                var dir = player.getRotationVec(1f).normalize();
                var box = player.getBoundingBox().stretch(dir.multiply(4)).expand(1);
                var target = world.getOtherEntities(player, box).stream().findFirst().orElse(null);
                if (target != null) {
                    target.addVelocity(dir.x * 1.1, 0.3, dir.z * 1.1);
                    target.velocityModified = true;
                    world.playSound(null, target.getBlockPos(), SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST, SoundCategory.PLAYERS, 1f, 1.2f);
                }
            }
            case "confringo", "bombarda", "bombarda_maxima" -> {
                float power = id.equals("bombarda_maxima") ? 3.0f : 1.5f;
                world.createExplosion(player, player.getX(), player.getEyeY(), player.getZ(),
                        power, World.ExplosionSourceType.NONE); // no block damage
                world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1f, 1f);
            }
            case "glacius" -> {
                world.getOtherEntities(player, player.getBoundingBox().expand(4)).forEach(e -> {
                    if (e instanceof LivingEntity le) {
                        le.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 2));
                    }
                });
                world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_POWDER_SNOW_PLACE, SoundCategory.PLAYERS, 1f, 1f);
            }
            default -> {
                // Feedback when no effect implemented yet
                world.playSound(null, player.getBlockPos(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 0.6f, 1.6f);
            }
        }
    }
}
