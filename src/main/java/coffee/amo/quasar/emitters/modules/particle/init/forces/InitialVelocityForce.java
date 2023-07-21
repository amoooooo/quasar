package coffee.amo.quasar.emitters.modules.particle.init.forces;

import coffee.amo.quasar.client.QuasarParticle;
import coffee.amo.quasar.emitters.modules.ModuleType;
import coffee.amo.quasar.emitters.modules.particle.init.InitModule;
import coffee.amo.quasar.emitters.modules.particle.update.forces.AbstractParticleForce;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class InitialVelocityForce extends AbstractParticleForce implements InitModule {
    Vec3 velocityDirection;

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
        return null;
    }
}
