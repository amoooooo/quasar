package coffee.amo.quasar.editor;

import coffee.amo.quasar.QuasarClient;
import coffee.amo.quasar.emitters.ParticleEmitter;
import coffee.amo.quasar.emitters.modules.emitter.settings.EmissionShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public class ImGuiGizmos {
    public static void renderGizmos(PoseStack stack, Camera camera, ImGuiEditorOverlay editorScreen, float partialTicks) {
        if(editorScreen != null) {
            stack.pushPose();
            Vec3 pos = camera.getPosition();
            stack.translate(-pos.x, -pos.y, -pos.z);
            VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.lines());
            if(editorScreen.currentlySelectedEmitterInstance != null){
                Vec3 position = editorScreen.currentlySelectedEmitterInstance.getEmitterModule().getPosition();
                stack.pushPose();
//                LevelRenderer.renderLineBox(stack, consumer, QuasarClient.xGizmo.aabb, 1f, 0, 0, 1);
//                LevelRenderer.renderLineBox(stack, consumer, QuasarClient.yGizmo.aabb, 0, 1f, 0, 1);
//                LevelRenderer.renderLineBox(stack, consumer, QuasarClient.zGizmo.aabb, 0, 0, 1f, 1);
                stack.popPose();
                stack.translate(position.x, position.y, position.z);
                renderAxisGizmos(stack, editorScreen.currentlySelectedEmitterInstance, partialTicks);
                renderEmitterShape(stack, editorScreen.currentlySelectedEmitterInstance);
            }
            stack.popPose();
        }
    }


    // this is ugly as fuck but i have no energy to fix it can stay for now
    public static void renderAxisGizmos(PoseStack stack, ParticleEmitter emitter, float partialTicks){
        stack.pushPose();
        stack.translate(0, 0, 0);
        AABB aabb = new AABB(-0.1f, 0,-0.1f, 0.1f, 5f * emitter.getEmitterSettingsModule().getEmissionShapeSettings().getDimensions().length(), 0.1f);
        VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.lines());
        Vec3 pos = emitter.getEmitterModule().getPosition();
        if(QuasarClient.editorScreen.localGizmos){
            stack.mulPose(Vector3f.YP.rotationDegrees((float) emitter.getEmitterSettingsModule().getEmissionShapeSettings().getRotation().y()));
            stack.mulPose(Vector3f.XP.rotationDegrees((float) emitter.getEmitterSettingsModule().getEmissionShapeSettings().getRotation().x()));
            stack.mulPose(Vector3f.ZP.rotationDegrees((float) emitter.getEmitterSettingsModule().getEmissionShapeSettings().getRotation().z()));
        }
        QuasarClient.yGizmo.aabb = new AABB(pos.x - 0.01f, pos.y, pos.z - 0.01f, pos.x + 0.01f, pos.y + 0.5f * emitter.getEmitterSettingsModule().getEmissionShapeSettings().getDimensions().length(), pos.z + 0.01f);
        QuasarClient.zGizmo.aabb = new AABB(pos.x - 0.01f, pos.y - 0.01f, pos.z, pos.x + 0.01f, pos.y + 0.01f, pos.z + 0.5f * emitter.getEmitterSettingsModule().getEmissionShapeSettings().getDimensions().length());
        QuasarClient.xGizmo.aabb = new AABB(pos.x, pos.y - 0.01f, pos.z - 0.01f, pos.x + 0.5f * emitter.getEmitterSettingsModule().getEmissionShapeSettings().getDimensions().length(), pos.y + 0.01f, pos.z + 0.01f);
        QuasarClient.xGizmo.position = pos;
        QuasarClient.yGizmo.position = pos;
        QuasarClient.zGizmo.position = pos;
        QuasarClient.xGizmo.rotation = new Vec3(-90, 0, -90);
        QuasarClient.yGizmo.rotation = new Vec3(0, 0, 0);
        QuasarClient.zGizmo.rotation = new Vec3(-90, 180, 0);
        QuasarClient.xGizmo.scale = new Vec3(0.1f, 0.1f, 0.1f);
        QuasarClient.yGizmo.scale = new Vec3(0.1f, 0.1f, 0.1f);
        QuasarClient.zGizmo.scale = new Vec3(0.1f, 0.1f, 0.1f);

        QuasarClient.xGizmo.render(stack, consumer, partialTicks, QuasarClient.mouseX, QuasarClient.mouseY);
        QuasarClient.yGizmo.render(stack, consumer, partialTicks, QuasarClient.mouseX, QuasarClient.mouseY);
        QuasarClient.zGizmo.render(stack, consumer, partialTicks, QuasarClient.mouseX, QuasarClient.mouseY);

        stack.popPose();
    }

    public static void renderEmitterShape(PoseStack stack, ParticleEmitter emitter) {
        stack.pushPose();
        stack.translate(0, 0, 0);
        EmissionShape shape = emitter.getEmitterSettingsModule().getEmissionShapeSettings().getShape();
        Vec3 dimensions = emitter.getEmitterSettingsModule().getEmissionShapeSettings().getDimensions();
        Vec3 rotation = emitter.getEmitterSettingsModule().getEmissionShapeSettings().getRotation();
        RenderSystem.disableCull();
        stack.mulPose(Vector3f.YP.rotationDegrees((float) rotation.y));
        stack.mulPose(Vector3f.XP.rotationDegrees((float) rotation.x));
        stack.mulPose(Vector3f.ZP.rotationDegrees((float) rotation.z));
        VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.lines());
        Matrix4f pose = stack.last().pose();
        // i really hate this ill refactor it one day
        switch (shape) {
            case DISC -> {
                float radius = (float) dimensions.x();
                float angle = 0;
                float angleStep = 360f / 32f;
                float x = (float) 0;
                float y = (float) 0;
                float z = (float) 0;
                for(int i = 0; i < 32; i++){
                    float x1 = (float) (x + Math.cos(Math.toRadians(angle)) * radius);
                    float z1 = (float) (z + Math.sin(Math.toRadians(angle)) * radius);
                    float x2 = (float) (x + Math.cos(Math.toRadians(angle + angleStep)) * radius);
                    float z2 = (float) (z + Math.sin(Math.toRadians(angle + angleStep)) * radius);
                    consumer.vertex(pose, x1, y, z1).color(0.15f, 0.15f, 1, 1).normal(0, 1, 0).endVertex();
                    angle += angleStep;
                }
            }
            case CUBE -> {
                float x = (float) dimensions.x();
                float y = (float) dimensions.y();
                float z = (float) dimensions.z();
                LevelRenderer.renderLineBox(stack, consumer, new AABB(-x, -y, -z, x, y, z), 0.15f, 0.15f, 1, 1);
            }
            case PLANE -> {
                float x = (float) dimensions.x();
                float y = 0;
                float z = (float) dimensions.z();
                LevelRenderer.renderLineBox(stack, consumer, new AABB(-x, -y, -z, x, y, z), 0.15f, 0.15f, 1, 1);
            }
            case TORUS -> {
                float radius = (float) dimensions.x();
                float tubeRadius = (float) dimensions.y();
                float angle = 0;
                float angleStep = 360f / 32f;
                float x = (float) 0;
                float y = (float) 0;
                float z = (float) 0;
                for(int i = 0; i < 32; i++){
                    float x1 = (float) (x + Math.cos(Math.toRadians(angle)) * radius);
                    float z1 = (float) (z + Math.sin(Math.toRadians(angle)) * radius);
                    float x2 = (float) (x + Math.cos(Math.toRadians(angle + angleStep)) * radius);
                    float z2 = (float) (z + Math.sin(Math.toRadians(angle + angleStep)) * radius);
                    consumer.vertex(pose, x1, y, z1).color(0.15f, 0.15f, 1, 1).normal(0, 1, 0).endVertex();
                    consumer.vertex(pose, x2, y, z2).color(0.15f, 0.15f, 1, 1).normal(0, 1, 0).endVertex();
                    angle += angleStep;
                }
            }
            case SPHERE -> {
                float radius = (float) dimensions.x();
                Matrix4f matrix4f = stack.last().pose();
                for(int i = 0; i < 32; i++){
                    for(int j = 0; j < 32; j++){
                        Vector3f v1 = parametricSphere((float) Math.toRadians(i * 11.25f), (float) Math.toRadians(j * 11.25f), radius);
                        Vector3f v2 = parametricSphere((float) Math.toRadians((i + 1) * 11.25f), (float) Math.toRadians(j * 11.25f), radius);
                        Vector3f v3 = parametricSphere((float) Math.toRadians(i * 11.25f), (float) Math.toRadians((j + 1) * 11.25f), radius);
                        Vector3f v4 = parametricSphere((float) Math.toRadians((i + 1) * 11.25f), (float) Math.toRadians((j + 1) * 11.25f), radius);
                        consumer.vertex(matrix4f, v1.x(), v1.y(), v1.z()).color(0.15f, 0.15f, 1, 1).normal(0, 1, 0).endVertex();
                        consumer.vertex(matrix4f, v2.x(), v2.y(), v2.z()).color(0.15f, 0.15f, 1, 1).normal(0, 1, 0).endVertex();
                        consumer.vertex(matrix4f, v3.x(), v3.y(), v3.z()).color(0.15f, 0.15f, 1, 1).normal(0, 1, 0).endVertex();
                        consumer.vertex(matrix4f, v4.x(), v4.y(), v4.z()).color(0.15f, 0.15f, 1, 1).normal(0, 1, 0).endVertex();
                    }
                }
            }
        }
        RenderSystem.enableCull();
        stack.popPose();
    }

    public static Vector3f parametricSphere(float u, float v, float r) {
        return new Vector3f(Mth.cos(u) * Mth.sin(v) * r, Mth.cos(v) * r, Mth.sin(u) * Mth.sin(v) * r);
    }
}
