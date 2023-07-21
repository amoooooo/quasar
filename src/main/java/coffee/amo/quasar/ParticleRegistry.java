package coffee.amo.quasar;

import coffee.amo.quasar.emitters.*;
import coffee.amo.quasar.emitters.modules.emitter.EmitterModule;
import coffee.amo.quasar.emitters.modules.emitter.settings.EmissionParticleSettings;
import coffee.amo.quasar.emitters.modules.emitter.settings.EmissionShape;
import coffee.amo.quasar.emitters.modules.emitter.settings.EmissionShapeSettings;
import coffee.amo.quasar.emitters.modules.emitter.settings.EmitterSettingsModule;
import coffee.amo.quasar.emitters.modules.particle.init.InitRandomColorModule;
import coffee.amo.quasar.emitters.modules.particle.init.forces.InitialVelocityForce;
import coffee.amo.quasar.emitters.modules.particle.render.*;
import coffee.amo.quasar.emitters.modules.particle.update.fields.VectorField;
import coffee.amo.quasar.emitters.modules.particle.update.forces.*;
import coffee.amo.quasar.registry.AllSpecialTextures;
import coffee.amo.quasar.util.FastNoiseLite;
import cofh.core.client.particle.types.ColorParticleType;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector4f;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
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
        try {
            ParticleEmitter emitter = ParticleEmitterRegistry.getEmitter(ResourceLocation.tryParse("quasar:vortex")).instance();
            emitter.setLevel(context.entity.level);
            emitter.setPosition(context.position);
            emitter.getParticleData().getForces().forEach( force -> {
                if(force instanceof VortexForce vf) {
                    vf.setVortexCenter(() -> Minecraft.getInstance().player.position().add(0.0, 1.5, 0.0));
                }
                if(force instanceof PointAttractorForce paf){
                    paf.setPosition(() -> Minecraft.getInstance().player.position().add(0.0, 1.5, 0.0));
                }
            });
            ParticleSystemManager.getInstance().addParticleSystem(emitter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
