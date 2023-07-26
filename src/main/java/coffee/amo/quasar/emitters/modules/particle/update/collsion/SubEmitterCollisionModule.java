package coffee.amo.quasar.emitters.modules.particle.update.collsion;

import coffee.amo.quasar.emitters.ParticleContext;
import coffee.amo.quasar.emitters.ParticleEmitter;
import coffee.amo.quasar.emitters.ParticleEmitterRegistry;
import coffee.amo.quasar.emitters.ParticleSystemManager;
import coffee.amo.quasar.emitters.modules.ModuleType;
import coffee.amo.quasar.emitters.modules.particle.update.forces.PointForce;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SubEmitterCollisionModule extends CollisionModule {
    public static final Codec<SubEmitterCollisionModule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("subemitter").forGetter(SubEmitterCollisionModule::getSubEmitter)
    ).apply(instance, SubEmitterCollisionModule::new));
    private final ResourceLocation subEmitter;

    public ResourceLocation getSubEmitter() {
        return subEmitter;
    }

    public SubEmitterCollisionModule(ResourceLocation subEmitter) {
        super(particle -> {
            ParticleContext context = particle.getContext();
            ParticleEmitter emitter = ParticleEmitterRegistry.getEmitter(subEmitter).instance();
            if(emitter == null) return;
            emitter.setPosition(context.position);
            emitter.setLevel(context.particle.getLevel());
//            emitter.getEmitterSettingsModule().getEmissionParticleSettings().setInitialDirection(context.velocity.scale(-1f));
            emitter.getEmitterSettingsModule().getEmissionShapeSettings().setRandomSource(context.particle.getLevel().random);
            // TODO: Make this an option inside the force modules
            emitter.getParticleData().getForces().forEach(force -> {
                if(force instanceof PointForce pf){
                    pf.setPoint(context.position);
                }
            });
            ParticleSystemManager.getInstance().addParticleSystem(emitter);
        });
        this.subEmitter = subEmitter;
    }

    @Override
    public @NotNull ModuleType<?> getType() {
        return ModuleType.SUB_EMITTER_COLLISION;
    }
}
