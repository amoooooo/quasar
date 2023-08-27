package coffee.amo.quasar.emitters.modules.particle.update.forces;

import coffee.amo.quasar.QuasarClient;
import coffee.amo.quasar.client.particle.QuasarParticle;
import coffee.amo.quasar.emitters.modules.ModuleType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import imgui.ImGui;
import imgui.flag.ImGuiDataType;
import imgui.type.ImBoolean;
import imgui.type.ImFloat;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * A force that applies a vortex force to a particle.
 * @see AbstractParticleForce
 * @see coffee.amo.quasar.emitters.modules.particle.update.UpdateModule
 * <p>
 *     Vortex forces are forces that are applied in a circular motion around a center point.
 *     They are useful for simulating whirlpools or tornadoes.
 *     The strength of the force is determined by the strength parameter.
 *     The falloff parameter determines how quickly the force falls off with distance. (unused)
 */
public class VortexForce extends AbstractParticleForce {
    public static final Codec<VortexForce> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Vec3.CODEC.fieldOf("vortex_axis").forGetter(VortexForce::getVortexAxis),
                    Vec3.CODEC.fieldOf("vortex_center").forGetter(VortexForce::getVortexCenter),
                    Codec.FLOAT.fieldOf("range").forGetter(VortexForce::getRange),
                    Codec.FLOAT.fieldOf("strength").forGetter(VortexForce::getStrength),
                    Codec.FLOAT.fieldOf("falloff").forGetter(VortexForce::getFalloff)
            ).apply(instance, VortexForce::new)
            );
    private Vec3 vortexAxis;
    public Vec3 getVortexAxis() {
        return vortexAxis;
    }
    public void setVortexAxis(Vec3 vortexAxis) {
        this.vortexAxis = vortexAxis;
    }
    private Supplier<Vec3> vortexCenter;
    public Vec3 getVortexCenter() {
        return vortexCenter.get();
    }
    public void setVortexCenter(Supplier<Vec3> vortexCenter) {
        this.vortexCenter = vortexCenter;
    }
    public void setVortexCenter(Vec3 vortexCenter) {
        this.vortexCenter = () -> vortexCenter;
    }
    private float range;
    public float getRange() {
        return range;
    }
    public void setRange(float range) {
        this.range = range;
    }

    public VortexForce(Vec3 vortexAxis, Vec3 vortexCenter, float range, float strength, float decay) {
        this.vortexAxis = vortexAxis;
        this.vortexCenter = () -> vortexCenter;
        this.strength = strength;
        this.falloff = decay;
        this.range = range;
    }

    public VortexForce(Vec3 vortexAxis, Supplier<Vec3> vortexCenter, float range, float strength, float decay) {
        this.vortexAxis = vortexAxis;
        this.vortexCenter = vortexCenter;
        this.strength = strength;
        this.falloff = decay;
        this.range = range;
    }
    @Override
    public void applyForce(QuasarParticle particle) {
        double dist = particle.getPos().subtract(vortexCenter.get()).length();
        if(dist < range) {
            // apply force to particle to move around the vortex center on the vortex axis, but do not modify outwards/inwards velocity
            Vec3 particleToCenter = vortexCenter.get().subtract(particle.getPos());
            Vec3 particleToCenterOnAxis = particleToCenter.subtract(vortexAxis.scale(particleToCenter.dot(vortexAxis)));
            Vec3 particleToCenterOnAxisUnit = particleToCenterOnAxis.normalize();
            Vec3 particleToCenterOnAxisUnitCrossVortexAxis = particleToCenterOnAxisUnit.cross(vortexAxis);
            Vec3 particleToCenterOnAxisUnitCrossVortexAxisUnit = particleToCenterOnAxisUnitCrossVortexAxis.normalize();
            Vec3 particleToCenterOnAxisUnitCrossVortexAxisUnitScaled = particleToCenterOnAxisUnitCrossVortexAxisUnit.scale(strength);
            particle.addForce(particleToCenterOnAxisUnitCrossVortexAxisUnitScaled);
        }
    }

    @NotNull
    @Override
    public ModuleType<?> getType() {
        return ModuleType.VORTEX;
    }

    public ImBoolean shouldStay = new ImBoolean(true);

    @Override
    public boolean shouldRemove() {
        return !shouldStay.get();
    }

    @Override
    public void renderImGuiSettings() {
        if(ImGui.collapsingHeader("Vortex Force #"+this.hashCode(), shouldStay)){
            ImGui.text("Vortex Force Settings");
            float[] strength = new float[]{this.strength};
            ImGui.dragFloat("##Strength " + this.hashCode(), strength, 0.01f);
            this.strength = strength[0];
            ImFloat range = new ImFloat(this.getRange());
            ImGui.inputFloat("##Range #" + this.hashCode(), range);
            this.setRange(range.get());
            float[] pos = new float[]{(float) this.getVortexCenter().x, (float) this.getVortexCenter().y, (float) this.getVortexCenter().z};
            ImGui.text("Vortex Center:");
            ImGui.dragFloat3("##Pos: #" + this.hashCode(), pos);
            this.setVortexCenter(new Vec3(pos[0], pos[1], pos[2]));
            float[] axis = new float[]{(float) this.getVortexAxis().x, (float) this.getVortexAxis().y, (float) this.getVortexAxis().z};
            ImGui.text("Vortex Axis:");
            ImGui.dragFloat3("##Axis: #" + this.hashCode(), axis, 0.01f);
            this.setVortexAxis(new Vec3(axis[0], axis[1], axis[2]));
            if(ImGui.button("Set force center to emitter pos")) {
                this.setVortexCenter(QuasarClient.editorScreen.currentlySelectedEmitterInstance.getEmitterModule().getPosition());
            }
        }
    }

    @Override
    public VortexForce copy() {
        return new VortexForce(vortexAxis, vortexCenter, range, strength, falloff);
    }

    
}
