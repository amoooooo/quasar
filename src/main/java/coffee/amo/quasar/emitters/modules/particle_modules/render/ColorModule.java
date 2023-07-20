package coffee.amo.quasar.emitters.modules.particle_modules.render;

import coffee.amo.quasar.client.QuasarParticle;
import com.mojang.math.Vector4f;

public class ColorModule implements RenderModule {
    Vector4f color;

    public ColorModule(Vector4f color) {
        this.color = color;
    }
    @Override
    public void apply(QuasarParticle particle, float partialTicks, RenderData data) {
        data.setRGBA(color.x(), color.y(), color.z(), color.w());
    }
}
