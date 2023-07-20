package coffee.amo.quasar.emitters.modules.particle.render;

import coffee.amo.quasar.client.QuasarParticle;

import java.util.List;

/**
 * A module that adds trails to a particle.
 * @see TrailSettings
 * @see RenderModule
 * @see coffee.amo.quasar.emitters.modules.particle.render.RenderData
 * WARNING: Trails add a lot of time to the rendering process, so use them sparingly.
 */
public class TrailModule implements RenderModule {
    List<TrailSettings> settings;

    public TrailModule(TrailSettings... settings) {
        this.settings = List.of(settings);
    }
    @Override
    public void apply(QuasarParticle particle, float partialTicks, RenderData data) {
        data.addTrails(settings);
    }
}
