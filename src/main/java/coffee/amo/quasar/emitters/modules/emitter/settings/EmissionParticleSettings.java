package coffee.amo.quasar.emitters.modules.emitter.settings;

import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class EmissionParticleSettings {
    RandomSource randomSource;
    float particleSpeed;
    float baseParticleSize;
    float particleSizeVariation;
    int particleLifetime;
    float particleLifetimeVariation;
    Supplier<Vec3> initialDirection;
    boolean randomInitialDirection;
    boolean randomInitialRotation;
    boolean randomSpeed;
    boolean randomSize;
    boolean randomLifetime;

    private EmissionParticleSettings(RandomSource randomSource, float particleSpeed, float baseParticleSize, float particleSizeVariation, int particleLifetime, float particleLifetimeVariation, Supplier<Vec3> initialDirection, boolean randomInitialDirection, boolean randomInitialRotation, boolean randomSpeed, boolean randomSize, boolean randomLifetime) {
        this.randomSource = randomSource;
        this.particleSpeed = particleSpeed;
        this.baseParticleSize = baseParticleSize;
        this.particleSizeVariation = particleSizeVariation;
        this.particleLifetime = particleLifetime;
        this.particleLifetimeVariation = particleLifetimeVariation;
        this.initialDirection = initialDirection;
        this.randomInitialDirection = randomInitialDirection;
        this.randomInitialRotation = randomInitialRotation;
        this.randomSpeed = randomSpeed;
        this.randomSize = randomSize;
        this.randomLifetime = randomLifetime;
    }

    public float getParticleSpeed() {
        return randomSpeed ? particleSpeed + randomSource.nextFloat() * particleSpeed : particleSpeed;
    }

    public float getParticleSize(){
        return randomSize ? baseParticleSize + randomSource.nextFloat() * particleSizeVariation : baseParticleSize;
    }

    public int getParticleLifetime(){
        return randomLifetime ? particleLifetime + (int)(randomSource.nextFloat() * particleLifetimeVariation) : particleLifetime;
    }

    public Vec3 getInitialDirection(){
        return randomInitialDirection ? initialDirection.get().multiply(randomSource.nextFloat() * 2 - 1, randomSource.nextFloat() * 2 - 1, randomSource.nextFloat() * 2 - 1) : initialDirection.get();
    }

    public static class Builder {
        private RandomSource randomSource;
        private float particleSpeed = 0;
        private float baseParticleSize = 0;
        private float particleSizeVariation = 0;
        private int particleLifetime = 0;
        private float particleLifetimeVariation = 0;
        private Supplier<Vec3> initialDirection;
        private boolean randomInitialDirection = false;
        private boolean randomInitialRotation = false;
        private boolean randomSpeed = false;
        private boolean randomSize = false;
        private boolean randomLifetime = false;

        public Builder setRandomSource(RandomSource randomSource) {
            this.randomSource = randomSource;
            return this;
        }

        public Builder setParticleSpeed(float particleSpeed) {
            this.particleSpeed = particleSpeed;
            return this;
        }

        public Builder setBaseParticleSize(float baseParticleSize) {
            this.baseParticleSize = baseParticleSize;
            return this;
        }

        public Builder setParticleSizeVariation(float particleSizeVariation) {
            this.particleSizeVariation = particleSizeVariation;
            return this;
        }

        public Builder setParticleLifetime(int particleLifetime) {
            this.particleLifetime = particleLifetime;
            return this;
        }

        public Builder setParticleLifetimeVariation(float particleLifetimeVariation) {
            this.particleLifetimeVariation = particleLifetimeVariation;
            return this;
        }

        public Builder setInitialDirection(Supplier<Vec3> initialDirection) {
            this.initialDirection = initialDirection;
            return this;
        }

        public Builder setRandomInitialDirection(boolean randomInitialDirection) {
            this.randomInitialDirection = randomInitialDirection;
            return this;
        }

        public Builder setRandomInitialRotation(boolean randomInitialRotation) {
            this.randomInitialRotation = randomInitialRotation;
            return this;
        }

        public Builder setRandomSpeed(boolean randomSpeed) {
            this.randomSpeed = randomSpeed;
            return this;
        }

        public Builder setRandomSize(boolean randomSize) {
            this.randomSize = randomSize;
            return this;
        }

        public Builder setRandomLifetime(boolean randomLifetime) {
            this.randomLifetime = randomLifetime;
            return this;
        }

        public EmissionParticleSettings build() {
            return new EmissionParticleSettings(randomSource, particleSpeed, baseParticleSize, particleSizeVariation, particleLifetime, particleLifetimeVariation, initialDirection, randomInitialDirection, randomInitialRotation, randomSpeed, randomSize, randomLifetime);
        }
    }
}
