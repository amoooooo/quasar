package coffee.amo.quasar.emitters.modules.emitter_modules.settings;

import coffee.amo.quasar.emitters.modules.emitter_modules.BaseEmitterModule;

public class EmitterSettingsModule implements BaseEmitterModule {
    EmissionShapeSettings emissionShapeSettings;
    EmissionParticleSettings emissionParticleSettings;

    public EmitterSettingsModule(EmissionShapeSettings emissionShapeSettings, EmissionParticleSettings emissionParticleSettings) {
        this.emissionShapeSettings = emissionShapeSettings;
        this.emissionParticleSettings = emissionParticleSettings;
    }

    public EmissionShapeSettings getEmissionShapeSettings() {
        return emissionShapeSettings;
    }

    public EmissionParticleSettings getEmissionParticleSettings() {
        return emissionParticleSettings;
    }



}