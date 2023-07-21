package coffee.amo.quasar.emitters.modules;

import coffee.amo.quasar.emitters.modules.particle.render.RenderModuleRegistry;
import coffee.amo.quasar.emitters.modules.particle.render.TrailModule;
import coffee.amo.quasar.emitters.modules.particle.update.UpdateModuleRegistry;
import coffee.amo.quasar.emitters.modules.particle.update.forces.*;
import com.mojang.serialization.Codec;

public interface ModuleType<T extends Module> {
    ModuleType<TrailModule> TRAIL = registerRenderModule("trail", TrailModule.CODEC);
    ModuleType<GravityForce> GRAVITY = registerUpdateModule("gravity", GravityForce.CODEC);
    ModuleType<VortexForce> VORTEX = registerUpdateModule("vortex", VortexForce.CODEC);
    ModuleType<PointAttractorForce> POINT_ATTRACTOR = registerUpdateModule("point_attractor", PointAttractorForce.CODEC);
    ModuleType<VectorFieldForce> VECTOR_FIELD = registerUpdateModule("vector_field", VectorFieldForce.CODEC);
    ModuleType<DragForce> DRAG = registerUpdateModule("drag", DragForce.CODEC);
    ModuleType<WindForce> WIND = registerUpdateModule("wind", WindForce.CODEC);
    ModuleType<PointForce> POINT = registerUpdateModule("point_force", PointForce.CODEC);
    Codec<T> getCodec();

    static void bootstrap() {
        UpdateModuleRegistry.bootstrap();
        RenderModuleRegistry.bootstrap();
    }

    static <T extends Module> ModuleType<T> registerUpdateModule(String name, Codec<T> codec) {
        ModuleType<T> type = () -> codec;
        UpdateModuleRegistry.register(name, type);
        return type;
    }

    static <T extends Module> ModuleType<T> registerRenderModule(String name, Codec<T> codec) {
        ModuleType<T> type = () -> codec;
        RenderModuleRegistry.register(name, type);
        return type;
    }
}
