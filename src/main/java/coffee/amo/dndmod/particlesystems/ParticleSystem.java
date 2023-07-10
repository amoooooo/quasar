package coffee.amo.dndmod.particlesystems;

import coffee.amo.dndmod.client.CubeParticleData;
import coffee.amo.dndmod.net.DNDNetworking;
import coffee.amo.dndmod.net.packets.CubeParticlePacket;
import coffee.amo.dndmod.util.TriFunction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;


public class ParticleSystem {
    private int lifetime = 0;
    private int maxLifetime = 100;
    private boolean loop = false;
    private boolean active = false;
    private int emissionRate = 1;
    private int emissionCount = 1;
    Supplier<Vec3> emissionArea = () -> Vec3.ZERO;
    Shape emissionShape = Shape.POINT;
    Vec3 position;
    Vec3 rotation;
    float speed;
    boolean orbit = false;
    float orbitRadius = 0;
    Vec3 averageDirection = Vec3.ZERO;
    float averageDirectionSpread = 0;
    CubeParticleData particleType;
    private Level level;
    boolean isComplete = false;
    private Supplier<Vec3> livePosition;
    private Entity linkedEntity;

    public ParticleSystem(Level level, CubeParticleData particleType, Vec3 position, Vec3 rotation, float speed) {
        this.level = level;
        this.particleType = particleType;
        this.position = position;
        this.rotation = rotation;
        this.speed = speed;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public void setMaxLifetime(int maxLifetime) {
        this.maxLifetime = maxLifetime;
    }

    public void setPosition(Supplier<Vec3> position) {
        this.livePosition = position;
    }

    public Vec3 getPosition() {
        Vec3 pos = this.livePosition == null ? this.position : this.livePosition.get();
        return emissionShape.getPositionInShape(emissionArea.get(), pos, level);
    }

    public void setLinkedEntity(Entity entity) {
        this.linkedEntity = entity;
    }

    public Entity getLinkedEntity() {
        return this.linkedEntity;
    }

    public void run() {
        // apply spread
        Vec3 direction = this.averageDirection;
        if (averageDirectionSpread > 0) {
            direction = direction.add(
                    (Math.random() - 0.5) * averageDirectionSpread,
                    (Math.random() - 0.5) * averageDirectionSpread,
                    (Math.random() - 0.5) * averageDirectionSpread
            );
        }

        if (this.lifetime == 0) {
            this.active = true;
        }
        if (level.isClientSide) {
            level.addParticle(this.particleType, getPosition().x, getPosition().y, getPosition().z, this.speed * direction.x, this.speed * direction.y, this.speed * direction.z);
        } else {
            ServerLevel serverLevel = (ServerLevel) level;
            DNDNetworking.sendToAll(new CubeParticlePacket(
                    (float) getPosition().x,
                    (float) getPosition().y,
                    (float) getPosition().z,
                    particleType.getScale(),
                    this.speed,
                    particleType.getAvgAge(),
                    particleType.getGravity(),
                    particleType.getR(),
                    particleType.getG(),
                    particleType.getB(),
                    particleType.isHot(),
                    direction,
                    orbit,
                    orbitRadius,
                    rotation
            ));
        }
        if (this.livePosition != null) {
            this.position = this.livePosition.get();
        }
    }

    public void tick() {
        this.lifetime++;
        if (this.lifetime % this.emissionRate == 0) {
            for (int i = 0; i < this.emissionCount; i++) {
                this.run();
            }
        }
        if (this.lifetime >= this.maxLifetime) {
            if (this.loop) {
                this.lifetime = 0;
            } else {
                this.lifetime = this.maxLifetime;
                this.active = false;
                this.isComplete = true;
            }
        }
    }

    public void setOrbit(float radius) {
        this.orbit = true;
        this.orbitRadius = radius;
    }

    public void setEmissionArea(Supplier<Vec3> radius) {
        this.emissionArea = radius;
    }

    public void setDirection(Vec3 direction, float spread) {
        this.averageDirection = direction;
        this.averageDirectionSpread = spread;
    }

    public void setEmissionRate(int rate) {
        this.emissionRate = rate;
    }

    public void setEmissionCount(int count) {
        this.emissionCount = count;
    }

    public void setEmissionShape(Shape shape) {
        this.emissionShape = shape;
    }

    public enum Shape {
        CUBE((radius, pos, level) -> {
            return new Vec3(
                    pos.x + ((Math.random() - 0.5) * radius.x),
                    pos.y + ((Math.random() - 0.5) * radius.y),
                    pos.z + ((Math.random() - 0.5) * radius.z)
            );
        }),
        SPHERE((radius, pos, level) -> {
            double theta = Math.random() * 2 * Math.PI;
            double phi = Math.random() * Math.PI;
            double r = Math.random();
            return new Vec3(
                    pos.x + radius.x * r * Math.sin(phi) * Math.cos(theta),
                    pos.y + radius.y * r * Math.sin(phi) * Math.sin(theta),
                    pos.z + radius.z * r * Math.cos(phi)
            );
        }),
        CONE((radius, pos, level) -> {
            double theta = Math.random() * 2 * Math.PI;
            double phi = Math.random() * Math.PI;
            double r = Math.random();
            return new Vec3(
                    pos.x + radius.x * r * Math.sin(phi) * Math.cos(theta),
                    pos.y + radius.y * r * Math.sin(phi) * Math.sin(theta),
                    pos.z + radius.z * r * Math.cos(phi)
            );
        }),
        CYLINDER((radius, pos, level) -> {
            double theta = Math.random() * 2 * Math.PI;
            double r = Math.random();
            return new Vec3(
                    pos.x + radius.x * r * Math.cos(theta),
                    pos.y + radius.y * Math.random(),
                    pos.z + radius.z * r * Math.sin(theta)
            );
        }),
        TORUS((radius, pos, level) -> {
            double theta = Math.random() * 2 * Math.PI;
            double phi = Math.random() * 2 * Math.PI;
            double r = Math.random();
            return new Vec3(
                    pos.x + radius.x * r * Math.cos(theta),
                    pos.y + radius.y * Math.sin(phi),
                    pos.z + radius.z * r * Math.sin(theta)
            );
        }),
        DISC((radius, pos, level) -> {
            double theta = Math.random() * 2 * Math.PI;
            double r = Math.random();
            return new Vec3(
                    pos.x + radius.x * r * Math.cos(theta),
                    pos.y + radius.y * Math.random(),
                    pos.z + radius.z * r * Math.sin(theta)
            );
        }),
        LINE((radius, pos, level) -> {
            double theta = Math.random() * 2 * Math.PI;
            double r = Math.random();
            return new Vec3(
                    pos.x + radius.x * r * Math.cos(theta),
                    pos.y + radius.y * Math.random(),
                    pos.z + radius.z * r * Math.sin(theta)
            );
        }),
        POINT((radius, pos, level) -> pos);

        TriFunction<Vec3, Vec3, Level, Vec3> radiusConsumer;

        private Shape(TriFunction<Vec3, Vec3, Level, Vec3> consumer) {
            this.radiusConsumer = consumer ;
        }

        public Vec3 getPositionInShape(Vec3 radius, Vec3 pos, Level level) {
            return this.radiusConsumer.apply(radius, pos, level);
        }
    }
}
