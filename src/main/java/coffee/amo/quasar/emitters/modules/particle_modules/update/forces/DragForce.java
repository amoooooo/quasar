package coffee.amo.quasar.emitters.modules.particle_modules.update.forces;

import coffee.amo.quasar.client.QuasarParticle;

/**
 * A force that applies a drag force to a particle.
 * @see AbstractParticleForce
 * @see coffee.amo.quasar.emitters.modules.particle_modules.update.UpdateModule
 * <p>
 *     Drag forces are forces that are applied in the opposite direction of the particle's velocity.
 *     They are useful for simulating air resistance.
 *     The strength of the force is determined by the strength parameter.
 *     The falloff parameter is unused.
 */
public class DragForce extends AbstractParticleForce {
    public DragForce(float strength, float decay) {
        this.strength = strength;
        this.falloff = decay;
    }
    @Override
    public void applyForce(QuasarParticle particle) {
        particle.modifyForce(strength);
    }
}
