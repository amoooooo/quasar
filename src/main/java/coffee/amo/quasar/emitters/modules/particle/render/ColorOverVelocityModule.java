package coffee.amo.quasar.emitters.modules.particle.render;

import coffee.amo.quasar.client.QuasarParticle;
import com.mojang.math.Vector4f;

public class ColorOverVelocityModule extends ColorModule {
    Vector4f endColor;

    public ColorOverVelocityModule(Vector4f color, Vector4f endColor) {
        super(color);
        this.endColor = endColor;
    }

    @Override
    public void apply(QuasarParticle particle, float partialTicks, RenderData data) {
        double velocity = particle.getDeltaMovement().normalize().length();
        Vector4f col = this.color;
        col.lerp(endColor, (float) velocity);
        data.setRGBA(col.x(), col.y(), col.z(), col.w());
    }
}
