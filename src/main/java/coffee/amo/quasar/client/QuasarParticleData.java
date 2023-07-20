package coffee.amo.quasar.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import coffee.amo.quasar.emitters.modules.emitter_modules.settings.EmissionParticleSettings;
import coffee.amo.quasar.emitters.modules.particle_modules.init.InitModule;
import coffee.amo.quasar.emitters.modules.particle_modules.render.RenderModule;
import coffee.amo.quasar.emitters.modules.particle_modules.update.UpdateModule;
import coffee.amo.quasar.emitters.modules.particle_modules.update.collsion.CollisionModule;
import coffee.amo.quasar.registry.AllParticleTypes;
import coffee.amo.quasar.emitters.ICustomParticleData;
import coffee.amo.quasar.emitters.ParticleContext;
import coffee.amo.quasar.emitters.modules.particle_modules.update.forces.AbstractParticleForce;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class QuasarParticleData implements ICustomParticleData<QuasarParticleData>, ParticleOptions {
    EmissionParticleSettings particleSettings;
    public boolean shouldCollide = true;
    boolean faceVelocity = false;
    float velocityStretchFactor = 0;
    List<Consumer<ParticleContext>> subEmitters = new ArrayList<>();
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

    public void addSubEmitter(Consumer<ParticleContext> emitter) {
        subEmitters.add(emitter);
    }

    public void addSubEmitters(Consumer<ParticleContext>... emitters) {
        subEmitters.addAll(Arrays.asList(emitters));
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
    public static final Codec<QuasarParticleData> CODEC = RecordCodecBuilder.create(i -> null);

    public void removeForces(AbstractParticleForce[] forces) {
        this.forces.removeAll(Arrays.asList(forces));
    }
}