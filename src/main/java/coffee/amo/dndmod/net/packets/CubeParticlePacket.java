package coffee.amo.dndmod.net.packets;

import coffee.amo.dndmod.client.CubeParticleData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CubeParticlePacket {
    public float x;
    public float y;
    public float z;
    public float speed;
    public float size;
    public int avgLifetime;
    public float gravity;
    public float r;
    public float g;
    public float b;
    public boolean hot;
    Vec3 averageDirection;
    public boolean orbit;
    public float orbitRadius;
    public Vec3 orbitAngle;

    public CubeParticlePacket(float x, float y, float z, float size, float speed, int avgLifetime, float gravity, float r, float g, float b, boolean hot, Vec3 averageDirection, boolean orbit, float orbitRadius, Vec3 orbitAngle) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.size = size;
        this.speed = speed;
        this.avgLifetime = avgLifetime;
        this.gravity = gravity;
        this.r = r;
        this.g = g;
        this.b = b;
        this.hot = hot;
        this.averageDirection = averageDirection;
        this.orbit = orbit;
        this.orbitRadius = orbitRadius;
        this.orbitAngle = orbitAngle;
    }

    public CubeParticlePacket(FriendlyByteBuf buf) {
        this.x = buf.readFloat();
        this.y = buf.readFloat();
        this.z = buf.readFloat();
        this.size = buf.readFloat();
        this.speed = buf.readFloat();
        this.avgLifetime = buf.readInt();
        this.gravity = buf.readFloat();
        this.r = buf.readFloat();
        this.g = buf.readFloat();
        this.b = buf.readFloat();
        this.hot = buf.readBoolean();
        this.averageDirection = new Vec3(buf.readFloat(), buf.readFloat(), buf.readFloat());
        this.orbit = buf.readBoolean();
        this.orbitRadius = buf.readFloat();
        this.orbitAngle = new Vec3(buf.readFloat(), buf.readFloat(), buf.readFloat());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeFloat(this.x);
        buf.writeFloat(this.y);
        buf.writeFloat(this.z);
        buf.writeFloat(this.size);
        buf.writeFloat(this.speed);
        buf.writeInt(this.avgLifetime);
        buf.writeFloat(this.gravity);
        buf.writeFloat(this.r);
        buf.writeFloat(this.g);
        buf.writeFloat(this.b);
        buf.writeBoolean(this.hot);
        buf.writeFloat((float) this.averageDirection.x);
        buf.writeFloat((float) this.averageDirection.y);
        buf.writeFloat((float) this.averageDirection.z);
        buf.writeBoolean(this.orbit);
        buf.writeFloat(this.orbitRadius);
        buf.writeFloat((float) this.orbitAngle.x);
        buf.writeFloat((float) this.orbitAngle.y);
        buf.writeFloat((float) this.orbitAngle.z);
    }

    public static void handle(CubeParticlePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if(ctx.get().getDirection().getReceptionSide().isClient()) {
                ClientLevel level = Minecraft.getInstance().level;
                if(level == null) return;
                CubeParticleData data = new CubeParticleData(msg.r, msg.g, msg.b, msg.size, msg.avgLifetime, true, msg.gravity);
                if(msg.orbit){
                    data.setOrbiting(msg.orbitRadius, msg.orbitAngle);
                }
                level.addParticle(data, msg.x, msg.y, msg.z, msg.speed * msg.averageDirection.x, msg.speed * msg.averageDirection.y, msg.speed * msg.averageDirection.z);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
