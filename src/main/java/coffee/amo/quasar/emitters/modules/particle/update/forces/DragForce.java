package coffee.amo.quasar.emitters.modules.particle.update.forces;

import coffee.amo.quasar.client.particle.QuasarParticle;
import coffee.amo.quasar.emitters.modules.ModuleType;
import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;

/**
 * A force that applies a drag force to a particle.
 * @see AbstractParticleForce
 * @see coffee.amo.quasar.emitters.modules.particle.update.UpdateModule
 * <p>
 *     Drag forces are forces that are applied in the opposite direction of the particle's velocity.
 *     They are useful for simulating air resistance.
 *     The strength of the force is determined by the strength parameter.
 *     The falloff parameter is unused.
 */
public class DragForce extends AbstractParticleForce {
    public static final Codec<DragForce> CODEC = Codec.FLOAT.fieldOf("strength").xmap(DragForce::new, DragForce::getStrength).codec();
    public DragForce(float strength) {
        this.strength = strength;
    }
    @Override
    public void applyForce(QuasarParticle particle) {
        particle.modifyForce(strength);
    }

    @NotNull
    @Override
    public ModuleType<?> getType() {
        return ModuleType.DRAG;
    }

    @Override
    public DragForce copy() {
        return new DragForce(strength);
    }

    
}
