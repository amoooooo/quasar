package coffee.amo.quasar.emitters.modules.particle.update.forces;

import coffee.amo.quasar.client.QuasarParticle;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

/**
 * A force that attracts particles to a point.
 * @see AbstractParticleForce
 * @see coffee.amo.quasar.emitters.modules.particle.update.UpdateModule
 * <p>
 *     Point attractor forces are forces that are applied in the direction of a point.
 *     They are useful for simulating gravity or other forces that pull particles towards a point.
 *     The strength of the force is determined by the strength parameter.
 *     The falloff parameter determines how quickly the force falls off with distance. (unused)
 *     The strengthByDistance parameter determines whether the strength of the force is multiplied by the distance from the point.
 *     If strengthByDistance is true, the strength of the force is multiplied by (1 - distance / range).
 *     If strengthByDistance is false, the strength of the force is not affected by distance.
 *     The range parameter determines the maximum distance from the point at which the force is applied.
 *     If the distance from the point is greater than the range, the force is not applied.
 *     The position parameter determines the position of the point.
 *     The position parameter can be a Vec3 or a Supplier<Vec3>.
 *     If the position parameter is a Vec3, the position of the point is fixed.
 *     If the position parameter is a Supplier<Vec3>, the position of the point is updated every tick.
 *     This allows the point to move.
 * </p>
 */
public class PointAttractorForce extends AbstractParticleForce {
    Supplier<Vec3> position;
    float range;
    boolean strengthByDistance;

    public PointAttractorForce(Vec3 position, float range, float strength, float decay, boolean strengthByDistance) {
        this.position = () -> position;
        this.range = range;
        this.strength = strength;
        this.falloff = decay;
        this.strengthByDistance = strengthByDistance;
    }

    public PointAttractorForce(Supplier<Vec3> position, float range, float strength, float decay, boolean strengthByDistance) {
        this.position = position;
        this.range = range;
        this.strength = strength;
        this.falloff = decay;
        this.strengthByDistance = strengthByDistance;
    }
    @Override
    public void applyForce(QuasarParticle particle) {
        Vec3 particlePos = particle.getPos();
        Vec3 diff = particlePos.subtract(position.get());
        float distance = (float)diff.length();
        if(distance < range) {
            float strength = this.strength;
            if(strengthByDistance) {
                strength = strength * (1 - distance / range);
            }
            particle.addForce(diff.normalize().scale(-strength));
        }
    }
}
