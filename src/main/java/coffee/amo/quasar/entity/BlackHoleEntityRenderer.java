package coffee.amo.quasar.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class BlackHoleEntityRenderer extends EntityRenderer<BlackHoleEntity> {
    // the cube should be 0.1 x 2.0 x 1.0 blocks
    // array of 4 sets of vec3s representing the 4 vertices of each face
    public static final BlackHoleModel cubeModel = new BlackHoleModel(new Color(82, 36, 227, 255));
    public BlackHoleEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public ResourceLocation getTextureLocation(BlackHoleEntity pEntity) {
        return new ResourceLocation("quasar:textures/special/image.png");
    }

    public static final ResourceLocation BLANK = new ResourceLocation("quasar:textures/special/blank.png");
    @Override
    public void render(BlackHoleEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack ps, MultiBufferSource pBuffer, int pPackedLight) {
        float fac = ((Minecraft.getInstance().level.getGameTime() + pPartialTick) / 50f) * 4 * 0.75f;
        ps.pushPose();
        ps.translate(-0.25f, 0.0, -0.25);
        ps.translate(-0.5f, 0.5, -0.5f);
        ps.scale(1.5f, 1.5f, 1.5f);
        ps.translate(0, 0.5, 0);
        setupRotations(ps, fac, 1);
        GlStateManager._enableCull();
        GL11.glCullFace(GL11.GL_FRONT);
        ps.translate(0.5, 0.5, 0.5);
        ps.mulPose(Vector3f.YN.rotation(fac*2));
        ps.mulPose(Vector3f.ZN.rotation(fac*2));
        ps.translate(-0.5, -0.5, -0.5);
        cubeModel.renderToBuffer(ps, pBuffer.getBuffer(RenderType.entityTranslucent(BLANK)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0f, 1f, 0f, 1f);
        ps.translate(0.125, 0.125, 0.125);
        ps.scale(0.75f, 0.75f, 0.75f);
        cubeModel.renderToBuffer(ps, pBuffer.getBuffer(RenderType.entityTranslucent(BLANK)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0f, 0f, 1f, 1f);
        ps.translate(0.125f, 0.125f, 0.125f);
        ps.scale(0.75f, 0.75f, 0.75f);
        setupRotations(ps, fac, -2);
        ps.translate(0.5, 0.5, 0.5);
        ps.mulPose(Vector3f.YN.rotation(fac*4));
        ps.mulPose(Vector3f.ZN.rotation(fac*4));
        ps.translate(-0.5, -0.5, -0.5);
        cubeModel.renderToBuffer(ps, pBuffer.getBuffer(RenderType.entityTranslucent(BLANK)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1f, 1f, 0f, 1f);
//        ps.translate(0.125f, 0.125f, 0.125f);
//        ps.scale(0.65f, 0.65f, 0.65f);
//        ps.translate(0.5f, 0.5f, 0.5f);
//        ps.mulPose(Vector3f.YN.rotation(fac * 2));
//        ps.mulPose(Vector3f.ZN.rotation(fac * 2));
//
        GL11.glCullFace(GL11.GL_BACK);
        GlStateManager._disableCull();
        ps.popPose();
    }

    private void setupRotations(PoseStack ps, float fac, float mult) {
        ps.translate(0.5, 0.5f, 0.5);
        ps.mulPose(Vector3f.XP.rotationDegrees((35.26f + fac * 75)* mult));
        ps.mulPose(Vector3f.YP.rotationDegrees((45f + fac * 75)* mult));
        ps.mulPose(Vector3f.ZP.rotationDegrees((40f + fac * 75)* mult));
        ps.translate(-0.5, -0.5f, -0.5);
    }


    static class BlackHoleModel extends Model {
        private final ModelPart cube;
        private final Color color;

        public BlackHoleModel(Color color) {
            super(RenderType::entityTranslucent);
            this.color = color;
            List<ModelPart.Cube> cubes = List.of(
                    new ModelPart.Cube(0, 0, 0, 0, 0, 16, 16, 16, 0, 0, 0, false, 0, 0)
            );
            this.cube = new ModelPart(cubes, Map.of());
        }

        @Override
        public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
            matrices.pushPose();
            cube.render(matrices, vertices, light, overlay, red, green, blue, alpha);
            matrices.popPose();
        }
    }
}
