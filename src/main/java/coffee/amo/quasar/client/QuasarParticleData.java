package coffee.amo.quasar.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import coffee.amo.quasar.emitters.modules.emitter.settings.EmissionParticleSettings;
import coffee.amo.quasar.emitters.modules.particle.init.InitModule;
import coffee.amo.quasar.emitters.modules.particle.init.InitModuleRegistry;
import coffee.amo.quasar.emitters.modules.particle.render.RenderModule;
import coffee.amo.quasar.emitters.modules.particle.render.RenderModuleRegistry;
import coffee.amo.quasar.emitters.modules.particle.update.UpdateModule;
import coffee.amo.quasar.emitters.modules.particle.update.UpdateModuleRegistry;
import coffee.amo.quasar.emitters.modules.particle.update.collsion.CollisionModule;
import coffee.amo.quasar.registry.AllParticleTypes;
import coffee.amo.quasar.emitters.ICustomParticleData;
import coffee.amo.quasar.emitters.ParticleContext;
import coffee.amo.quasar.emitters.modules.particle.update.forces.AbstractParticleForce;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Data that is passed to each particle when it is created.
 *
 * @see ICustomParticleData
 * @see QuasarParticle
 * @see ParticleContext
 * <p>
 * This class is used to store all the data that is passed to each particle when it is created.
 * This includes the particle settings, whether or not the particle should collide with blocks,
 * whether or not the particle should face its velocity, and the list of sub emitters.
 * This class also stores the list of particle modules that are applied to each particle.
 * These modules are used to modify the particle's behavior.
 * The list of particle modules includes init modules, render modules, update modules, and collision modules.
 * Init modules are applied when the particle is created.
 * Render modules are applied when the particle is rendered.
 * Update modules are applied every tick.
 * Collision modules are applied when the particle collides with a block.
 * This class also stores the list of particle forces that are applied to each particle.
 * These forces are used to modify the particle's velocity.
 */
public class QuasarParticleData implements ICustomParticleData<QuasarParticleData>, ParticleOptions {
    public static final Codec<QuasarParticleData> CODEC = RecordCodecBuilder.create(i ->
            i.group(
                    Codec.BOOL.optionalFieldOf("should_collide", true).forGetter(QuasarParticleData::shouldCollide),
                    Codec.BOOL.optionalFieldOf("face_velocity", false).forGetter(QuasarParticleData::getFaceVelocity),
                    Codec.FLOAT.optionalFieldOf("velocity_stretch_factor", 0.0f).forGetter(QuasarParticleData::getVelocityStretchFactor),
                    ResourceLocation.CODEC.listOf().fieldOf("init_modules").xmap(
                            r -> r.stream().map(InitModuleRegistry::getModule).collect(Collectors.toList()),
                            r -> r.stream().map(InitModuleRegistry::getModuleId).collect(Collectors.toList())
                    ).forGetter(QuasarParticleData::getInitModules),
                    ResourceLocation.CODEC.listOf().fieldOf("update_modules").xmap(
                            r -> r.stream().map(UpdateModuleRegistry::getModule).collect(Collectors.toList()),
                            r -> r.stream().map(UpdateModuleRegistry::getModuleId).collect(Collectors.toList())
                    ).forGetter(QuasarParticleData::getUpdateModules),
                    ResourceLocation.CODEC.listOf().fieldOf("collision_modules").orElse(List.of()).xmap(
                            r -> {
                                return r.stream().map(f -> (CollisionModule) UpdateModuleRegistry.getModule(f)).collect(Collectors.toList());
                            },
                            r -> {
                                return r.stream().map(f -> UpdateModuleRegistry.getModuleId((CollisionModule) f)).collect(Collectors.toList());
                            }
                    ).forGetter(QuasarParticleData::getCollisionModules),
                    ResourceLocation.CODEC.listOf().fieldOf("forces").xmap(
                            r -> {
                                return r.stream().map(f -> (AbstractParticleForce) UpdateModuleRegistry.getModule(f)).collect(Collectors.toList());
                            },
                            r -> {
                                return r.stream().map(f -> UpdateModuleRegistry.getModuleId((UpdateModule) f)).collect(Collectors.toList());
                            }
                    ).forGetter(QuasarParticleData::getForces),
                    ResourceLocation.CODEC.listOf().fieldOf("render_modules").xmap(
                            r -> r.stream().map(RenderModuleRegistry::getModule).collect(Collectors.toList()),
                            r -> r.stream().map(RenderModuleRegistry::getModuleId).collect(Collectors.toList())
                    ).forGetter(QuasarParticleData::getRenderModules)
            ).apply(i, (shouldCollide, faceVelocity, velocityStretchFactor, initModules, updateModules, collisionModules, forces, renderModules) -> {
                        QuasarParticleData data = new QuasarParticleData(shouldCollide, faceVelocity, velocityStretchFactor);
                        data.initModules = initModules;
                        data.updateModules = updateModules;
                        data.collisionModules = collisionModules;
                        data.renderModules = renderModules;
                        data.forces = forces;
                        return data;
                    }
            )
    );

