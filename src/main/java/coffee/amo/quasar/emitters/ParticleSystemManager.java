package coffee.amo.quasar.emitters;

import coffee.amo.quasar.emitters.modules.particle.update.forces.AbstractParticleForce;
import coffee.amo.quasar.event.EmitterInstantiationEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class ParticleSystemManager {
    private static ParticleSystemManager instance;
    private Queue<ParticleEmitter> particleEmitters = new ConcurrentLinkedQueue<>();
    private Queue<ParticleEmitter> particleSystemsToRemove = new ConcurrentLinkedQueue<>();
    private Queue<ParticleEmitter> particleSystemsToAdd = new ConcurrentLinkedQueue<>();

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

    public void addDelayedParticleSystem(ParticleEmitter particleEmitter) {
        ParticleEmitter emitter = particleEmitter;
        EmitterInstantiationEvent event = new EmitterInstantiationEvent(emitter);
        MinecraftForge.EVENT_BUS.post(event);
        emitter = event.getEmitter();
        particleSystemsToAdd.add(emitter);
    }

    public void removeDelayedParticleSystem(ParticleEmitter particleEmitter) {
        particleSystemsToRemove.add(particleEmitter);
    }

    public void clear() {
        particleEmitters.clear();
    }

    public void tick() {
        particleEmitters.addAll(particleSystemsToAdd);
        particleSystemsToAdd.clear();
        particleEmitters.forEach(ParticleEmitter::tick);
        particleEmitters.removeIf(emitter -> emitter.isComplete);
        particleEmitters.removeAll(particleSystemsToRemove);
        particleSystemsToRemove.clear();
    }
}
