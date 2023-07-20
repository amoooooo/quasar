package coffee.amo.quasar.emitters.modules.particle.update.collsion;

import coffee.amo.quasar.client.QuasarParticle;
import coffee.amo.quasar.emitters.modules.particle.update.UpdateModule;

import java.util.function.Consumer;

public class CollisionModule implements UpdateModule {
    Consumer<QuasarParticle> collisionFunction;

    public CollisionModule(Consumer<QuasarParticle> collisionFunction) {
        this.collisionFunction = collisionFunction;
    }
    @Override
    public void run(QuasarParticle particle) {
        collisionFunction.accept(particle);
    }
}
