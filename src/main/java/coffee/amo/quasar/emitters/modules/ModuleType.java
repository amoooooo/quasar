package coffee.amo.quasar.emitters.modules;

import coffee.amo.quasar.emitters.modules.particle.init.InitColorModule;
import coffee.amo.quasar.emitters.modules.particle.init.InitModuleRegistry;
import coffee.amo.quasar.emitters.modules.particle.init.InitRandomColorModule;
import coffee.amo.quasar.emitters.modules.particle.init.InitRandomRotationModule;
import coffee.amo.quasar.emitters.modules.particle.init.forces.InitialVelocityForce;
import coffee.amo.quasar.emitters.modules.particle.render.color.ColorModule;
import coffee.amo.quasar.emitters.modules.particle.render.RenderModuleRegistry;
import coffee.amo.quasar.emitters.modules.particle.render.TrailModule;
import coffee.amo.quasar.emitters.modules.particle.render.color.ColorOverTimeModule;
import coffee.amo.quasar.emitters.modules.particle.render.color.ColorOverVelocityModule;
import coffee.amo.quasar.emitters.modules.particle.update.UpdateModuleRegistry;
import coffee.amo.quasar.emitters.modules.particle.update.collsion.DieOnCollisionModule;
import coffee.amo.quasar.emitters.modules.particle.update.collsion.SubEmitterCollisionModule;
import coffee.amo.quasar.emitters.modules.particle.update.forces.*;
import com.mojang.serialization.Codec;

public interface ModuleType<T extends Module> {

    // INIOT
    ModuleType<InitialVelocityForce> INITIAL_VELOCITY = registerInitModule("initial_velocity", InitialVelocityForce.CODEC);
    ModuleType<InitColorModule> INIT_COLOR = registerInitModule("init_color", InitColorModule.CODEC);
    ModuleType<InitRandomColorModule> INIT_RANDOM_COLOR = registerInitModule("init_random_color", InitRandomColorModule.CODEC);
    ModuleType<InitRandomRotationModule> INIT_RANDOM_ROTATION = registerInitModule("init_random_rotation", InitRandomRotationModule.CODEC);

    // RENDER
    ModuleType<TrailModule> TRAIL = registerRenderModule("trail", TrailModule.CODEC);
    ModuleType<ColorModule> COLOR = registerRenderModule("color", ColorModule.CODEC);
    ModuleType<ColorOverTimeModule> COLOR_OVER_LIFETIME = registerRenderModule("color_over_lifetime", ColorOverTimeModule.CODEC);
    ModuleType<ColorOverVelocityModule> COLOR_OVER_VELOCITY = registerRenderModule("color_over_velocity", ColorOverVelocityModule.CODEC);

    // UPDATE


    // UPDATE - COLLISION
    ModuleType<DieOnCollisionModule> DIE_ON_COLLISION = registerUpdateModule("die_on_collision", DieOnCollisionModule.CODEC);
    ModuleType<SubEmitterCollisionModule> SUB_EMITTER_COLLISION = registerUpdateModule("sub_emitter_collision", SubEmitterCollisionModule.CODEC);


    // UPDATE - FORCES
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
        InitModuleRegistry.bootstrap();
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

    static <T extends Module> ModuleType<T> registerInitModule(String name, Codec<T> codec) {
        ModuleType<T> type = () -> codec;
        InitModuleRegistry.register(name, type);
        return type;
    }
}
