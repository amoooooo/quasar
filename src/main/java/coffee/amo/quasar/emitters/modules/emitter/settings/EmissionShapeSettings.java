package coffee.amo.quasar.emitters.modules.emitter.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class EmissionShapeSettings {
    public static final Codec<EmissionShapeSettings> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                Codec.STRING.fieldOf("shape").xmap(
                        EmissionShape::valueOf,
                        Enum::name).forGetter(EmissionShapeSettings::getShape),
                Vec3.CODEC.fieldOf("dimensions").forGetter(EmissionShapeSettings::getDimensions),
                Vec3.CODEC.fieldOf("rotation").forGetter(EmissionShapeSettings::getRotation),
                Codec.BOOL.fieldOf("from_surface").forGetter(EmissionShapeSettings::isFromSurface)
        ).apply(instance, EmissionShapeSettings::new);
    });
    public ResourceLocation registryName;
    Supplier<Vec3> dimensions;
    Supplier<Vec3> position;
    Supplier<Vec3> rotation;
    RandomSource randomSource;
    boolean fromSurface;
    EmissionShape shape;
    private EmissionShapeSettings(EmissionShape shape, Vec3 dimensions, Vec3 position, Vec3 rotation, RandomSource randomSource, boolean fromSurface) {
        this.dimensions = () -> dimensions;
        this.position = () -> position;
        this.randomSource = randomSource;
        this.fromSurface = fromSurface;
        this.rotation = () -> rotation;
        this.shape = shape;
    }

    private EmissionShapeSettings(EmissionShape shape, Vec3 dimensions, Vec3 rotation, boolean fromSurface) {
        this.dimensions = () -> dimensions;
        this.fromSurface = fromSurface;
        this.rotation = () -> rotation;
        this.shape = shape;
    }

    public EmissionShapeSettings(EmissionShape shape, Supplier<Vec3> dimensions, Supplier<Vec3> position, Supplier<Vec3> rotation, RandomSource randomSource, boolean fromSurface) {
        this.dimensions = dimensions;
        this.position = position;
        this.randomSource = randomSource;
        this.fromSurface = fromSurface;
        this.rotation = rotation;
        this.shape = shape;
    }

    public EmissionShapeSettings instance(){
        EmissionShapeSettings instance = new EmissionShapeSettings(shape, dimensions, position, rotation, randomSource, fromSurface);
        instance.registryName = registryName;
        return instance;
    }

    public ResourceLocation getRegistryId() {
        return registryName;
    }

    public Vec3 getPos(){
        return shape.getPos(this);
    }
    public EmissionShape getShape(){
        return shape;
    }

    public Vec3 getDimensions(){
        return dimensions.get();
    }

    public Vec3 getRotation(){
        return rotation.get();
    }

    public boolean isFromSurface(){
        return fromSurface;
    }

    public void setRandomSource(RandomSource randomSource) {
        this.randomSource = randomSource;
    }
    public void setPosition(Supplier<Vec3> position) {
        this.position = position;
    }
    public void setPosition(Vec3 position) {
        this.position = () -> position;
    }

    public static class Builder {
        private Supplier<Vec3> dimensions;
        private Supplier<Vec3> position;
        private Supplier<Vec3> rotation;
        private RandomSource randomSource;
        private boolean fromSurface;
        private EmissionShape shape;

        public Builder setDimensions(Supplier<Vec3> dimensions) {
            this.dimensions = dimensions;
            return this;
        }

        public Builder setPosition(Supplier<Vec3> position) {
            this.position = position;
            return this;
        }

        public Builder setRotation(Supplier<Vec3> rotation) {
            this.rotation = rotation;
            return this;
        }

        public Builder setRandomSource(RandomSource randomSource) {
            this.randomSource = randomSource;
            return this;
        }

        public Builder setFromSurface(boolean fromSurface) {
            this.fromSurface = fromSurface;
            return this;
        }

        public Builder setShape(EmissionShape shape) {
            this.shape = shape;
            return this;
        }

        public Builder setDimensions(Vec3 dimensions) {
            this.dimensions = () -> dimensions;
            return this;
        }

        public Builder setPosition(Vec3 position) {
            this.position = () -> position;
            return this;
        }

        public Builder setRotation(Vec3 rotation) {
            this.rotation = () -> rotation;
            return this;
        }

        public EmissionShapeSettings build() {
            return new EmissionShapeSettings(shape, dimensions, position, rotation, randomSource, fromSurface);
        }
    }
}
