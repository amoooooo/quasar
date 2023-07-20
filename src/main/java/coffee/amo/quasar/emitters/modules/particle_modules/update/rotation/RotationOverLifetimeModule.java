package coffee.amo.quasar.emitters.modules.particle_modules.update.rotation;

import coffee.amo.quasar.client.QuasarParticle;
import coffee.amo.quasar.emitters.modules.particle_modules.update.UpdateModule;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

public class RotationOverLifetimeModule implements UpdateModule {
    Function<Integer, Vec3> rotationFunction;

    public RotationOverLifetimeModule(Function<Integer, Vec3> rotationFunction) {
        this.rotationFunction = rotationFunction;
    }

    @Override
    public void run(QuasarParticle particle) {
        particle.setRotation(rotationFunction.apply(particle.getAge()));
    }
}
