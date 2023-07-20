package coffee.amo.quasar.client;

import cofh.core.util.helpers.vfx.RenderTypes;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class CrackParticle extends TextureSheetParticle {
    boolean isFloor = false;
    private CrackParticle(ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
        super(level, x, y + (dy == 69420 ? -0.0001f : 0.01f), z, dx, dy, dz);
        isFloor = dy == 69420;
        this.lifetime = 40;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        quadSize = 1f;
        this.rCol = 1f;
        this.gCol = dy == 69420 ? 1.0f : 0f;
        this.bCol = dy == 69420 ? 1.0f : 0f;
    }

    @Override
    public @NotNull AABB getBoundingBox() {
        return AABB.ofSize(new Vec3(this.x, this.y, this.z), this.quadSize/2f, this.quadSize/2f, this.quadSize/2f);
    }

    public void tick() {
        super.tick();
    }


    @Override
    public void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
        Vec3 vec3 = pRenderInfo.getPosition();
        float f = (float)(Mth.lerp((double)pPartialTicks, this.xo, this.x) - vec3.x());
        float f1 = (float)(Mth.lerp((double)pPartialTicks, this.yo, this.y) - vec3.y());
        float f2 = (float)(Mth.lerp((double)pPartialTicks, this.zo, this.z) - vec3.z());
        Quaternion quaternion;
        if (this.roll == 0.0F) {
            quaternion = pRenderInfo.rotation();
        } else {
            quaternion = new Quaternion(pRenderInfo.rotation());
            float f3 = Mth.lerp(pPartialTicks, this.oRoll, this.roll);
            quaternion.mul(Vector3f.ZP.rotation(f3));
        }
        quaternion = new Quaternion(Quaternion.ONE);
        // flip the particle so it's facing up
        quaternion.mul(Vector3f.XP.rotationDegrees(-90));
        quaternion.mul(Vector3f.YP.rotationDegrees(180));
        quaternion.mul(Vector3f.ZP.rotationDegrees(90));
//        quaternion.mul(Vector3f.ZP.rotationDegrees(90));
        Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
        vector3f1.transform(quaternion);
        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float f4 = this.getQuadSize(pPartialTicks);

        for(int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.transform(quaternion);
            vector3f.mul(f4);
            vector3f.add(f, f1, f2);
        }

        float f7 = this.getU0();
        float f8 = this.getU1();
        float f5 = this.getV0();
        float f6 = this.getV1();
        int j = this.getLightColor(pPartialTicks);
        pBuffer.vertex((double)avector3f[0].x(), (double)avector3f[0].y(), (double)avector3f[0].z()).uv(f8, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        pBuffer.vertex((double)avector3f[1].x(), (double)avector3f[1].y(), (double)avector3f[1].z()).uv(f8, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        pBuffer.vertex((double)avector3f[2].x(), (double)avector3f[2].y(), (double)avector3f[2].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        pBuffer.vertex((double)avector3f[3].x(), (double)avector3f[3].y(), (double)avector3f[3].z()).uv(f7, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
    }

    public ParticleRenderType getRenderType() {
        if(isFloor) {
            return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
        } else {
            return RenderTypes.PARTICLE_SHEET_OVER;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet sprite) {
            this.spriteSet = sprite;
        }

        @Nullable
        public Particle createParticle(SimpleParticleType data, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            boolean isFloor = dy == 69420;
            CrackParticle particle = new CrackParticle(level, x, y, z, dx, dy, dz);
            particle.setSprite(this.spriteSet.get(level.random));
            if(isFloor) {
                particle.setSprite(((ParticleEngine.MutableSpriteSet)this.spriteSet).sprites.stream().filter(s -> s.getName().getPath().equals("particle/crack_under")).findFirst().get());
            } else {
                particle.setSprite(((ParticleEngine.MutableSpriteSet)this.spriteSet).sprites.stream().filter(s -> !s.getName().getPath().equals("particle/crack_under")).toList()
                        .get(level.random.nextInt(((ParticleEngine.MutableSpriteSet)this.spriteSet).sprites.stream().filter(s -> !s.getName().getPath().equals("particle/crack_under")).toList().size())));
            }
            particle.setSize(1f, 1f);
            return particle;
        }
    }
}
