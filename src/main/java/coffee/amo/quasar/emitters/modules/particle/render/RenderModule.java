package coffee.amo.quasar.emitters.modules.particle.render;

import coffee.amo.quasar.client.QuasarParticle;

public interface RenderModule {
    void apply(QuasarParticle particle, float partialTicks, RenderData data);
}
