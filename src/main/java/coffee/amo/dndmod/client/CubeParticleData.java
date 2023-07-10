package coffee.amo.dndmod.client;

import java.util.Locale;

import coffee.amo.dndmod.AllParticleTypes;
import coffee.amo.dndmod.ICustomParticleData;
import cofh.core.client.particle.options.CoFHParticleOptions;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CubeParticleData extends CoFHParticleOptions implements ICustomParticleData<CubeParticleData> {

    public static final Codec<CubeParticleData> CODEC = RecordCodecBuilder.create(i ->
            i.group(
                            Codec.FLOAT.fieldOf("r").forGetter(p -> p.r),
                            Codec.FLOAT.fieldOf("g").forGetter(p -> p.g),
                            Codec.FLOAT.fieldOf("b").forGetter(p -> p.b),
                            Codec.FLOAT.fieldOf("scale").forGetter(p -> p.scale),
                            Codec.INT.fieldOf("avgAge").forGetter(p -> p.avgAge),
                            Codec.BOOL.fieldOf("hot").forGetter(p -> p.hot),
                    Codec.FLOAT.fieldOf("gravity").forGetter(p -> p.gravity))
                    .apply(i, CubeParticleData::new));

    public static final ParticleOptions.Deserializer<CubeParticleData> DESERIALIZER = new ParticleOptions.Deserializer<CubeParticleData>() {
        @Override
        public CubeParticleData fromCommand(ParticleType<CubeParticleData> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float r = reader.readFloat();
            reader.expect(' ');
            float g = reader.readFloat();
            reader.expect(' ');
            float b = reader.readFloat();
            reader.expect(' ');
            float scale = reader.readFloat();
            reader.expect(' ');
            int avgAge = reader.readInt();
            reader.expect(' ');
            boolean hot = reader.readBoolean();
            reader.expect(' ');
            float gravity = reader.readFloat();
            return new CubeParticleData(r, g, b, scale, avgAge, hot, gravity);
        }

        @Override
        public CubeParticleData fromNetwork(ParticleType<CubeParticleData> type, FriendlyByteBuf buffer) {
            return new CubeParticleData(buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readInt(), buffer.readBoolean(), buffer.readFloat());
        }
    };

    final float r;
    final float g;
    final float b;
    final float scale;
    final int avgAge;
    final boolean hot;
    final float gravity;

    boolean orbiting = false;
    float orbitRadius = 0;
    float orbitAngle = 0;
    Vec3 rotation = new Vec3(0,0, 0);

    public CubeParticleData(float r, float g, float b, float scale, int avgAge, boolean hot, float gravity) {
        super((ParticleType<? extends CoFHParticleOptions>) AllParticleTypes.CUBE.get(), scale, 1, 0);
        this.r = r;
        this.g = g;
        this.b = b;
        this.scale = scale;
        this.avgAge = avgAge;
        this.hot = hot;
        this.gravity = gravity;
    }

    public void setOrbiting(float radius, Vec3 rotation) {
        orbiting = true;
        orbitRadius = radius;
        this.rotation = rotation;
    }

    public float getR() {
        return r;
    }

    public float getG() {
        return g;
    }

    public float getB() {
        return b;
    }

    public float getScale() {
        return scale;
    }

    public int getAvgAge() {
        return avgAge;
    }

    public boolean isHot() {
        return hot;
    }

    public float getGravity() {
        return gravity;
    }

    public CubeParticleData() {
        this(0, 0, 0, 0, 0, false, 0);
    }

    @Override
    public Deserializer<CubeParticleData> getDeserializer() {
        return DESERIALIZER;
    }

    @Override
    public Codec<CubeParticleData> getCodec(ParticleType<CubeParticleData> type) {
        return CODEC;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ParticleProvider<CubeParticleData> getFactory() {
        return new CubeParticle.Factory();
    }

    @Override
    public ParticleType<? extends CoFHParticleOptions> getType() {
        return (ParticleType<? extends CoFHParticleOptions>) AllParticleTypes.CUBE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeFloat(r);
        buffer.writeFloat(g);
        buffer.writeFloat(b);
        buffer.writeFloat(scale);
        buffer.writeInt(avgAge);
        buffer.writeBoolean(hot);
        buffer.writeFloat(gravity);
        buffer.writeBoolean(orbiting);
        buffer.writeFloat(orbitRadius);
        buffer.writeDouble(rotation.x);
        buffer.writeDouble(rotation.y);
        buffer.writeDouble(rotation.z);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %f %f %f %f %d %s", AllParticleTypes.CUBE.parameter(), r, g, b, scale, avgAge, hot);
    }
}