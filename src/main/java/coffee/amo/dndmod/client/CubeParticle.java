package coffee.amo.dndmod.client;

import coffee.amo.dndmod.AllSpecialTextures;
import cofh.core.client.particle.CoFHParticle;
import cofh.core.client.particle.options.CoFHParticleOptions;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;

import com.mojang.math.Vector4f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CubeParticle extends CoFHParticle {

    public static final Vec3[] CUBE = {
            // TOP
            new Vec3(1, 1, -1), new Vec3(1, 1, 1), new Vec3(-1, 1, 1), new Vec3(-1, 1, -1),

            // BOTTOM
            new Vec3(-1, -1, -1), new Vec3(-1, -1, 1), new Vec3(1, -1, 1), new Vec3(1, -1, -1),

            // FRONT
            new Vec3(-1, -1, 1), new Vec3(-1, 1, 1), new Vec3(1, 1, 1), new Vec3(1, -1, 1),

            // BACK
            new Vec3(1, -1, -1), new Vec3(1, 1, -1), new Vec3(-1, 1, -1), new Vec3(-1, -1, -1),

            // LEFT
            new Vec3(-1, -1, -1), new Vec3(-1, 1, -1), new Vec3(-1, 1, 1), new Vec3(-1, -1, 1),

            // RIGHT
            new Vec3(1, -1, 1), new Vec3(1, 1, 1), new Vec3(1, 1, -1), new Vec3(1, -1, -1) };

    private static final ParticleRenderType RENDER_TYPE = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder builder, TextureManager textureManager) {
            AllSpecialTextures.BLANK.bind();

            // transparent, additive blending
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            // overlay photoshop blend mode
            RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE,
                    GlStateManager.DestFactor.ONE);

            // opaque
