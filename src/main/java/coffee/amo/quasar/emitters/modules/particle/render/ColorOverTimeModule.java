package coffee.amo.quasar.emitters.modules.particle.render;

import coffee.amo.quasar.client.QuasarParticle;
import com.mojang.math.Vector4f;

public class ColorOverTimeModule extends ColorModule {
    Vector4f endColor;
    public ColorOverTimeModule(Vector4f color, Vector4f endColor) {
        super(color);
        this.endColor = color;
    }

    @Override
    public void apply(QuasarParticle particle, float partialTicks, RenderData data) {
        Vector4f col = this.color;
        col.lerp(endColor, ((float) particle.getAge() / particle.getLifetime()) + partialTicks);
        data.setRGBA(col.x(), col.y(), col.z(), col.w());
    }
}
