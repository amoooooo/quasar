package coffee.amo.quasar.emitters.modules.particle_modules.update.velocity;

import coffee.amo.quasar.client.QuasarParticle;
import coffee.amo.quasar.emitters.modules.particle_modules.update.UpdateModule;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

public class VelocityOverLifetimeModule implements UpdateModule {
    Function<Vec3, Vec3> velocityFunction;

    public VelocityOverLifetimeModule(Function<Vec3, Vec3> velocityFunction) {
        this.velocityFunction = velocityFunction;
    }
    @Override
    public void run(QuasarParticle particle) {
        particle.setDeltaMovement(velocityFunction.apply(particle.getDeltaMovement()));
    }
}
