package coffee.amo.quasar.emitters.modules.particle.render;

import coffee.amo.quasar.client.particle.QuasarParticle;
import coffee.amo.quasar.emitters.modules.ModuleType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

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

    @NotNull
    @Override
    public ModuleType<?> getType() {
        return null;
    }
}
