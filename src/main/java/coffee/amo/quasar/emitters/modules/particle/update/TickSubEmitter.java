package coffee.amo.quasar.emitters.modules.particle.update;

import coffee.amo.quasar.client.particle.QuasarParticle;
import coffee.amo.quasar.emitters.ParticleContext;
import coffee.amo.quasar.emitters.ParticleEmitter;
import coffee.amo.quasar.emitters.ParticleEmitterRegistry;
import coffee.amo.quasar.emitters.ParticleSystemManager;
import coffee.amo.quasar.emitters.modules.ModuleType;
import coffee.amo.quasar.emitters.modules.particle.init.InitModule;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class TickSubEmitter implements UpdateModule {
    public static final Codec<TickSubEmitter> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("subemitter").forGetter(TickSubEmitter::getSubEmitter),
                    Codec.INT.fieldOf("frequency").forGetter(TickSubEmitter::getFrequency)
            ).apply(instance, TickSubEmitter::new));
    ResourceLocation subEmitter;
    int frequency;

    public TickSubEmitter(ResourceLocation subEmitter, int frequency) {
        this.subEmitter = subEmitter;
        this.frequency = frequency;
    }

    public ResourceLocation getSubEmitter() {
        return subEmitter;
    }

    public int getFrequency() {
        return frequency;
    }
    @Override
    public void run(QuasarParticle particle) {
        if(particle.getAge() % frequency != 0) return;
        ParticleContext context = particle.getContext();
        ParticleEmitter emitter = ParticleEmitterRegistry.getEmitter(subEmitter).instance();
        if(emitter == null) return;
        emitter.setPosition(context.particle.getPos());
        emitter.setLevel(context.particle.getLevel());
        emitter.getEmitterSettingsModule().getEmissionShapeSettings().setRandomSource(context.particle.getLevel().random);
        emitter.getEmitterSettingsModule().getEmissionShapeSettings().setPosition(context.particle.getPos());
        ParticleSystemManager.getInstance().addDelayedParticleSystem(emitter);
    }

    @NotNull
    @Override
    public ModuleType<?> getType() {
        return ModuleType.TICK_SUB_EMITTER;
    }
}
