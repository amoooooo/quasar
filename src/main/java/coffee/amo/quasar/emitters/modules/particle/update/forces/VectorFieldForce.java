package coffee.amo.quasar.emitters.modules.particle.update.forces;

import coffee.amo.quasar.client.QuasarParticle;
import coffee.amo.quasar.emitters.modules.ModuleType;
import coffee.amo.quasar.emitters.modules.particle.update.fields.VectorField;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * A force that applies the force created in a vector field to a particle.
 * @see AbstractParticleForce
 * @see coffee.amo.quasar.emitters.modules.particle.update.UpdateModule
 * @see VectorField
 *
 * <p>
 *     Vector fields are useful for creating complex forces that vary over time.
 */
public class VectorFieldForce extends AbstractParticleForce {
    public static final Codec<VectorFieldForce> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    VectorField.CODEC.fieldOf("vector_field").forGetter(VectorFieldForce::getVectorField),
                    Codec.FLOAT.fieldOf("strength").forGetter(VectorFieldForce::getStrength),
                    Codec.FLOAT.fieldOf("falloff").forGetter(VectorFieldForce::getFalloff)
            ).apply(instance, VectorFieldForce::new)
            );
    private VectorField vectorField;
    public VectorField getVectorField() {
        return vectorField;
    }

    public VectorFieldForce(VectorField vectorField, float strength, float decay) {
        this.vectorField = vectorField;
        this.strength = strength;
        this.falloff = decay;
    }
    @Override
    public void applyForce(QuasarParticle particle) {
        particle.addForce(vectorField.getVector(particle.getPos()).scale(strength));
    }

    @NotNull
    @Override
    public ModuleType<?> getType() {
        return ModuleType.VECTOR_FIELD;
    }
}
