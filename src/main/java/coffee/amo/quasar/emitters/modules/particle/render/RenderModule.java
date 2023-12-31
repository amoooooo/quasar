package coffee.amo.quasar.emitters.modules.particle.render;

import coffee.amo.quasar.client.particle.QuasarParticle;
import coffee.amo.quasar.emitters.modules.Module;
import coffee.amo.quasar.emitters.modules.ModuleType;
import com.mojang.serialization.Codec;

public interface RenderModule extends Module {
    Codec<Module> DISPATCH_CODEC = RenderModuleRegistry.MODULE_MAP_CODEC.dispatch("module", renderModule -> {
        if(renderModule.getType() == null) {
            throw new IllegalStateException("Module type is null");
        }
        return renderModule.getType();
    }, ModuleType::getCodec);
    @Override
    default Codec<Module> getDispatchCodec(){
        return DISPATCH_CODEC;
    }

    void apply(QuasarParticle particle, float partialTicks, RenderData data);
}
