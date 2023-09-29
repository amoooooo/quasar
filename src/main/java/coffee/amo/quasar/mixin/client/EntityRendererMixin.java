package coffee.amo.quasar.mixin.client;

import coffee.amo.quasar.emitters.ParticleEmitter;
import coffee.amo.quasar.emitters.ParticleEmitterRegistry;
import coffee.amo.quasar.emitters.ParticleSystemManager;
import coffee.amo.quasar.emitters.modules.particle.update.forces.PointForce;
import coffee.amo.quasar.util.EntityExtension;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {

    @Inject(method = "render", at = @At("HEAD"))
    public void render(T pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, CallbackInfo ci) {
        if(pEntity.isOnFire()){
            if(((EntityExtension)pEntity).getEmitters().isEmpty()){
                ParticleEmitter emitter = ParticleEmitterRegistry.getEmitter(new ResourceLocation("quasar:basic_smoke")).instance();
                emitter.setPosition(pEntity.position());
                emitter.setLevel(pEntity.level());
                emitter.getEmitterSettingsModule().getEmissionShapeSettings().setPosition(pEntity::position);
                emitter.getEmitterSettingsModule().getEmissionShapeSettings().setDimensions(
                        new Vec3(
                                pEntity.getBbWidth(),
                                pEntity.getBbHeight(),
                                pEntity.getBbWidth()
                        )
                );
                emitter.getEmitterModule().setLoop(true);
                emitter.getEmitterModule().setMaxLifetime(5);
                emitter.getParticleData().getForces().forEach(force -> {
                    if(force instanceof PointForce pf) {
                        pf.setPoint(pEntity::position);
                    }
                });
                ((EntityExtension)pEntity).addEmitter(emitter);
                ParticleSystemManager.getInstance().addDelayedParticleSystem(emitter);
            } else {
                ((EntityExtension)pEntity).getEmitters().stream().filter(emitter -> emitter.registryName.toString().equals("quasar:basic_smoke")).forEach(emitter -> emitter.getEmitterModule().setMaxLifetime(5));
            }
        } else {
            ((EntityExtension)pEntity).getEmitters().stream().filter(emitter -> emitter.registryName.toString().equals("quasar:basic_smoke")).forEach(p -> p.getEmitterModule().setLoop(false));
            ((EntityExtension)pEntity).getEmitters().forEach(emitter -> {
                if(emitter.registryName.toString().equals("quasar:basic_smoke")){
                    ParticleSystemManager.getInstance().removeDelayedParticleSystem(emitter);
                }
            });
            ((EntityExtension)pEntity).getEmitters().removeIf(emitter -> emitter.registryName.toString().equals("quasar:basic_smoke"));
        }
        if(!((EntityExtension)pEntity).getEmitters().isEmpty()){
            ParticleEmitter emitter = ((EntityExtension)pEntity).getEmitters().get(0);
            if(emitter.registryName.toString().equals("quasar:basic_smoke")){
                pEntity.setCustomName(Component.literal("Particles: " + emitter.particleCount));
            }
        }
    }
}
