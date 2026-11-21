package com.jojomod.network;

import com.jojomod.AbilityHandler;
import com.jojomod.JojoMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** action: 0 = invocar/recolher stand, 1 = habilidade 1, 2 = habilidade 2 */
public record AbilityPayload(int action) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<AbilityPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(JojoMod.MOD_ID, "ability"));

    public static final StreamCodec<ByteBuf, AbilityPayload> CODEC =
            ByteBufCodecs.VAR_INT.map(AbilityPayload::new, AbilityPayload::action);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(AbilityPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer sp) {
                AbilityHandler.handle(sp, payload.action());
            }
        });
    }
}
