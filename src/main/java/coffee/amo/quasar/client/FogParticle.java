package coffee.amo.quasar.client;

import cofh.core.client.particle.SpriteParticle;
import cofh.core.client.particle.options.ColorParticleOptions;
import cofh.core.util.helpers.vfx.Color;
import cofh.core.util.helpers.vfx.RenderTypes;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class FogParticle extends SpriteParticle {
    private final float rotSpeed;
    private FogParticle(ColorParticleOptions data, SpriteSet sprites, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
        super(data, level, sprites, x, y, z, dx, dy, dz);
        Color color = Color.fromRGBA(data.rgba0);
        this.rCol = color.r;
        this.gCol = color.g;
        this.bCol = color.b;
        this.lifetime = 40 + this.random.nextInt(20);
        this.xd = dx;
        this.yd = dy;
        this.zd = dz;
        this.gravity = this.random.nextFloat() * 0.02F + 0.01F;
        size = level.random.nextInt(3) * level.random.nextFloat() + 0.5f;
        this.roll = (float)Math.random() * ((float)Math.PI * 2F);
        this.rotSpeed = ((float)Math.random() - 0.5F) * 0.1F;
        this.alpha = 0.25F;
    }

    @Override
    public void render(VertexConsumer consumer, Camera cam, float partialTicks) {
        super.render(consumer, cam, partialTicks);
    }

    @Override
    public @NotNull AABB getBoundingBox() {
        return super.getBoundingBox();
    }

    public void tick() {
        this.roll += (float)Math.PI * rotSpeed * 2.0F;
        super.tick();
        this.alpha *= 0.975F;
        this.oRoll = this.roll;
    }

    public ParticleRenderType getRenderType() {
        return RenderTypes.PARTICLE_SHEET_OVER;
    }

    @Nonnull
    public static ParticleProvider<ColorParticleOptions> factory(SpriteSet spriteSet) {
        return (data, level, x, y, z, dx, dy, dz) -> {
            return new FogParticle(data, spriteSet, level, x, y, z, dx, dy, dz);
        };
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<ColorParticleOptions> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet sprite) {
            this.spriteSet = sprite;
        }

        @Nullable
        public Particle createParticle(ColorParticleOptions data, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            FogParticle particle = new FogParticle(data, spriteSet, level, x, y, z, dx, dy, dz);
            particle.sprite = spriteSet.get(level.random);
            particle.setSize(10.0f, 10.0f);
            particle.y += particle.size / 2.0f;
            return particle;
        }
    }
}
