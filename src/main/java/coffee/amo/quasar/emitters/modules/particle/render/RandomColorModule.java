package coffee.amo.quasar.emitters.modules.particle.render;

import coffee.amo.quasar.client.particle.QuasarParticle;
import coffee.amo.quasar.emitters.modules.ModuleType;
import org.joml.Vector4f;
import imgui.ImGui;
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

    @Override
    public void renderImGuiSettings() {
    }
}
