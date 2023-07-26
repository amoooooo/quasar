package coffee.amo.quasar.emitters.modules.particle.init;

import coffee.amo.quasar.client.particle.QuasarParticle;
import coffee.amo.quasar.emitters.ParticleContext;
import coffee.amo.quasar.emitters.ParticleEmitter;
import coffee.amo.quasar.emitters.ParticleEmitterRegistry;
import coffee.amo.quasar.emitters.ParticleSystemManager;
import coffee.amo.quasar.emitters.modules.ModuleType;
import coffee.amo.quasar.util.CodecUtil;
import com.mojang.math.Vector4f;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class InitSubEmitter implements InitModule {
    public static final Codec<InitSubEmitter> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("subemitter").forGetter(InitSubEmitter::getSubEmitter)
            ).apply(instance, InitSubEmitter::new));
    ResourceLocation subEmitter;

    public InitSubEmitter(ResourceLocation subEmitter) {
        this.subEmitter = subEmitter;
    }

    public ResourceLocation getSubEmitter() {
        return subEmitter;
    }

    @Override
    public void run(QuasarParticle particle) {
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
        return ModuleType.INIT_SUB_EMITTER;
    }
}
