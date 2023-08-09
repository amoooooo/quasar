package coffee.amo.quasar.util;

import coffee.amo.quasar.emitters.ParticleEmitter;

import java.util.List;

public interface EntityExtension {
    void addEmitter(ParticleEmitter emitter);
    List<ParticleEmitter> getEmitters();
}
