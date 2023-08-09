package coffee.amo.quasar.emitters.modules.emitter.settings.shapes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public class Cylinder extends AbstractEmitterShape {
    @Override
    public Vec3 getPoint(RandomSource randomSource, Vec3 dimensions, Vec3 rotation, Vec3 position, boolean fromSurface) {
        double theta = randomSource.nextDouble() * 2 * Math.PI;
        double x = Math.cos(theta);
        double y = Math.sin(theta);
        double z = randomSource.nextDouble() * 2 - 1;
        Vec3 normal = new Vec3(x, y, z).normalize();
        if(!fromSurface){
            normal = normal.scale(randomSource.nextDouble()).normalize();
            dimensions = dimensions.multiply(
                    randomSource.nextDouble(),
                    randomSource.nextDouble(),
                    randomSource.nextDouble()
            );
        }
        Vec3 pos = normal.multiply(dimensions);
        pos = pos.xRot((float) Math.toRadians(rotation.x())).yRot((float) Math.toRadians(rotation.y())).zRot((float) Math.toRadians(rotation.z()));
        return pos.add(position);
    }

    @Override
    public void renderShape(PoseStack stack, VertexConsumer consumer, Vec3 dimensions, Vec3 rotation) {

    }
}
