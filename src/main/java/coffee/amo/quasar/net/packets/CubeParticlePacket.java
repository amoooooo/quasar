package coffee.amo.quasar.net.packets;

import coffee.amo.quasar.ParticleRegistry;
import coffee.amo.quasar.emitters.ParticleContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CubeParticlePacket {
    public int entityId;
    public String particleSystem;

    public CubeParticlePacket(String particleSystem, int entityId) {
        this.entityId = entityId;
        this.particleSystem = particleSystem;
    }

    public CubeParticlePacket(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        this.particleSystem = buf.readUtf(32767);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeUtf(this.particleSystem);
    }

    public static void handle(CubeParticlePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if(ctx.get().getDirection().getReceptionSide().isClient()) {
                ClientLevel level = Minecraft.getInstance().level;
                Entity entity = level.getEntity(msg.entityId);
                if(level == null) return;
                if(entity == null) return;
                ParticleRegistry.PARTICLE_SYSTEMS.get(msg.particleSystem).accept(new ParticleContext(entity.position(), entity.getDeltaMovement(), entity));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
