package coffee.amo.dndmod.client;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

public class CubeParticleType extends ParticleType<CubeParticleData> {
    public CubeParticleType(boolean pOverrideLimiter, ParticleOptions.Deserializer<CubeParticleData> pDeserializer) {
        super(pOverrideLimiter, pDeserializer);
    }

    public CubeParticleType(){
        super(false, CubeParticleData.DESERIALIZER);
    }

    @Override
    public Codec<CubeParticleData> codec() {
        return CubeParticleData.CODEC;
    }
}
