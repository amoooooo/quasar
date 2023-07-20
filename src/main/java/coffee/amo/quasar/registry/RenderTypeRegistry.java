package coffee.amo.quasar.registry;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;
import java.util.function.Function;

import static com.mojang.blaze3d.vertex.DefaultVertexFormat.NEW_ENTITY;

public class RenderTypeRegistry {

    public static RenderType slashFade(ResourceLocation texture) {
        return RenderTypes.SLASHFADE.apply(texture);
    }

    public static class RenderTypes extends RenderType {

        public RenderTypes(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
            super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
            throw new IllegalStateException("This class should not be instantiated");
        }

        public static ShaderInstance slashFade;

        private static final ShaderStateShard SLASH_FADE = new ShaderStateShard(() -> slashFade);

        public static Function<ResourceLocation, RenderType> SLASHFADE = Util.memoize(RenderTypes::slashFade);

        private static RenderType slashFade(ResourceLocation texture) {
            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder().setShaderState(SLASH_FADE).setTextureState(new RenderStateShard.TextureStateShard(texture, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setWriteMaskState(COLOR_WRITE).setOverlayState(OVERLAY).createCompositeState(true);
            return create("slash_fade", NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, rendertype$compositestate);
        }
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = "quasar", bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ShaderRegistry {
        @SubscribeEvent
        public static void shaderRegistry(RegisterShadersEvent event) throws IOException {
            event.registerShader(new ShaderInstance(event.getResourceManager(), new ResourceLocation("quasar", "slash_fade"), NEW_ENTITY), shaderInstance -> {
                RenderTypes.slashFade = shaderInstance;
            });
        }
    }
}
