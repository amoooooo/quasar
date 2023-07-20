package coffee.amo.quasar.emitters.modules.particle_modules.update.forces;

import coffee.amo.quasar.client.QuasarParticle;

/**
 * A force that applies a gravity force to a particle.
 * @see AbstractParticleForce
 * @see coffee.amo.quasar.emitters.modules.particle_modules.update.UpdateModule
 */
public class GravityForce extends AbstractParticleForce {

    public GravityForce(float strength, float falloff) {
        this.strength = strength;
        this.falloff = falloff;
    }
    @Override
    public void applyForce(QuasarParticle particle) {
        particle.setGravity(strength);
    }
}
