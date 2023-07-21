package coffee.amo.quasar.emitters.modules.emitter.settings;

import coffee.amo.quasar.emitters.modules.emitter.BaseEmitterModule;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public class EmitterSettingsModule implements BaseEmitterModule {
    // TODO: Get the settings from a "registry" by resource location so you can split up files
    public static final Codec<EmitterSettingsModule> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                ResourceLocation.CODEC.fieldOf("shape").xmap(
                        EmitterSettingsRegistry::getShapeSettings,
                        EmitterSettingsRegistry::getShapeSettingsId
                ).forGetter(EmitterSettingsModule::getEmissionShapeSettings),
                ResourceLocation.CODEC.fieldOf("particle").xmap(
                        EmitterSettingsRegistry::getParticleSettings,
                        EmitterSettingsRegistry::getParticleSettingsId
                ).forGetter(EmitterSettingsModule::getEmissionParticleSettings)
        ).apply(instance, EmitterSettingsModule::new);
    });
    EmissionShapeSettings emissionShapeSettings;
    EmissionParticleSettings emissionParticleSettings;

    public EmitterSettingsModule(EmissionShapeSettings emissionShapeSettings, EmissionParticleSettings emissionParticleSettings) {
        this.emissionShapeSettings = emissionShapeSettings;
        this.emissionParticleSettings = emissionParticleSettings;
    }

    public EmitterSettingsModule instance() {
        return new EmitterSettingsModule(emissionShapeSettings.instance(), emissionParticleSettings.instance());
    }

    public EmissionShapeSettings getEmissionShapeSettings() {
        return emissionShapeSettings;
    }

    public EmissionParticleSettings getEmissionParticleSettings() {
        return emissionParticleSettings;
    }



}
