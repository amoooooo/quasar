package coffee.amo.quasar.emitters.modules.particle.init;

import coffee.amo.quasar.client.QuasarParticle;
import coffee.amo.quasar.emitters.modules.ModuleType;
import coffee.amo.quasar.util.CodecUtil;
import com.mojang.math.Vector4f;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class InitColorModule implements InitModule {
    public static final Codec<InitColorModule> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    CodecUtil.VECTOR4F_CODEC.fieldOf("color").forGetter(InitColorModule::getColor)
            ).apply(instance, InitColorModule::new));
    Vector4f color;

    public Vector4f getColor() {
        return color;
    }

    public InitColorModule(Vector4f color) {
        this.color = color;
    }

    @Override
    public void run(QuasarParticle particle) {
        particle.setColor(color);
    }

    @NotNull
    @Override
    public ModuleType<?> getType() {
        return ModuleType.INIT_COLOR;
    }
}
