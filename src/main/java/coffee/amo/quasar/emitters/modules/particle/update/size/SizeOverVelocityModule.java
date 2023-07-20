package coffee.amo.quasar.emitters.modules.particle.update.size;

import coffee.amo.quasar.client.QuasarParticle;
import coffee.amo.quasar.emitters.modules.particle.update.UpdateModule;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

public class SizeOverVelocityModule implements UpdateModule {
    Function<Vec3, Float> sizeFunction;

    public SizeOverVelocityModule(Function<Vec3, Float> sizeFunction) {
        this.sizeFunction = sizeFunction;
    }

    @Override
    public void run(QuasarParticle particle) {
        particle.setScale(sizeFunction.apply(particle.getDeltaMovement()));
    }
}
