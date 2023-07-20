package coffee.amo.quasar.emitters;

import coffee.amo.quasar.client.QuasarParticleData;
import coffee.amo.quasar.emitters.modules.emitter.EmitterModule;
import coffee.amo.quasar.emitters.modules.emitter.settings.EmitterSettingsModule;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

/*
 *  TODO:
 *   ✅ REQUIRED EmitterModule that controls lifetime, loop, rate, count, position
 *   ✅ OPTIONAL EmitterSettingsModule that controls randomization, emission area, emission shape, emission direction, emission speed, onEdge, linked entity, live position, etc
 *   OPTIONAL EmitterEntityModule that controls entity-specific settings
 *   OPTIONAL EmitterParticleModule that controls particle-specific settings
 *   OPTIONAL EmitterBlockEntityModule that controls block entity-specific settings
 *   OPTIONAL SubEmitterModule that controls sub-emitters
 *   OPTIONAL OnSpawnAction that can be set to run when a particle is spawned
 *   OPTIONAL OnDeathAction that can be set to run when a particle dies
 *   OPTIONAL OnUpdateAction that can be set to run when a particle is updated
 *   OPTIONAL OnRenderAction that can be set to run when a particle is rendered
 */
public class ParticleEmitter {
    public boolean isComplete = false;
    private boolean active = false;
    EmitterModule emitterModule;
    EmitterSettingsModule emitterSettingsModule;
    Function<Vec3, Vec3> colorFunction = (vec) -> vec;
    float maxYOffset = 0.0f;
    private Level level;
    private Entity linkedEntity;
    QuasarParticleData data;

    public ParticleEmitter(Level level, EmitterModule emitterModule, EmitterSettingsModule emitterSettingsModule) {
        this.level = level;
        this.emitterModule = emitterModule;
        this.emitterSettingsModule = emitterSettingsModule;
        this.data = new QuasarParticleData(emitterSettingsModule.getEmissionParticleSettings(), true, true);
    }

    public boolean isActive() {
        return active;
    }

    public QuasarParticleData getParticleData() {
        return data;
    }

    public void run() {
        // apply spread

        if (this.emitterModule.getCurrentLifetime() == 0) {
            this.active = true;
        }
        if (level.isClientSide) {
            Vec3 particlePos = emitterSettingsModule.getEmissionShapeSettings().getPos();
            Vec3 particleDirection = emitterSettingsModule.getEmissionParticleSettings().getInitialDirection().scale(emitterSettingsModule.getEmissionParticleSettings().getParticleSpeed());
            level.addParticle(data, particlePos.x(), particlePos.y(), particlePos.z(), particleDirection.x(), particleDirection.y(), particleDirection.z());
        }
    }

    public void tick() {
        emitterModule.tick(() -> {
            if(emitterModule.getCurrentLifetime() % emitterModule.getRate() == 0 || emitterModule.getCurrentLifetime() == 1){
                int i = 0;
                while (i < emitterModule.getCount()){
                    this.run();
                    i++;
                }
            }
        });
        if(emitterModule.isComplete()){
            this.isComplete = true;
        }
    }
}
