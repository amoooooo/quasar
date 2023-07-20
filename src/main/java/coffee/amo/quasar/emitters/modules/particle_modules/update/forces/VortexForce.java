package coffee.amo.quasar.emitters.modules.particle_modules.update.forces;

import coffee.amo.quasar.client.QuasarParticle;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

/**
 * A force that applies a vortex force to a particle.
 * @see AbstractParticleForce
 * @see coffee.amo.quasar.emitters.modules.particle_modules.update.UpdateModule
 * <p>
 *     Vortex forces are forces that are applied in a circular motion around a center point.
 *     They are useful for simulating whirlpools or tornadoes.
 *     The strength of the force is determined by the strength parameter.
 *     The falloff parameter determines how quickly the force falls off with distance. (unused)
 */
public class VortexForce extends AbstractParticleForce {
    private Vec3 vortexAxis;
    private Supplier<Vec3> vortexCenter;
    private float range;

    public VortexForce(Vec3 vortexAxis, Vec3 vortexCenter, float range, float strength, float decay) {
        this.vortexAxis = vortexAxis;
        this.vortexCenter = () -> vortexCenter;
        this.strength = strength;
        this.falloff = decay;
        this.range = range;
    }

    public VortexForce(Vec3 vortexAxis, Supplier<Vec3> vortexCenter, float range, float strength, float decay) {
        this.vortexAxis = vortexAxis;
        this.vortexCenter = vortexCenter;
        this.strength = strength;
        this.falloff = decay;
        this.range = range;
    }
    @Override
    public void applyForce(QuasarParticle particle) {
        double dist = particle.getPos().subtract(vortexCenter.get()).length();
        if(dist < range) {
            // apply force to particle to move around the vortex center on the vortex axis, but do not modify outwards/inwards velocity
            Vec3 particleToCenter = vortexCenter.get().subtract(particle.getPos());
            Vec3 particleToCenterOnAxis = particleToCenter.subtract(vortexAxis.scale(particleToCenter.dot(vortexAxis)));
            Vec3 particleToCenterOnAxisUnit = particleToCenterOnAxis.normalize();
            Vec3 particleToCenterOnAxisUnitCrossVortexAxis = particleToCenterOnAxisUnit.cross(vortexAxis);
            Vec3 particleToCenterOnAxisUnitCrossVortexAxisUnit = particleToCenterOnAxisUnitCrossVortexAxis.normalize();
            Vec3 particleToCenterOnAxisUnitCrossVortexAxisUnitScaled = particleToCenterOnAxisUnitCrossVortexAxisUnit.scale(strength);
            particle.addForce(particleToCenterOnAxisUnitCrossVortexAxisUnitScaled);
        }
    }
}
