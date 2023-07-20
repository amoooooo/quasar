package coffee.amo.quasar.emitters.modules.particle_modules.render;

import coffee.amo.quasar.client.QuasarParticle;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiFunction;

public class RotationOverVelocityModule implements RenderModule {
    BiFunction<QuasarParticle, Float, Vec3> rotationFunction;

    public RotationOverVelocityModule(BiFunction<QuasarParticle, Float, Vec3> rotationFunction) {
        this.rotationFunction = rotationFunction;
    }
    @Override
    public void apply(QuasarParticle particle, float partialTicks, RenderData data) {
        Vec3 rotation = rotationFunction.apply(particle, partialTicks);
        data.vectorToRotation(rotation);
    }
}