//			RenderSystem.depthMask(true);
//			RenderSystem.disableBlend();
//			RenderSystem.enableLighting();

            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(Tesselator tessellator) {
            tessellator.end();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        }
    };

    protected float scale;
    protected boolean hot;
    protected Vec3 previousMotion = Vec3.ZERO;
    protected Vec3 previousPosition = Vec3.ZERO;
    protected Vec3 initialPosition = Vec3.ZERO;

    protected boolean orbiting = false;
    protected double orbitRadius = 0;
    protected double xRot = 0;
    protected double yRot = 0;
    protected double zRot = 0;
    protected double orbit = 0;


    public CubeParticle(CoFHParticleOptions data, ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(data, world, x, y, z, motionX, motionY, motionZ);
        this.xd = motionX;
        this.yd = motionY;
        this.zd = motionZ;
        initialPosition = new Vec3(x, y, z);
        setScale(0.2F);
    }

    public CubeParticle(){
        super(null, null, 0, 0, 0, 0, 0, 0);
    }

    public void setScale(float scale) {
        this.scale = scale;
        this.setSize(scale * 0.5f, scale * 0.5f);
    }

    public void setOrbit(double radius, Vec3 angle) {
        this.orbiting = true;
        this.orbitRadius = radius;
        this.xRot = angle.x;
        this.zRot = angle.z;
        this.yRot = angle.y;
    }

    public void setOrbit(double radius) {
        setOrbit(radius, new Vec3(0, 0, 0));
    }

    public void averageAge(int age) {
        this.lifetime = (int) (age + (random.nextDouble() * 2D - 1D) * 8);
    }

    public void setHot(boolean hot) {
        this.hot = hot;
    }

    private boolean billowing = false;

    @Override
    public void tick() {
        Vec3 motion = new Vec3(xd, yd, zd);
        Vec3 position = new Vec3(x, y, z);
        hasPhysics = true;
        if (this.hot && this.age > 0) {
            if (this.yo == this.y) {
                billowing = true;
//                stoppedByCollision = true; // Prevent motion being ignored due to vertical collision
                if (this.xd == 0 && this.zd == 0) {
                    Vec3 diff = Vec3.atLowerCornerOf(new BlockPos(x, y, z)).add(0.5, 0.5, 0.5).subtract(x, y, z);
                    this.xd = -diff.x * 0.1;
                    this.zd = -diff.z * 0.1;
                }
                this.xd *= 1.1;
                this.yd *= 0.9;
                this.zd *= 1.1;
            } else if (billowing) {
                this.yd *= 1.2;
            }
        }
        // apply physics
        if (hasPhysics) {
            this.yd -= this.gravity;
            // if on ground and velocity is high enough, bounce
            // TODO: add shouldBounce() method
            float bounciness = 2F;
            if ((this.onGround || this.stoppedByCollision)&& (this.yd * this.yd > 0.05D || this.xd * this.xd > 0.05D
                    || this.zd * this.zd > 0.05D)) {
                this.yd = -this.yd * 0.3D * bounciness;
                this.xd *= 0.5D * bounciness;
                this.zd *= 0.5D * bounciness;
            }

            // if in water, apply water drag
            // TODO: add shouldApplyWaterDrag() method
            float drag = 0.76F;
            if (this.level.getFluidState(new BlockPos(this.x, this.y, this.z)).is(FluidTags.WATER) && hot) {
                this.yd = -0.1D;
                this.xd *= drag;
                this.zd *= drag;
            } else if (this.level.getFluidState(new BlockPos(this.x, this.y, this.z)).is(FluidTags.WATER) && !hot) {
                this.lifetime *= 0.9f;
            }
            // if Y motion is too low, stop adding to Y motion
            // TODO: add shouldStopFalling() method
            if (Math.abs(this.yd) < 0.003D) {
                this.yd = 0.0D;
            }

        }

//        if (orbiting) {
//            float orbitSpeed = 6f;
//            orbit += orbitSpeed;
//            // modify xd, yd, zd to orbit around the initial position using the xRot, yRot, zRot as multipliers. update xd, yd, zd to reflect the new delta movement
//            Vec3 rotationMult = new Vec3(xRot, yRot, zRot);
//            double newDeltaX = Math.cos(orbit) * orbitRadius * rotationMult.x;
//            double newDeltaY = Math.sin(orbit) * orbitRadius * rotationMult.y;
//            double newDeltaZ = Math.sin(orbit) * orbitRadius * rotationMult.z;
//            Vec3 newDelta = new Vec3(newDeltaX, newDeltaY, newDeltaZ);
//            Vec3 delta = newDelta.subtract(motion);
//            // add a little randomness to the orbit
//            delta = delta.add(random.nextDouble() * 0.1, random.nextDouble() * 0.1, random.nextDouble() * 0.1);
//            xd += delta.x;
//            yd += delta.y;
//            zd += delta.z;
//        }
        previousMotion = motion;
        previousPosition = position;
        super.tick();
    }

    @Override
    public void render(VertexConsumer builder, Camera renderInfo, float p_225606_3_) {
        Vec3 projectedView = renderInfo.getPosition();
        float lerpedX = (float) (Mth.lerp(p_225606_3_, this.xo, this.x) - projectedView.x());
        float lerpedY = (float) (Mth.lerp(p_225606_3_, this.yo, this.y) - projectedView.y());
        float lerpedZ = (float) (Mth.lerp(p_225606_3_, this.zo, this.z) - projectedView.z());

        // int light = getBrightnessForRender(p_225606_3_);
        int light = LightTexture.FULL_BRIGHT;
        double ageMultiplier = 1 - Math.pow(Mth.clamp(age + p_225606_3_, 0, lifetime), 3) / Math.pow(lifetime, 3);

        for (int i = 0; i < 6; i++) {
            // 6 faces to a cube
            for (int j = 0; j < 4; j++) {
                Vec3 vec = CUBE[i * 4 + j].scale(-1);
                vec = vec
                        /* .rotate(?) */
                        .scale(scale * ageMultiplier)
                        .add(lerpedX, lerpedY, lerpedZ);

                builder.vertex(vec.x, vec.y, vec.z)
                        .uv(j / 2, j % 2)
                        .color(rCol, gCol, bCol, alpha)
                        .uv2(light)
                        .endVertex();
            }
        }
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, VertexConsumer vertexConsumer, int i, float v, float v1) {

        render(vertexConsumer, Minecraft.getInstance().gameRenderer.getMainCamera(), v1);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return RENDER_TYPE;
    }

    public static class Factory implements ParticleProvider<CubeParticleType> {

        @Override
        public Particle createParticle(@NotNull CubeParticleData data, ClientLevel world, double x, double y, double z, double motionX,
                                       double motionY, double motionZ) {
            CubeParticle particle = new CubeParticle(data, world, x, y, z, motionX, motionY, motionZ);
            particle.setColor(data.r, data.g, data.b);
            particle.setScale(data.scale);
            particle.averageAge(data.avgAge);
            particle.setHot(data.hot);
            particle.gravity = data.gravity;
            if(data.orbiting){
                particle.setOrbit(data.orbitRadius, data.rotation);
            }
            return particle;
        }

        @Nullable
        @Override
        public Particle createParticle(CubeParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            createParticle(pType.getParticle(), pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
            return null;
        }
    }
}