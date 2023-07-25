package coffee.amo.quasar.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public class QuasarParticleRenderType implements ParticleRenderType {
    private ResourceLocation texture;
    public QuasarParticleRenderType(){
    }

    public QuasarParticleRenderType setTexture(ResourceLocation texture){
        this.texture = texture;
        return this;
    }
    @Override
    public void begin(BufferBuilder builder, TextureManager textureManager) {
        if(texture != null) {
            RenderSystem.setShaderTexture(0, texture);
        }
        // transparent, additive blending
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        // overlay photoshop blend mode
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        // opaque
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
//			RenderSystem.enableLighting();

        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
    }

    @Override
    public void end(Tesselator tessellator) {
        tessellator.end();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }
}
