package coffee.amo.quasar.emitters.modules.particle_modules.init;

import coffee.amo.quasar.client.QuasarParticle;
import com.mojang.math.Vector4f;
import net.minecraft.world.phys.Vec3;

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
