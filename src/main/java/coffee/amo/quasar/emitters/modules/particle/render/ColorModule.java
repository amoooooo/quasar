package coffee.amo.quasar.emitters.modules.particle.render;

import coffee.amo.quasar.client.QuasarParticle;
import coffee.amo.quasar.emitters.modules.ModuleType;
import com.mojang.math.Vector4f;
import org.jetbrains.annotations.NotNull;

public class ColorModule implements RenderModule {
    Vector4f color;

    public ColorModule(Vector4f color) {
        this.color = color;
    }
    @Override
    public void apply(QuasarParticle particle, float partialTicks, RenderData data) {
        data.setRGBA(color.x(), color.y(), color.z(), color.w());
    }

    @NotNull
    @Override
    public ModuleType<?> getType() {
        return null;
    }
}
