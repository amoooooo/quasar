package coffee.amo.quasar.emitters.modules.particle.update.size;

import coffee.amo.quasar.client.particle.QuasarParticle;
import coffee.amo.quasar.emitters.modules.ModuleType;
import coffee.amo.quasar.emitters.modules.particle.update.UpdateModule;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class SizeOverLifetimeModule implements UpdateModule {
    Function<Integer, Float> sizeFunction;

    public SizeOverLifetimeModule(Function<Integer, Float> sizeFunction) {
        this.sizeFunction = sizeFunction;
    }
    @Override
    public void run(QuasarParticle particle) {
        particle.setScale(sizeFunction.apply(particle.getAge()));
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
