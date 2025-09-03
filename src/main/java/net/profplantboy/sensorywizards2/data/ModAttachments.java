// src/main/java/net/profplantboy/sensorywizards2/data/ModAttachments.java
package net.profplantboy.sensorywizards2.data;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.util.Identifier;
import net.profplantboy.sensorywizards2.SensoryWizards2;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ModAttachments {
    // Persistent Set<String> on players: which spells are learned
    public static final AttachmentType<Set<String>> LEARNED_SPELLS =
            AttachmentRegistry.createPersistent(
                    Identifier.of(SensoryWizards2.MOD_ID, "learned_spells"),
                    Codec.STRING.listOf().xmap(HashSet::new, List::copyOf)
            );

    private ModAttachments() {}
    public static void init() {} // call once from your main mod init
}
