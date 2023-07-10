package coffee.amo.dndmod.particlesystems;

import java.util.ArrayList;
import java.util.List;

public class ParticleSystemManager {
    private static ParticleSystemManager instance;
    private List<ParticleSystem> particleSystems = new ArrayList<>();
    private List<ParticleSystem> particleSystemsToRemove = new ArrayList<>();

    public static ParticleSystemManager getInstance() {
        if (instance == null) {
            instance = new ParticleSystemManager();
        }
        return instance;
    }

    public void addParticleSystem(ParticleSystem particleSystem) {
        particleSystems.add(particleSystem);
    }

    public void clear() {
        particleSystems.clear();
    }

    public void tick() {
        particleSystemsToRemove.clear();
        for (ParticleSystem particleSystem : particleSystems) {
            particleSystem.tick();
            if(particleSystem.isComplete){
                particleSystemsToRemove.add(particleSystem);
            }
            if(particleSystem.getLinkedEntity() != null){
                if(particleSystem.getLinkedEntity().isRemoved()){
                    particleSystemsToRemove.add(particleSystem);
                }
            }
        }
        particleSystems.removeAll(particleSystemsToRemove);
    }
}
