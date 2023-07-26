package coffee.amo.quasar.emitters.modules.particle.update.collsion;

import coffee.amo.quasar.client.particle.QuasarParticle;
import coffee.amo.quasar.emitters.modules.Module;
import coffee.amo.quasar.emitters.modules.ModuleType;
import coffee.amo.quasar.emitters.modules.particle.update.UpdateModule;
import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class CollisionModule implements UpdateModule {
    Consumer<QuasarParticle> collisionFunction;

    public CollisionModule(Consumer<QuasarParticle> collisionFunction) {
        this.collisionFunction = collisionFunction;
    }
    @Override
    public void run(QuasarParticle particle) {
        collisionFunction.accept(particle);
    }

    @NotNull
    @Override
    public ModuleType<?> getType() {
        return null;
    }

    @Override
    public void renderImGuiSettings() {

    }

    @Override
    public Codec<Module> getDispatchCodec() {
        return null;
    }
}
