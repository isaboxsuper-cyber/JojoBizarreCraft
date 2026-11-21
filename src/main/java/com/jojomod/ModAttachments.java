package com.jojomod;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/** Dados salvos em cada jogador (todos so no servidor). */
public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, JojoMod.MOD_ID);

    /** id do stand ("" = nenhum). Persiste apos a morte. */
    public static final Supplier<AttachmentType<String>> STAND = ATTACHMENTS.register("stand",
            () -> AttachmentType.builder(() -> "").serialize(Codec.STRING).copyOnDeath().build());

    /** Stand invocado ou nao. */
    public static final Supplier<AttachmentType<Boolean>> STAND_ACTIVE = ATTACHMENTS.register("stand_active",
            () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).build());

    /** E usuario de Hamon. Persiste apos a morte. */
    public static final Supplier<AttachmentType<Boolean>> HAMON = ATTACHMENTS.register("hamon",
            () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).copyOnDeath().build());

    /** Energia do Hamon (0-100). */
    public static final Supplier<AttachmentType<Integer>> HAMON_ENERGY = ATTACHMENTS.register("hamon_energy",
            () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build());

    /** E vampiro. Persiste apos a morte. */
    public static final Supplier<AttachmentType<Boolean>> VAMPIRE = ATTACHMENTS.register("vampire",
            () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).copyOnDeath().build());
}
