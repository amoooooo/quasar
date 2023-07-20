package coffee.amo.quasar.emitters.modules.particle.init;

import coffee.amo.quasar.client.QuasarParticle;
import com.mojang.math.Vector4f;

public class InitColorModule implements InitModule {
    Vector4f color;

    public InitColorModule(Vector4f color) {
        this.color = color;
    }

    @Override
    public void run(QuasarParticle particle) {
        particle.setColor(color);
    }
}