    public ResourceLocation registryId;
    EmissionParticleSettings particleSettings;
    public boolean shouldCollide = true;
    boolean faceVelocity = false;
    float velocityStretchFactor = 0;
    List<ResourceLocation> subEmitters = new ArrayList<>();
    List<AbstractParticleForce> forces = new ArrayList<>();
    List<InitModule> initModules = new ArrayList<>();
    List<RenderModule> renderModules = new ArrayList<>();
    List<UpdateModule> updateModules = new ArrayList<>();
    List<CollisionModule> collisionModules = new ArrayList<>();


    public QuasarParticleData(EmissionParticleSettings particleSettings) {
        this.particleSettings = particleSettings;
    }

    public QuasarParticleData(EmissionParticleSettings particleSettings, boolean shouldCollide) {
        this.particleSettings = particleSettings;
        this.shouldCollide = shouldCollide;
    }

    public QuasarParticleData(EmissionParticleSettings particleSettings, boolean shouldCollide, boolean faceVelocity) {
        this.particleSettings = particleSettings;
        this.shouldCollide = shouldCollide;
        this.faceVelocity = faceVelocity;
    }

    public QuasarParticleData(EmissionParticleSettings particleSettings, boolean shouldCollide, boolean faceVelocity, float velocityStretchFactor) {
        this.particleSettings = particleSettings;
        this.shouldCollide = shouldCollide;
        this.faceVelocity = faceVelocity;
        this.velocityStretchFactor = velocityStretchFactor;
    }

    public QuasarParticleData(boolean shouldCollide, boolean faceVelocity, float velocityStretchFactor) {
        this.particleSettings = null;
        this.shouldCollide = shouldCollide;
        this.faceVelocity = faceVelocity;
        this.velocityStretchFactor = velocityStretchFactor;
    }

    public QuasarParticleData() {
        this(null, false, false, 0.0f);
    }

    public void addInitModule(InitModule module) {
        initModules.add(module);
    }

    public void addInitModules(InitModule... modules) {
        initModules.addAll(Arrays.asList(modules));
    }

    public void addRenderModule(RenderModule module) {
        renderModules.add(module);
    }

    public void addRenderModules(RenderModule... modules) {
        renderModules.addAll(Arrays.asList(modules));
    }

    public void addUpdateModule(UpdateModule module) {
        updateModules.add(module);
    }

    public void addUpdateModules(UpdateModule... modules) {
        updateModules.addAll(Arrays.asList(modules));
    }

    public void addCollisionModule(CollisionModule module) {
        collisionModules.add(module);
    }

    public void addCollisionModules(CollisionModule... modules) {
        collisionModules.addAll(Arrays.asList(modules));
    }

    public void addForce(AbstractParticleForce force) {
        forces.add(force);
    }

    public void addForces(AbstractParticleForce... forces) {
        this.forces.addAll(Arrays.asList(forces));
    }

    public void addSubEmitter(ResourceLocation emitter) {
        subEmitters.add(emitter);
    }

    public void addSubEmitters(ResourceLocation... emitters) {
        subEmitters.addAll(Arrays.asList(emitters));
    }


    public EmissionParticleSettings getParticleSettings() {
        return particleSettings;
    }

    public void setParticleSettings(EmissionParticleSettings particleSettings) {
        this.particleSettings = particleSettings;
    }

    public boolean shouldCollide() {
        return shouldCollide;
    }

    public boolean getFaceVelocity() {
        return faceVelocity;
    }

    public float getVelocityStretchFactor() {
        return velocityStretchFactor;
    }

    public List<ResourceLocation> getSubEmitters() {
        return subEmitters;
    }

    public List<AbstractParticleForce> getForces() {
        return forces;
    }

    public List<InitModule> getInitModules() {
        return initModules;
    }

    public List<RenderModule> getRenderModules() {
        return renderModules;
    }

    public List<UpdateModule> getUpdateModules() {
        return updateModules;
    }

    public List<CollisionModule> getCollisionModules() {
        return collisionModules;
    }








    /*
     * MOJANG SHIT
     */

    @Override
    public Codec<QuasarParticleData> getCodec(ParticleType<QuasarParticleData> type) {
        return CODEC;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ParticleProvider<QuasarParticleData> getFactory() {
        return new QuasarParticle.Factory();
    }

    @Override
    public ParticleType<? extends ParticleOptions> getType() {
        return AllParticleTypes.QUASAR_BASE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
    }

    @Override
    public String writeToString() {
        return "";
    }

    @Override
    public Deserializer<QuasarParticleData> getDeserializer() {
        return DESERIALIZER;
    }

    public static final ParticleOptions.Deserializer<QuasarParticleData> DESERIALIZER = new ParticleOptions.Deserializer<QuasarParticleData>() {
        @Override
        public QuasarParticleData fromCommand(ParticleType<QuasarParticleData> type, StringReader reader) throws CommandSyntaxException {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("Don't use this");
        }

        @Override
        public QuasarParticleData fromNetwork(ParticleType<QuasarParticleData> type, FriendlyByteBuf buffer) {
            return new QuasarParticleData(null);
        }
    };

    public void removeForces(AbstractParticleForce[] forces) {
        this.forces.removeAll(Arrays.asList(forces));
    }

    public QuasarParticleData instance() {
        QuasarParticleData data = new QuasarParticleData(particleSettings, shouldCollide, faceVelocity, velocityStretchFactor);
        data.initModules = initModules;
        data.updateModules = updateModules;
        data.renderModules = renderModules;
        data.collisionModules = collisionModules;
        data.forces = forces;
        data.registryId = registryId;
        return data;
    }
}