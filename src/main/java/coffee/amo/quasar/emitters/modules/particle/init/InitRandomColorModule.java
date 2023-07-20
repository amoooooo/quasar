package coffee.amo.quasar.emitters.modules.particle.init;

import coffee.amo.quasar.client.QuasarParticle;
import com.mojang.math.Vector4f;

public class InitRandomColorModule implements InitModule {
    Vector4f[] color;

    public InitRandomColorModule(Vector4f... color) {
        this.color = color;
    }

    @Override
    public void run(QuasarParticle particle) {
        if(particle.getAge() == 0) {
            int index = (int) (Math.random() * color.length);
            Vector4f color = this.color[index];
            particle.setColor(color);
        }
    }
}
