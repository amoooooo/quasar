package coffee.amo.quasar.emitters.modules.particle.init.forces;

import coffee.amo.quasar.client.QuasarParticle;
import coffee.amo.quasar.emitters.modules.Module;
import coffee.amo.quasar.emitters.modules.ModuleType;
import coffee.amo.quasar.emitters.modules.particle.init.InitModule;
import coffee.amo.quasar.emitters.modules.particle.update.forces.AbstractParticleForce;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class InitialVelocityForce extends AbstractParticleForce implements InitModule {
    public static final Codec<InitialVelocityForce> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Vec3.CODEC.fieldOf("direction").forGetter(InitialVelocityForce::getVelocityDirection),
                    Codec.FLOAT.fieldOf("strength").forGetter(InitialVelocityForce::getStrength),
                    Codec.FLOAT.fieldOf("falloff").forGetter(InitialVelocityForce::getFalloff)
            ).apply(instance, InitialVelocityForce::new));
    Vec3 velocityDirection;
    public Vec3 getVelocityDirection() {
        return velocityDirection;
    }

    public InitialVelocityForce(Vec3 velocityDirection, float strength, float decay) {
        this.velocityDirection = velocityDirection;
        this.strength = strength;
        this.falloff = decay;
    }
    @Override
    public void applyForce(QuasarParticle particle) {
        if(particle.getAge() == 0) {
            particle.addForce(velocityDirection.normalize().scale(strength));
        }
    }

    @Override
    public void run(QuasarParticle particle) {
        applyForce(particle);
    }

    @NotNull
    @Override
    public ModuleType<?> getType() {
        return ModuleType.INITIAL_VELOCITY;
    }

    @Override
    public InitialVelocityForce copy() {
        return new InitialVelocityForce(velocityDirection, strength, falloff);
    }

    @Override
    public Codec<Module> getDispatchCodec() {
        return super.getDispatchCodec();
    }
}
