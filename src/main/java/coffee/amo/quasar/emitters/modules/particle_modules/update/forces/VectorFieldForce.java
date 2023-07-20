package coffee.amo.quasar.emitters.modules.particle_modules.update.forces;

import coffee.amo.quasar.client.QuasarParticle;
import coffee.amo.quasar.emitters.modules.particle_modules.update.fields.VectorField;

/**
 * A force that applies the force created in a vector field to a particle.
 * @see AbstractParticleForce
 * @see coffee.amo.quasar.emitters.modules.particle_modules.update.UpdateModule
 * @see VectorField
 *
 * <p>
 *     Vector fields are useful for creating complex forces that vary over time.
 */
public class VectorFieldForce extends AbstractParticleForce {
    private VectorField vectorField;

    public VectorFieldForce(VectorField vectorField, float strength, float decay) {
        this.vectorField = vectorField;
        this.strength = strength;
        this.falloff = decay;
    }
    @Override
    public void applyForce(QuasarParticle particle) {
        particle.addForce(vectorField.getVector(particle.getPos()).scale(strength));
    }
}
