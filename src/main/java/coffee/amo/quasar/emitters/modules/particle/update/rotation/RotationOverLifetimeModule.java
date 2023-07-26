package coffee.amo.quasar.emitters.modules.particle.update.rotation;

import coffee.amo.quasar.client.particle.QuasarParticle;
import coffee.amo.quasar.emitters.modules.ModuleType;
import coffee.amo.quasar.emitters.modules.particle.update.UpdateModule;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class RotationOverLifetimeModule implements UpdateModule {
    Function<Integer, Vec3> rotationFunction;

    public RotationOverLifetimeModule(Function<Integer, Vec3> rotationFunction) {
        this.rotationFunction = rotationFunction;
    }

    @Override
    public void run(QuasarParticle particle) {
        particle.addRotation(rotationFunction.apply(particle.getAge()));
    }

    @NotNull
    @Override
    public ModuleType<?> getType() {
        return null;
    }

    @Override
    public void renderImGuiSettings() {

    }
}
