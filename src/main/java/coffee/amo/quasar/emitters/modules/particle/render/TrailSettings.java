package coffee.amo.quasar.emitters.modules.particle.render;

import coffee.amo.quasar.registry.AllSpecialTextures;
import coffee.amo.quasar.util.CodecUtil;
import coffee.amo.quasar.util.TriFunction;
import com.mojang.math.Vector4f;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;
import imgui.type.ImFloat;
import imgui.type.ImInt;
import imgui.type.ImString;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.BiFunction;

public class TrailSettings {
    public static final Codec<TrailSettings> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("trailFrequency").forGetter(settings -> settings.trailFrequency),
                    Codec.INT.fieldOf("trailLength").forGetter(settings -> settings.trailLength),
                    CodecUtil.VECTOR4F_CODEC.fieldOf("trailColor").xmap(
                            s -> s == null ? new Vector4f(0.0f, 0.0f, 0.0f, 1.0f) : s,
                            s -> s == null ? new Vector4f(0.0f, 0.0f, 0.0f, 1.0f) : s).forGetter(settings -> settings.trailColor),
                    Codec.FLOAT.fieldOf("trailWidthModifier").forGetter(settings -> 1f),
                    ResourceLocation.CODEC.fieldOf("trailTexture").forGetter(settings -> settings.trailTexture),
                    Codec.FLOAT.fieldOf("trailPointModifier").forGetter(settings -> 1f)
            ).apply(instance, TrailSettings::new)
    );
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

    private TrailSettings(int trailFrequency, int trailLength, Vector4f trailColor, float trailWidthModifier, ResourceLocation trailTexture, float trailPointModifier) {
        this.trailFrequency = trailFrequency;
        this.trailLength = trailLength;
        this.trailColor = trailColor;
        this.trailWidthModifier = (width, ageScale) -> (1 - width) * ageScale * trailWidthModifier;
        this.trailTexture = trailTexture;
        this.trailPointModifier = (point, index, velocity) -> point;
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

    public void renderImGuiSettings() {
        ImString trailTextureString = new ImString(trailTexture.toString());
        ImGui.inputText("Trail Texture" + this.hashCode(), trailTextureString);
        trailTexture = new ResourceLocation(trailTextureString.get());
        ImInt trailFrequencyInt = new ImInt(trailFrequency);
        ImGui.inputInt("Trail Frequency" + this.hashCode(), trailFrequencyInt);
        trailFrequency = trailFrequencyInt.get();
        ImInt trailLengthInt = new ImInt(trailLength);
        ImGui.inputInt("Trail Length" + this.hashCode(), trailLengthInt);
        trailLength = trailLengthInt.get();
        float[] trailColorVector4f = new float[]{trailColor.x(), trailColor.y(), trailColor.z(), trailColor.w()};
        ImGui.colorEdit4("Trail Color" + this.hashCode(), trailColorVector4f, ImGuiColorEditFlags.AlphaBar | ImGuiColorEditFlags.AlphaPreview);
        trailColor = new Vector4f(trailColorVector4f[0], trailColorVector4f[1], trailColorVector4f[2], trailColorVector4f[3]);

    }
}
