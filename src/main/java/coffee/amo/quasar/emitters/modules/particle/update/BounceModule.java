package coffee.amo.quasar.emitters.modules.particle.update;

import coffee.amo.quasar.client.particle.QuasarParticle;
import coffee.amo.quasar.emitters.modules.ModuleType;
import org.jetbrains.annotations.NotNull;

public class BounceModule implements UpdateModule {
    @Override
    public void run(QuasarParticle particle) {
        if(false){
            float bounciness = 2F;
            if ((particle.isOnGround() || particle.stoppedByCollision()) && (particle.getYDelta() * particle.getYDelta() > 0.05D || particle.getXDelta() * particle.getXDelta() > 0.05D
                    || particle.getZDelta() * particle.getZDelta() > 0.05D)) {
                particle.setYDelta((-particle.getYDelta() * 0.3D * bounciness * 0.4));
                particle.setXDelta(particle.getXDelta() * 0.5D * bounciness * 0.4);
                particle.setZDelta(particle.getZDelta() * 0.5D * bounciness * 0.4);
                if(particle.getXDelta() == 0) particle.setXDelta(particle.getXDelta() * -1);
                if(particle.getZDelta() == 0) particle.setZDelta(particle.getZDelta() * -1);
            }
        }
    }

    @NotNull
    @Override
    public ModuleType<?> getType() {
        return null;
    }
}
