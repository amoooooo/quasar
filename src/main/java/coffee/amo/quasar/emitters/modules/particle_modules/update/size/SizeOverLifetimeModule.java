package coffee.amo.quasar.emitters.modules.particle_modules.update.size;

import coffee.amo.quasar.client.QuasarParticle;
import coffee.amo.quasar.emitters.modules.particle_modules.update.UpdateModule;

import java.util.function.Function;

public class SizeOverLifetimeModule implements UpdateModule {
    Function<Integer, Float> sizeFunction;

    public SizeOverLifetimeModule(Function<Integer, Float> sizeFunction) {
        this.sizeFunction = sizeFunction;
    }
    @Override
    public void run(QuasarParticle particle) {
        particle.setScale(sizeFunction.apply(particle.getAge()));
    }
}
