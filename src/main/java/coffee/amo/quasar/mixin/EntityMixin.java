package coffee.amo.quasar.mixin;

import coffee.amo.quasar.emitters.ParticleEmitter;
import coffee.amo.quasar.emitters.ParticleEmitterRegistry;
import coffee.amo.quasar.emitters.ParticleSystemManager;
import coffee.amo.quasar.util.EntityExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityExtension {
    @Shadow public abstract boolean isOnFire();

    @Unique
    List<ParticleEmitter> emitters = new ArrayList<>();

    @Override
    public List<ParticleEmitter> getEmitters() {
        return emitters;
    }

    @Override
    public void addEmitter(ParticleEmitter emitter) {
        emitters.add(emitter);
    }

    @Inject(method = "remove", at = @At("TAIL"))
    public void remove(CallbackInfo ci) {
        if(!((Entity) (Object) this).level.isClientSide) return;
        emitters.forEach(ParticleSystemManager.getInstance()::removeDelayedParticleSystem);
        emitters.clear();
    }
}
