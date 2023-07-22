package coffee.amo.quasar.emitters;

import coffee.amo.quasar.emitters.modules.particle.update.forces.AbstractParticleForce;
import coffee.amo.quasar.event.EmitterInstantiationEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.List;

public class ParticleSystemManager {
    private static ParticleSystemManager instance;
    private List<ParticleEmitter> particleEmitters = new ArrayList<>();
    private List<ParticleEmitter> particleSystemsToRemove = new ArrayList<>();

    public static ParticleSystemManager getInstance() {
        if (instance == null) {
            instance = new ParticleSystemManager();
        }
        return instance;
    }

    public void applyForceToParticles(Vec3 center, float radius, AbstractParticleForce... forces){
        for (ParticleEmitter particleEmitter : particleEmitters) {
                if(particleEmitter.emitterModule.getPosition().distanceTo(center) < radius){
                    particleEmitter.getParticleData().addForces(forces);
                }
        }
    }

    public void removeForcesFromParticles(Vec3 center, float radius, AbstractParticleForce... forces){
        for (ParticleEmitter particleEmitter : particleEmitters) {
            if(particleEmitter.emitterModule.getPosition().distanceTo(center) < radius){
                particleEmitter.getParticleData().removeForces(forces);
            }
        }
    }

    public void addParticleSystem(ParticleEmitter particleEmitter) {
        ParticleEmitter emitter = particleEmitter;
        EmitterInstantiationEvent event = new EmitterInstantiationEvent(emitter);
        MinecraftForge.EVENT_BUS.post(event);
        emitter = event.getEmitter();
        particleEmitters.add(emitter);
    }

    public void clear() {
        particleEmitters.clear();
    }

    public void tick() {
        particleEmitters.removeAll(particleSystemsToRemove);
        particleSystemsToRemove.clear();
        for (ParticleEmitter particleEmitter : particleEmitters) {
            particleEmitter.tick();
            if(particleEmitter.isComplete){
                particleSystemsToRemove.add(particleEmitter);
            }
        }
    }
}
