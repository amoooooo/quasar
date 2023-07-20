package coffee.amo.quasar.emitters.modules.particle.render;

import coffee.amo.quasar.registry.AllSpecialTextures;
import coffee.amo.quasar.util.TriFunction;
import com.mojang.math.Vector4f;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiFunction;

public class TrailSettings {
    protected int trailFrequency = 1;
    protected int trailLength = 20;
    protected Vector4f trailColor = new Vector4f(1, 1, 1, 1);
    protected BiFunction<Float, Float, Float> trailWidthModifier = (width, ageScale) -> 1f;
    protected TriFunction<Vector4f, Integer, Vec3, Vector4f> trailPointModifier = (point, index, velocity) -> point;
    protected ResourceLocation trailTexture = AllSpecialTextures.BLANK.getLocation();

    public TrailSettings(int trailFrequency, int trailLength, Vector4f trailColor, BiFunction<Float, Float, Float> trailWidthModifier, ResourceLocation trailTexture, TriFunction<Vector4f, Integer, Vec3, Vector4f> trailPointModifier) {
        this.trailFrequency = trailFrequency;
        this.trailLength = trailLength;
        this.trailColor = trailColor;
        this.trailWidthModifier = trailWidthModifier;
        this.trailTexture = trailTexture;
        this.trailPointModifier = trailPointModifier;
    }

    public void setTrailPointModifier(TriFunction<Vector4f, Integer, Vec3, Vector4f> trailPointModifier) {
        this.trailPointModifier = trailPointModifier;
    }

    public TriFunction<Vector4f, Integer, Vec3, Vector4f> getTrailPointModifier() {
        return trailPointModifier;
    }

    public void setTrailFrequency(int trailFrequency) {
        this.trailFrequency = trailFrequency;
    }

    public void setTrailLength(int trailLength) {
        this.trailLength = trailLength;
    }

    public void setTrailColor(Vector4f trailColor) {
        this.trailColor = trailColor;
    }

    public void setTrailWidthModifier(BiFunction<Float, Float, Float> trailWidthModifier) {
        this.trailWidthModifier = trailWidthModifier;
    }

    public void setTrailTexture(ResourceLocation trailTexture) {
        this.trailTexture = trailTexture;
    }

    public int getTrailFrequency() {
        return trailFrequency;
    }

    public int getTrailLength() {
        return trailLength;
    }

    public Vector4f getTrailColor() {
        return trailColor;
    }

    public BiFunction<Float, Float, Float> getTrailWidthModifier() {
        return trailWidthModifier;
    }

    public ResourceLocation getTrailTexture() {
        return trailTexture;
    }
}
