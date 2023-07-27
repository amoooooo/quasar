package coffee.amo.quasar;

import coffee.amo.quasar.client.particle.QuasarParticleDataListener;
import coffee.amo.quasar.command.QuasarParticleCommand;
import coffee.amo.quasar.editor.ImGuiEditorOverlay;
import coffee.amo.quasar.editor.ImGuiEditorScreen;
import coffee.amo.quasar.emitters.ParticleEmitterJsonListener;
import coffee.amo.quasar.emitters.ParticleEmitterRegistry;
import coffee.amo.quasar.emitters.modules.emitter.settings.EmitterSettingsJsonListener;
import coffee.amo.quasar.emitters.modules.emitter.settings.ParticleSettingsJsonListener;
import coffee.amo.quasar.emitters.modules.emitter.settings.ShapeSettingsJsonListener;
import coffee.amo.quasar.emitters.modules.particle.init.InitModuleJsonListener;
import coffee.amo.quasar.emitters.modules.particle.render.RenderModuleJsonListener;
import coffee.amo.quasar.emitters.modules.particle.update.UpdateModuleJsonListener;
import coffee.amo.quasar.registry.AllParticleTypes;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = Quasar.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class QuasarClient {
    public static ImGuiEditorOverlay editorScreen = null;
    public static final KeyMapping EDITOR_KEY = new KeyMapping("key.quasar.editor", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, InputConstants.KEY_GRAVE, "key.categories.misc");
    public static final List<Consumer<PoseStack>> delayedRenders = new ArrayList<>();
    @SubscribeEvent
    public static void renderTranslucent(RenderLevelStageEvent event){
        if(event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            PoseStack stack = event.getPoseStack();
            stack.pushPose();
            Vec3 pos = event.getCamera().getPosition();
            stack.translate(-pos.x, -pos.y, -pos.z);
            delayedRenders.forEach(consumer -> consumer.accept(stack));
            stack.popPose();
        }
        if(event.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            delayedRenders.clear();
        }
    }

    public static void onCtorClient(IEventBus modEventBus, IEventBus forgeEventBus) {
        modEventBus.addListener(AllParticleTypes::registerFactories);
        modEventBus.addListener(QuasarClient::clientReloadListeners);
        modEventBus.addListener(QuasarClient::registerKeys);
        ParticleEmitterRegistry.bootstrap();
    }

    public static void clientReloadListeners(RegisterClientReloadListenersEvent event) {
        InitModuleJsonListener.register(event);
        UpdateModuleJsonListener.register(event);
        RenderModuleJsonListener.register(event);
        QuasarParticleDataListener.register(event);
        ParticleSettingsJsonListener.register(event);
        ShapeSettingsJsonListener.register(event);
        EmitterSettingsJsonListener.register(event);
        ParticleEmitterJsonListener.register(event);
    }

    public static void registerKeys(RegisterKeyMappingsEvent event){
        event.register(EDITOR_KEY);
    }

    @SubscribeEvent
    public static void registerClientCommands(RegisterClientCommandsEvent event){
        event.getDispatcher().register(QuasarParticleCommand.CMD.register());
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.RenderTickEvent event) {
        if(editorScreen == null) {
            editorScreen = new ImGuiEditorOverlay();
        }

        if(event.phase == TickEvent.Phase.END){
            editorScreen.renderEditor();
        }
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event){
        if(EDITOR_KEY.consumeClick()){
            Minecraft.getInstance().setScreen(new ImGuiEditorScreen());
        }
    }
}
