package coffee.amo.quasar.emitters.anchors;

import coffee.amo.quasar.util.RenderUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Vector3f;
import org.joml.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class AnchorPoint {
    public static AnchorPoint TEST_POINT = new AnchorPoint(new ResourceLocation("quasar", "test_point"));
    private final ResourceLocation id;
    public Vector3f localOffset = new Vector3f(0,0,0);
    public Vector3f worldOffset = new Vector3f(0,0,0);
    public List<ModelPart> modelParts = null;
    public Vector3f origin = new Vector3f(0,0,0);
    public Vector4f transformMatrix = new Vector4f();

    public AnchorPoint(ResourceLocation id) {
        this.id = id;
    }

    public ResourceLocation getId() {
        return id;
    }

    public Vector3f getLocalOffset() {
        return localOffset;
    }

    public Vec3 getWorldOffset(Entity entity){
        if(entity == null) return Vec3.ZERO;
        Vec3 pos = entity.position();
        pos = pos.add(transformMatrix.x(), transformMatrix.y(), transformMatrix.z());
        return pos;
    }

    public void updatePosition(Entity entity){
        if(this.modelParts == null) return;
        PoseStack stack = new PoseStack();
        stack.scale(1f, -1f, -1f);
        stack.translate(0,-1.501F,0);
        for (ModelPart modelPart : modelParts) {
            modelPart.translateAndRotate(stack);
        }
        Vector4f offset = new Vector4f(localOffset.x(), localOffset.y(), localOffset.z(), 1);
//        stack.mulPose(Vector3f.YP.rotationDegrees(entity.getYRot()));
        offset = stack.last().pose().transform(offset);
        transformMatrix = offset;
    }

    public void render(PoseStack stack, MultiBufferSource source, float scalar){
        stack.pushPose();
        stack.translate(transformMatrix.x(), transformMatrix.y(), transformMatrix.z());
        VertexConsumer consumer = source.getBuffer(RenderType.lines());
        stack.scale(1/16f, 1/16f, 1/16f);
        LevelRenderer.renderLineBox(stack, consumer, new AABB(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5), 1, 1, 1, 1);
        stack.popPose();
    }
}
