package coffee.amo.quasar.emitters.modules.particle.init.forces;

import coffee.amo.quasar.client.particle.QuasarParticle;
import coffee.amo.quasar.emitters.modules.Module;
import coffee.amo.quasar.emitters.modules.ModuleType;
import coffee.amo.quasar.emitters.modules.particle.init.InitModule;
import coffee.amo.quasar.emitters.modules.particle.update.forces.AbstractParticleForce;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import imgui.ImGui;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class InitialVelocityForce extends AbstractParticleForce implements InitModule {
    public static final Codec<InitialVelocityForce> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Vec3.CODEC.fieldOf("direction").forGetter(InitialVelocityForce::getVelocityDirection),
                    Codec.FLOAT.fieldOf("strength").forGetter(InitialVelocityForce::getStrength),
                    Codec.FLOAT.fieldOf("falloff").forGetter(InitialVelocityForce::getFalloff)
            ).apply(instance, InitialVelocityForce::new));
    Vec3 velocityDirection;
    public Vec3 getVelocityDirection() {
        return velocityDirection;
    }

    public InitialVelocityForce(Vec3 velocityDirection, float strength, float decay) {
        this.velocityDirection = velocityDirection;
        this.strength = strength;
        this.falloff = decay;
    }
    @Override
    public void applyForce(QuasarParticle particle) {
        if(particle.getAge() == 0) {
            particle.addForce(velocityDirection.normalize().scale(strength));
        }
    }

    @Override
    public void run(QuasarParticle particle) {
        applyForce(particle);
    }

    @NotNull
    @Override
    public ModuleType<?> getType() {
        return ModuleType.INITIAL_VELOCITY;
    }

    @Override
    public void renderImGuiSettings() {
        float[] velocityDirection = new float[]{(float) this.velocityDirection.x, (float) this.velocityDirection.y, (float) this.velocityDirection.z};
        if(ImGui.dragFloat3("Velocity Direction", velocityDirection, 0.01f, -1, 1, "%.2f")){
            this.velocityDirection = new Vec3(velocityDirection[0], velocityDirection[1], velocityDirection[2]);
        }
        float[] strength = new float[]{this.strength};
        if(ImGui.dragFloat("Strength", strength, 0.01f, 0, 100, "%.2f")){
            this.strength = strength[0];
        }
    }

    @Override
    public InitialVelocityForce copy() {
        return new InitialVelocityForce(velocityDirection, strength, falloff);
    }

    @Override
    public Codec<Module> getDispatchCodec() {
        return super.getDispatchCodec();
    }
}
