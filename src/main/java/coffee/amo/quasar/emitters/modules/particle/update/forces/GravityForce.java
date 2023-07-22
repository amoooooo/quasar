package coffee.amo.quasar.emitters.modules.particle.update.forces;

import coffee.amo.quasar.client.QuasarParticle;
import coffee.amo.quasar.emitters.modules.ModuleType;
import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;

/**
 * A force that applies a gravity force to a particle.
 * @see AbstractParticleForce
 * @see coffee.amo.quasar.emitters.modules.particle.update.UpdateModule
 */
public class GravityForce extends AbstractParticleForce {
    public static final Codec<GravityForce> CODEC = Codec.FLOAT.fieldOf("strength").xmap(GravityForce::new, GravityForce::getStrength).codec();

    public GravityForce(float strength) {
        this.strength = strength;
    }
    @Override
    public void applyForce(QuasarParticle particle) {
        particle.setGravity(strength);
    }

    @Override
    public GravityForce copy() {
        return new GravityForce(strength);
    }

    @NotNull
    @Override
    public ModuleType<?> getType() {
        return ModuleType.GRAVITY;
    }

    
}
