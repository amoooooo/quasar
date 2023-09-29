package coffee.amo.quasar;

import coffee.amo.quasar.emitters.*;
import coffee.amo.quasar.emitters.modules.particle.update.forces.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;
import java.util.function.Consumer;

public class ParticleRegistry {
    public static void register() {
    }

    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, "quasar");

    public static Map<String, Consumer<ParticleContext>> PARTICLE_SYSTEMS = Map.of(
            "vortex", ParticleRegistry::runVortexParticles
    );

    public static void runVortexParticles(ParticleContext context) {
        try {
            ParticleEmitter emitter = ParticleEmitterRegistry.getEmitter(ResourceLocation.tryParse("quasar:vortex")).instance();
            emitter.setLevel(context.entity.level());
            emitter.setPosition(context.position);
            emitter.getParticleData().getForces().forEach( force -> {
                if(force instanceof VortexForce vf) {
                    vf.setVortexCenter(() -> Minecraft.getInstance().player.position().add(0.0, 1.5, 0.0));
                }
                if(force instanceof PointAttractorForce paf){
                    paf.setPosition(() -> Minecraft.getInstance().player.position().add(0.0, 1.5, 0.0));
                }
            });
            ParticleSystemManager.getInstance().addParticleSystem(emitter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
