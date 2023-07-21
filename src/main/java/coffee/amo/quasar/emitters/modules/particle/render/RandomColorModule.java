package coffee.amo.quasar.emitters.modules.particle.render;

import coffee.amo.quasar.client.QuasarParticle;
import coffee.amo.quasar.emitters.modules.ModuleType;
import com.mojang.math.Vector4f;
import org.jetbrains.annotations.NotNull;

public class RandomColorModule implements RenderModule {
    Vector4f[] colors;

    public RandomColorModule(Vector4f... colors) {
        this.colors = colors;
    }
    @Override
    public void apply(QuasarParticle particle, float partialTicks, RenderData data) {
        int index = (int) (Math.random() * colors.length);
        Vector4f color = colors[index];
        data.setRGBA(color.x(), color.y(), color.z(), color.w());
    }

    @NotNull
    @Override
    public ModuleType<?> getType() {
        return null;
    }
}
