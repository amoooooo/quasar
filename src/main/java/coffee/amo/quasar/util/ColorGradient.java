package coffee.amo.quasar.util;

import cofh.lib.util.helpers.MathHelper;
import com.mojang.math.Vector4f;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ColorGradient {
    public static final Codec<ColorGradient> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    RGBPoint.CODEC.listOf().fieldOf("rgb_points").forGetter(ColorGradient::getPoints),
                    AlphaPoint.CODEC.listOf().fieldOf("alpha_points").forGetter(ColorGradient::getAlphaPoints)
            ).apply(instance, ColorGradient::new)
            );
    // a list of RGB points that define the gradient. The first point should have a percent of 0, and the last point should have a percent of 1.
    // allows for a gradient to be defined with a list of points, rather than a start and end color.
    // the gradient will be linearly interpolated between the points.
    // a secondary list of alpha points can be defined, which will be interpolated in the same way as the RGB points.
    // if no alpha points are defined, the alpha will be interpolated linearly between the first and last alpha points, which by default are 0 and 1.

    private RGBPoint[] points;
    private AlphaPoint[] alphaPoints;

    public ColorGradient(RGBPoint[] points, AlphaPoint[] alphaPoints) {
        this.points = points;
        this.alphaPoints = alphaPoints;
    }

    public ColorGradient(RGBPoint[] points) {
        this(points, new AlphaPoint[] {new AlphaPoint(0, 0), new AlphaPoint(1, 1)});
    }

    public ColorGradient(List<RGBPoint> points, List<AlphaPoint> alphaPoints) {
        this(points.toArray(new RGBPoint[0]), alphaPoints.toArray(new AlphaPoint[0]));
    }

    public ColorGradient(Vec3 startColor, Vec3 endColor, float startAlpha, float endAlpha) {
        this(new RGBPoint[] {new RGBPoint(0, startColor), new RGBPoint(1, endColor)}, new AlphaPoint[] {new AlphaPoint(0, startAlpha), new AlphaPoint(1, endAlpha)});
    }

    public ColorGradient(Vec3 startColor, Vec3 endColor) {
        this(new RGBPoint[] {new RGBPoint(0, startColor), new RGBPoint(1, endColor)});
    }

    public Vector4f getColor(float percentage){
        if (percentage < 0 || percentage > 1) {
            return new Vector4f(1, 0, 1, 1);
        }
        if (percentage == 0) {
            return MathUtil.vec4fFromVec3(points[0].getColor(), alphaPoints[0].getAlpha());
        }
        if (percentage == 1) {
            return MathUtil.vec4fFromVec3(points[points.length - 1].getColor(), alphaPoints[alphaPoints.length - 1].getAlpha());
        }
        int i = 0;
        while (points[i].getPercent() < percentage) {
            i++;
        }
        float percent = (percentage - points[i - 1].getPercent()) / (points[i].getPercent() - points[i - 1].getPercent());
        float alphaPercent = (percentage - alphaPoints[i - 1].getPercent()) / (alphaPoints[i].getPercent() - alphaPoints[i - 1].getPercent());
        return MathUtil.vec4fFromVec3(points[i - 1].getColor().lerp(points[i].getColor(), percent), alphaPoints[i - 1].getAlpha() + (alphaPoints[i].getAlpha() - alphaPoints[i - 1].getAlpha()) * alphaPercent);
    }

    public List<RGBPoint> getPoints() {
        return List.of(points);
    }

    public List<AlphaPoint> getAlphaPoints() {
        return List.of(alphaPoints);
    }

    static class RGBPoint {
        public static final Codec<RGBPoint> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.FLOAT.fieldOf("percent").forGetter(RGBPoint::getPercent),
                        Vec3.CODEC.fieldOf("color").forGetter(RGBPoint::getColor)
                ).apply(instance, RGBPoint::new)
                );
        float percent;
        Vec3 color;

        RGBPoint(float percent, Vec3 color) {
            this.percent = percent;
            this.color = color;
        }

        float getPercent() {
            return percent;
        }

        Vec3 getColor() {
            return color;
        }
    }

    static class AlphaPoint {
        public static final Codec<AlphaPoint> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.FLOAT.fieldOf("percent").forGetter(AlphaPoint::getPercent),
                        Codec.FLOAT.fieldOf("alpha").forGetter(AlphaPoint::getAlpha)
                ).apply(instance, AlphaPoint::new)
                );
        float percent;
        float alpha;

        AlphaPoint(float percent, float alpha) {
            this.percent = percent;
            this.alpha = alpha;
        }

        float getPercent() {
            return percent;
        }

        float getAlpha() {
            return alpha;
        }
    }
}
