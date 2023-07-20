package coffee.amo.quasar.emitters.modules.particle_modules.update.forces;

import coffee.amo.quasar.client.QuasarParticle;
import net.minecraft.world.phys.Vec3;

/**
 * A point force is used to apply a force in the direction away from a point.
 */
public class PointForce extends AbstractParticleForce{
    private Vec3 point;
    private float range;

    public PointForce(Vec3 point, float range, float strength, float decay) {
        this.point = point;
        this.strength = strength;
        this.falloff = decay;
        this.range = range;
    }
    @Override
    public void applyForce(QuasarParticle particle) {
        double dist = particle.getPos().subtract(point).length();
        if(dist < range) {
            // apply force to particle to move away from the point
            Vec3 particleToPoint = point.subtract(particle.getPos());
            Vec3 particleToPointUnit = particleToPoint.normalize();
            Vec3 particleToPointUnitScaled = particleToPointUnit.scale(-strength);
            particle.addForce(particleToPointUnitScaled);
        }
    }
}
