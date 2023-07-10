package coffee.amo.dndmod;

import coffee.amo.dndmod.client.CubeParticle;
import coffee.amo.dndmod.client.CubeParticleData;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.RegistryObject;

import static cofh.core.CoFHCore.PARTICLES;

public class ParticleRegistry {
    public static void register() {
    }

    public static final RegistryObject<CubeParticleData> CUBE = PARTICLES.register("cube", () -> new CubeParticle());
}
