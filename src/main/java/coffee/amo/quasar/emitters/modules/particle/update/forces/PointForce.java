package coffee.amo.quasar.emitters.modules.particle.update.forces;

import coffee.amo.quasar.client.particle.QuasarParticle;
import coffee.amo.quasar.emitters.modules.ModuleType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * A point force is used to apply a force in the direction away from a point.
 */
public class PointForce extends AbstractParticleForce{
    public static final Codec<PointForce> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Vec3.CODEC.fieldOf("point").forGetter(p -> p.getPoint().get()),
                    Codec.FLOAT.fieldOf("range").forGetter(PointForce::getRange),
                    Codec.FLOAT.fieldOf("strength").forGetter(PointForce::getStrength),
                    Codec.FLOAT.fieldOf("falloff").forGetter(PointForce::getFalloff)
            ).apply(instance, PointForce::new)
            );
    private Supplier<Vec3> point;
    public Supplier<Vec3> getPoint() {
        return point;
    }

    public void setPoint(Supplier<Vec3> point) {
        this.point = point;
    }

    public void setPoint(Vec3 point) {
        this.point = () -> point;
    }
    private float range;

    public float getRange() {
        return range;
    }

    public void setRange(float range) {
        this.range = range;
    }
    public PointForce(Vec3 point, float range, float strength, float decay) {
        this.point = () -> point;
        this.strength = strength;
        this.falloff = decay;
        this.range = range;
    }

    public PointForce(Supplier<Vec3> point, float range, float strength, float decay) {
        this.point = point;
        this.strength = strength;
        this.falloff = decay;
        this.range = range;
    }


    @Override
    public void applyForce(QuasarParticle particle) {
        double dist = particle.getPos().subtract(point.get()).length();
        if(dist < range) {
            // apply force to particle to move away from the point
            Vec3 particleToPoint = point.get().subtract(particle.getPos());
            Vec3 particleToPointUnit = particleToPoint.normalize();
            Vec3 particleToPointUnitScaled = particleToPointUnit.scale(-strength);
            particle.addForce(particleToPointUnitScaled);
        }
    }

    @NotNull
    @Override
    public ModuleType<?> getType() {
        return ModuleType.POINT;
    }

    @Override
    public PointForce copy() {
        return new PointForce(point, range, strength, falloff);
    }

    
}
