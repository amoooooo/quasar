package coffee.amo.quasar.emitters.modules.particle.update.forces;

import coffee.amo.quasar.client.QuasarParticle;
import net.minecraft.world.phys.Vec3;

/**
 * A force that applies a wind force to a particle.
 * @see AbstractParticleForce
 * @see coffee.amo.quasar.emitters.modules.particle.update.UpdateModule
 *
 * <p>
 *     Wind forces are useful for simulating wind.
 *     The strength of the force is determined by the strength parameter.
 *     The falloff parameter is unused.
 *     The direction and speed of the wind is determined by the windDirection and windSpeed parameters.
 *     The windDirection parameter is a vector that determines the direction of the wind.
 *     The windSpeed parameter determines the speed of the wind.
 *     The windSpeed parameter is measured in blocks per tick.
 */
public class WindForce extends AbstractParticleForce {
    Vec3 windDirection;
    float windSpeed;
    public WindForce(Vec3 windDirection, float windSpeed, float strength, float falloff) {
        this.windDirection = windDirection;
        this.windSpeed = windSpeed;
        this.strength = strength;
        this.falloff = falloff;
    }
    @Override
    public void applyForce(QuasarParticle particle) {
        particle.addForce(windDirection.scale(windSpeed));
    }
}
