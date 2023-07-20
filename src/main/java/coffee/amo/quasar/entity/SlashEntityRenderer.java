package coffee.amo.quasar.entity;

import coffee.amo.quasar.registry.RenderTypeRegistry;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Map;

public class SlashEntityRenderer extends EntityRenderer<SlashEntity> {
    private static final SlashModel MODEL = new SlashModel();
    // the cube should be 0.1 x 2.0 x 1.0 blocks
    // array of 4 sets of vec3s representing the 4 vertices of each face
    public static final Vec3[] RECTANGLE = {
            // TOP
            new Vec3(-0.05, 2.0, -0.5), new Vec3(0.05, 2.0, -0.5), new Vec3(0.05, 2.0, 0.5), new Vec3(-0.05, 1.0, 0.5),
            // BOTTOM
            new Vec3(-0.05, 0.0, -0.5), new Vec3(0.05, 0.0, -0.5), new Vec3(0.05, 0.0, 0.5), new Vec3(-0.05, 0.0, 0.5),
            // FRONT
            new Vec3(-0.05, 2.0, 0.5), new Vec3(0.05, 2.0, 0.5), new Vec3(0.05, 0.0, 0.5), new Vec3(-0.05, 0.0, 0.5),
            // BACK
            new Vec3(-0.05, 2.0, -0.5), new Vec3(0.05, 2.0, -0.5), new Vec3(0.05, 0.0, -0.5), new Vec3(-0.05, 0.0, -0.5),
            // LEFT
            new Vec3(-0.05, 2.0, -0.5), new Vec3(-0.05, 2.0, 0.5), new Vec3(-0.05, 0.0, 0.5), new Vec3(-0.05, 0.0, -0.5),
            // RIGHT
            new Vec3(0.05, 2.0, -0.5), new Vec3(0.05, 2.0, 0.5), new Vec3(0.05, 0.0, 0.5), new Vec3(0.05, 0.0, -0.5)
    };
    public SlashEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public ResourceLocation getTextureLocation(SlashEntity pEntity) {
        return new ResourceLocation("quasar:textures/special/image.png");
    }

    @Override
    public void render(SlashEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack ps, MultiBufferSource pBuffer, int pPackedLight) {
        ps.pushPose();
        RenderSystem.disableCull();
        VertexConsumer buffer = pBuffer.getBuffer(RenderTypeRegistry.slashFade(this.getTextureLocation(pEntity)));
        RenderSystem.setShader(() -> RenderTypeRegistry.RenderTypes.slashFade);
        ps.mulPose(Vector3f.YP.rotationDegrees(Mth.wrapDegrees(pEntityYaw) + 90));
        float scale = Math.min(2, (pEntity.tickCount + pPartialTick)/3f);
        int alpha = 255;
        // if tickCount > 15, fade out to 0
        ShaderInstance sh = RenderSystem.getShader();
        if(sh != null){
            Uniform dissolve = sh.getUniform("dissolve");
            if(dissolve != null){
                dissolve.set(1.0f);
            }
        }
        if (pEntity.tickCount > 8) {
            alpha = (int) (255 * (1 - (pEntity.tickCount - 8 + pPartialTick)/6f));
            ShaderInstance shader = RenderSystem.getShader();
            if(shader != null){
                Uniform dissolve = shader.getUniform("dissolve");
                if(dissolve != null){
                    dissolve.set(1-(pEntity.tickCount - 8 + pPartialTick)/6f);
                }
            }
        } else if (pEntity.tickCount < 3) {
            ShaderInstance shader = RenderSystem.getShader();
            if(shader != null){
                Uniform dissolve = shader.getUniform("dissolve");
                if(dissolve != null){
                    dissolve.set((pEntity.tickCount - 5 + pPartialTick)/5f);
                }
            }
        }

        ps.scale(scale, scale, scale);
        ps.translate(-1.0, -3.0, 0.0);
        buffer.vertex(ps.last().pose(), 0.0f, 0.0f, 0.0f).color(255, 255, 255, 255).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(pPackedLight).normal(0, 1, 0).endVertex();
        buffer.vertex(ps.last().pose(), 0.0f, 6.0f, 0.0f).color(255, 255, 255, 255).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(pPackedLight).normal(0, 1, 0).endVertex();
        buffer.vertex(ps.last().pose(), 3.0f, 6.0f, 0.0f).color(255, 255, 255, 255).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(pPackedLight).normal(0, 1, 0).endVertex();
        buffer.vertex(ps.last().pose(), 3.0f, 0.0f, 0.0f).color(255, 255, 255, 255).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(pPackedLight).normal(0, 1, 0).endVertex();
        RenderSystem.enableCull();
        ps.popPose();
    }


    static class SlashModel extends Model {
        private ModelPart cube;
        public SlashModel() {
            super(((renderyp) -> RenderType.entityCutout(new ResourceLocation("minecraft:textures/block/stone.png"))));
            List<ModelPart.Cube> cube = List.of(
                    new ModelPart.Cube(0,16, 7.9975f,0f,0f,0.005f,32f,16f,0f,0f,0f,false,32f,32f)
            );
            this.cube = new ModelPart(cube, Map.of());
        }

        @Override
        public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
            pPoseStack.pushPose();
            pPoseStack.translate(-0.5, 0.0, -0.5);
            cube.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
            pPoseStack.popPose();
        }
    }
}
