package coffee.amo.quasar.editor;

import coffee.amo.quasar.client.particle.QuasarParticleData;
import coffee.amo.quasar.emitters.ParticleEmitter;
import coffee.amo.quasar.emitters.ParticleEmitterRegistry;
import coffee.amo.quasar.emitters.ParticleSystemManager;
import coffee.amo.quasar.emitters.modules.Module;
import coffee.amo.quasar.emitters.modules.emitter.EmitterModule;
import coffee.amo.quasar.emitters.modules.particle.update.forces.*;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImDouble;
import imgui.type.ImInt;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class ImGuiEditorOverlay {
    private ImGuiEditor editor = new ImGuiEditor();
    private ParticleEmitter currentlySelectedEmitterInstance = null;
    private String currentlySelectedEmitter = "None";
    private Vec3 position = new Vec3(0, 0, 0);
    private static final Path EMITTER_OUTPUT_PATH = Path.of("D:\\MC-Projects\\dndmod\\run\\quasar\\output\\emitters");

    public ImGuiEditorOverlay() {
        ImGui.createContext();
        editor.init();
        MinecraftForge.EVENT_BUS.register(editor);
    }

    public void save() throws IOException {
        // save the particle emitter and all its components to jsons
        if(currentlySelectedEmitterInstance == null) return;
        DataResult<JsonElement> result = ParticleEmitter.CODEC.encodeStart(JsonOps.INSTANCE, currentlySelectedEmitterInstance);
        if(result.error().isPresent()) {
            System.out.println(result.error().get());
            return;
        }
        JsonElement element = result.result().get();
        Files.createDirectories(EMITTER_OUTPUT_PATH);
        Path path = EMITTER_OUTPUT_PATH.resolve(currentlySelectedEmitterInstance.registryName.getPath() + ".json");
        Files.write(path, element.toString().getBytes());
    }

    public void renderEditor() {
        ImGui.render();
        editor.renderDrawData(ImGui.getDrawData());
        editor.newFrame();

        ImGui.showDemoWindow();
        ImGui.begin("Editor");
        ImGui.text("Emitter:");
        renderEmitterDropdown();
        if (!Objects.equals(currentlySelectedEmitter, "None")) {
            if (currentlySelectedEmitterInstance != null) {
                renderEmitterSettings();
            }
            renderEmitterSimulationSettings();
            renderForceSettings();
            renderRenderSettings();
        }
        if(ImGui.button("Save")){
            try {
                save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (ImGui.button("Reset")) {
            if (Minecraft.getInstance().level != null) {
                if (currentlySelectedEmitterInstance != null) {
                    ParticleSystemManager.getInstance().removeDelayedParticleSystem(currentlySelectedEmitterInstance);
                }
                ParticleEmitter emitter = ParticleEmitterRegistry.getEmitter(new ResourceLocation(currentlySelectedEmitter)).instance();
                emitter.setPosition(position);
                emitter.setLevel(Minecraft.getInstance().level);
                currentlySelectedEmitterInstance = emitter;
            }
        }
        if (ImGui.button("Reset Editor")) {
            if (Minecraft.getInstance().level != null) {
                if (currentlySelectedEmitterInstance != null) {
                    ParticleSystemManager.getInstance().removeDelayedParticleSystem(currentlySelectedEmitterInstance);
                }
                ParticleEmitter emitter = ParticleEmitterRegistry.getEmitter(new ResourceLocation(currentlySelectedEmitter)).instance();
                emitter.setPosition(position);
                emitter.setLevel(Minecraft.getInstance().level);
                currentlySelectedEmitterInstance = emitter;
            }
            currentlySelectedEmitter = "None";
        }
        ImGui.end();
    }

    private void renderEmitterSettings() {
        ImGui.text("Emitter Module Settings:");
        EmitterModule module = currentlySelectedEmitterInstance.getEmitterModule();
        ImInt lifetime = new ImInt(module.getMaxLifetime());
        ImGui.inputInt("Max Lifetime", lifetime);
        module.setMaxLifetime(lifetime.get());
        ImInt count = new ImInt(module.getCount());
        ImGui.inputInt("Count", count);
        module.setCount(count.get());
        ImInt rate = new ImInt(module.getRate());
        ImGui.inputInt("Rate", rate);
        module.setRate(rate.get());
        ImBoolean loop = new ImBoolean(module.getLoop());
        ImGui.checkbox("Loop", loop);
        module.setLoop(loop.get());
    }

    private void renderEmitterDropdown() {
        if (ImGui.beginCombo("##", currentlySelectedEmitter)) {
            ImGui.pushItemWidth(-1);
            for (int i = 0; i < ParticleEmitterRegistry.getEmitters().size(); i++) {
                boolean isSelected = currentlySelectedEmitter.equals(ParticleEmitterRegistry.getEmitters().get(i).toString());
                if (ImGui.selectable(ParticleEmitterRegistry.getEmitters().get(i).toString(), isSelected)) {
                    currentlySelectedEmitter = ParticleEmitterRegistry.getEmitters().get(i).toString();
                    if (currentlySelectedEmitterInstance != null) {
                        if (currentlySelectedEmitterInstance.registryName != null && !currentlySelectedEmitterInstance.registryName.toString().equals(currentlySelectedEmitter) && Minecraft.getInstance().level != null) {
                            ParticleEmitter emitter = ParticleEmitterRegistry.getEmitter(new ResourceLocation(currentlySelectedEmitter)).instance();
                            emitter.setPosition(position);
                            emitter.setLevel(Minecraft.getInstance().level);
                            currentlySelectedEmitterInstance = emitter;
                        }
                    } else if (Minecraft.getInstance().level != null) {
                        ParticleEmitter emitter = ParticleEmitterRegistry.getEmitter(new ResourceLocation(currentlySelectedEmitter)).instance();
                        emitter.setPosition(position);
                        emitter.setLevel(Minecraft.getInstance().level);
                        currentlySelectedEmitterInstance = emitter;
                    }
                }
                if (isSelected) {
                    ImGui.setItemDefaultFocus();
                }
            }
            ImGui.popItemWidth();
            ImGui.endCombo();
        }
    }

    private void renderForceSettings() {
        if (!Objects.equals(currentlySelectedEmitter, "None") && currentlySelectedEmitterInstance != null) {
            ImGui.begin("Particle Force Settings");
            currentlySelectedEmitterInstance.getParticleData().getForces().forEach(Module::renderImGuiSettings);
            if (ImGui.button("Add Force")) {
                ImGui.openPopup("Add Force Module");
            }
            if (ImGui.beginPopup("Add Force Module")) {
                ImGui.text("Add Force Module");
                ImGui.separator();
                if (ImGui.selectable("Point Attractor")) {
                    PointAttractorForce force = new PointAttractorForce(new Vec3(0, 0, 0), 1.0f, 1.0f, 1.0f, true, false);
                    currentlySelectedEmitterInstance.getParticleData().addForce(force);
                    ImGui.closeCurrentPopup();
                }
                if (ImGui.selectable("Point Force")) {
                    PointForce force = new PointForce(new Vec3(0, 0, 0), 1.0f, 1.0f, 1.0f);
                    currentlySelectedEmitterInstance.getParticleData().addForce(force);
                    ImGui.closeCurrentPopup();
                }
                if (ImGui.selectable("Vortex Force")) {
                    VortexForce force = new VortexForce(new Vec3(0, 0, 0), new Vec3(0, 0, 0), 1.0f, 1.0f, 1.0f);
                    currentlySelectedEmitterInstance.getParticleData().addForce(force);
                    ImGui.closeCurrentPopup();
                }
                if (ImGui.selectable("Gravity Force")) {
                    GravityForce force = new GravityForce(1.0f);
                    currentlySelectedEmitterInstance.getParticleData().addForce(force);
                    ImGui.closeCurrentPopup();
                }
                if(ImGui.selectable("Drag Force")){
                    DragForce force = new DragForce(1.0f);
                    currentlySelectedEmitterInstance.getParticleData().addForce(force);
                    ImGui.closeCurrentPopup();
                }
                ImGui.endPopup();
            }
            ImGui.end();
            currentlySelectedEmitterInstance.getParticleData().getForces().removeIf(AbstractParticleForce::shouldRemove);
        }
    }

    private void renderRenderSettings() {
        if (!Objects.equals(currentlySelectedEmitter, "None") && currentlySelectedEmitterInstance != null) {
            ImGui.begin("Particle Render Settings");
            QuasarParticleData data = currentlySelectedEmitterInstance.getParticleData();
            currentlySelectedEmitterInstance.getParticleData().getRenderModules().forEach(Module::renderImGuiSettings);
            ImGui.end();
        }
    }

    private void renderEmitterSimulationSettings() {
        // TODO: XYZ, "Simulate" button
        if (!Objects.equals(currentlySelectedEmitter, "None")) {
            ImDouble x = new ImDouble(position.x);
            ImDouble y = new ImDouble(position.y);
            ImDouble z = new ImDouble(position.z);
            ImGui.begin("Emitter Simulation Settings");
            ImGui.text("Position:");
            ImGui.inputDouble("X: ", x);
            ImGui.inputDouble("Y: ", y);
            ImGui.inputDouble("Z: ", z);
            position = new Vec3(x.get(), y.get(), z.get());
            if (ImGui.button("Set pos from ray")) {
                HitResult ray = Minecraft.getInstance().hitResult;
                if (ray != null) {
                    position = ray.getLocation();
                }
            }
            if (currentlySelectedEmitterInstance != null) {
                currentlySelectedEmitterInstance.setPosition(position);
            }
            if (ImGui.button("Simulate")) {
                if (Minecraft.getInstance().level != null) {
                    currentlySelectedEmitterInstance.getEmitterModule().reset();
                    currentlySelectedEmitterInstance.isComplete = false;
                    ParticleSystemManager.getInstance().addDelayedParticleSystem(currentlySelectedEmitterInstance);
                }
            }
            if (ImGui.button("Stop")) {
                ParticleSystemManager.getInstance().removeDelayedParticleSystem(currentlySelectedEmitterInstance);
            }
            ImGui.end();
        }
    }
}
