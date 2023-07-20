package coffee.amo.quasar.emitters.modules.particle_modules.update.forces;

import coffee.amo.quasar.client.QuasarParticle;
import coffee.amo.quasar.emitters.modules.particle_modules.update.UpdateModule;

public abstract class AbstractParticleForce implements UpdateModule {
    public float strength;
    public float falloff;

    public abstract void applyForce(QuasarParticle particle);

    @Override
    public void run(QuasarParticle particle) {
        applyForce(particle);
    }
}
