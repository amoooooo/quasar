package coffee.amo.quasar;

import coffee.amo.quasar.emitters.*;
import coffee.amo.quasar.emitters.modules.emitter_modules.EmitterModule;
import coffee.amo.quasar.emitters.modules.emitter_modules.settings.EmissionParticleSettings;
import coffee.amo.quasar.emitters.modules.emitter_modules.settings.EmissionShape;
import coffee.amo.quasar.emitters.modules.emitter_modules.settings.EmissionShapeSettings;
import coffee.amo.quasar.emitters.modules.emitter_modules.settings.EmitterSettingsModule;
import coffee.amo.quasar.emitters.modules.particle_modules.init.InitRandomColorModule;
import coffee.amo.quasar.emitters.modules.particle_modules.init.forces.InitialVelocityForce;
import coffee.amo.quasar.emitters.modules.particle_modules.render.*;
import coffee.amo.quasar.emitters.modules.particle_modules.update.BounceModule;
import coffee.amo.quasar.emitters.modules.particle_modules.update.collsion.CollisionModule;
import coffee.amo.quasar.emitters.modules.particle_modules.update.fields.VectorField;
import coffee.amo.quasar.emitters.modules.particle_modules.update.forces.*;
import coffee.amo.quasar.registry.AllSpecialTextures;
import coffee.amo.quasar.util.FastNoiseLite;
import cofh.core.client.particle.types.ColorParticleType;
import com.mojang.math.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;
import java.util.function.Consumer;

public class ParticleRegistry {
    public static void register() {
    }

    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, "quasar");

    public static final RegistryObject<ColorParticleType> FOG = PARTICLES.register("fog", () -> {
        return new ColorParticleType(true);
    });

    public static final RegistryObject<SimpleParticleType> CRACK = PARTICLES.register("crack", () -> {
        return new SimpleParticleType(true);
    });

    public static Map<String, Consumer<ParticleContext>> PARTICLE_SYSTEMS = Map.of(
            "vortex", ParticleRegistry::runVortexParticles
    );

    public static void runVortexParticles(ParticleContext context) {
        EmitterModule emitterModule = new EmitterModule(context.position, 600, false, 1, 2);
        EmissionShapeSettings shapeSettings = new EmissionShapeSettings.Builder()
                .setShape(EmissionShape.DISC)
                .setDimensions(new Vec3(5, 5, 5))
                .setRandomSource(context.entity.level.random)
                .setPosition(() -> Minecraft.getInstance().player.position())
                .setRotation(new Vec3(0.0, 0.0, 0.0))
                .setFromSurface(true)
                .build();
        EmissionParticleSettings particleSettings = new EmissionParticleSettings.Builder()
                .setParticleLifetime(240)
                .setParticleSpeed(0.0f)
                .setBaseParticleSize(0.03f)
                .setInitialDirection(() -> new Vec3(0.0, 1.0, 0.0))
                .setRandomSource(context.entity.level.random)
                .build();
        EmitterSettingsModule settings = new EmitterSettingsModule(shapeSettings, particleSettings);
        ParticleEmitter emitter = new ParticleEmitter(context.entity.level, emitterModule, settings);
        emitter.getParticleData().addInitModule(new InitialVelocityForce(new Vec3(0.0, 0.01f, 0.0), 0.1f, 1.0f));
        emitter.getParticleData().addInitModule(new InitRandomColorModule(new Vector4f(0.25f, 0.25f, 0.25f, 1.0f), new Vector4f(71/255f, 135/255f, 135/255f, 1.0f)));
        emitter.getParticleData().addRenderModule(
                new TrailModule(
                        new TrailSettings(
                                1,
                                6,
                                null,
                                (index, ageMod) -> {
                                    float value = 0f;
                                    return (1 - index) * 0.06f * ageMod;
                                },
                                AllSpecialTextures.BLANK.getLocation(),
                                (point, index, velocity) -> {
                                    return point;
                                }
                        )
                )
        );
        emitter.getParticleData().addForce(new GravityForce(0.6f, 1.0f));
        FastNoiseLite noise = new FastNoiseLite();
        noise.SetFractalLacunarity(2.0f);
        noise.SetFractalGain(1.5f);
        noise.SetFractalOctaves(1);
        noise.SetFrequency(0.5f);
        noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        noise.SetFractalType(FastNoiseLite.FractalType.FBm);
        noise.SetDomainWarpType(FastNoiseLite.DomainWarpType.OpenSimplex2);
        emitter.getParticleData().addForce(
                new VortexForce(
                        new Vec3(0.0, 1.0f, 0.0),
                        () -> Minecraft.getInstance().player.position().add(0.0, 0.75f, 0.0),
                        10f,
                        0.15f,
                        1.0f
                )
        );
        emitter.getParticleData().addForce(new PointAttractorForce(() -> Minecraft.getInstance().player.position().add(0.0, 0.75f, 0.0), 15f, 0.2f, 1.0f, true));
        emitter.getParticleData().addForce(new VectorFieldForce(
                new VectorField(noise, 0.25f, null),
                0.45f, 1.0f));
        emitter.getParticleData().addForce(new DragForce(0.775f, 1.0f));
        emitter.getParticleData().addForce(new WindForce(new Vec3(0.0, 0.25f, 0.0f), 0.25f, 1.0f, 1.0f));

        ParticleSystemManager.getInstance().addParticleSystem(emitter);
    }

}
