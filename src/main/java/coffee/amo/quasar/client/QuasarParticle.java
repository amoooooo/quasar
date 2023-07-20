package coffee.amo.quasar.client;

import coffee.amo.quasar.emitters.modules.particle.init.InitModule;
import coffee.amo.quasar.emitters.modules.particle.render.RenderData;
import coffee.amo.quasar.emitters.modules.particle.render.TrailModule;
import coffee.amo.quasar.emitters.modules.particle.update.collsion.CollisionModule;
import coffee.amo.quasar.registry.AllSpecialTextures;
import coffee.amo.quasar.QuasarClient;
import coffee.amo.quasar.emitters.ParticleContext;
import coffee.amo.quasar.emitters.modules.particle.render.TrailSettings;
import coffee.amo.quasar.emitters.modules.particle.render.RenderModule;
import coffee.amo.quasar.emitters.modules.particle.update.UpdateModule;
import coffee.amo.quasar.emitters.modules.particle.update.forces.AbstractParticleForce;
import coffee.amo.quasar.util.MathUtil;
import cofh.core.util.helpers.vfx.Color;
import cofh.core.util.helpers.vfx.RenderTypes;
import cofh.core.util.helpers.vfx.VFXHelper;
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
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class QuasarParticle extends Particle {

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
            new Vec3(1, -1, 1), new Vec3(1, 1, 1), new Vec3(1, 1, -1), new Vec3(1, -1, -1)};

    private static final ParticleRenderType RENDER_TYPE_EMISSIVE = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder builder, TextureManager textureManager) {
            AllSpecialTextures.BLANK.bind();

            // transparent, additive blending
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            // overlay photoshop blend mode
            RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

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

    private static final ParticleRenderType RENDER_TYPE_FLAT = new QuasarParticleRenderType();

    protected float scale;
    public boolean shouldCollide = true;
    protected boolean emissive = true;
    protected Vec3 previousMotion = Vec3.ZERO;
    protected Vector4f[] previousPositions = new Vector4f[0];
    protected Vec3 previousPosition = Vec3.ZERO;
    protected Vec3 initialPosition = Vec3.ZERO;
    protected Vec3 position = Vec3.ZERO;
    protected double xRot = 0;
    protected double oxRot = 0;
    protected double yRot = 0;
    protected double oyRot = 0;
    protected double zRot = 0;
    protected double ozRot = 0;
    protected boolean faceVelocity = false;
    protected float velocityStretchFactor = 0.0f;

    protected List<TrailModule> trailModules = new ArrayList<>();
    List<Consumer<ParticleContext>> subEmitters = new ArrayList<>();
    List<AbstractParticleForce> forces = new ArrayList<>();
    List<InitModule> initModules = new ArrayList<>();
    List<RenderModule> renderModules = new ArrayList<>();
    List<UpdateModule> updateModules = new ArrayList<>();
    List<CollisionModule> collisionModules = new ArrayList<>();


    public QuasarParticle(QuasarParticleData data, ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z);
        this.xd = motionX;
        this.yd = motionY;
        this.zd = motionZ;
        this.initialPosition = new Vec3(x, y, z);
        this.faceVelocity = data.faceVelocity;
        this.velocityStretchFactor = data.velocityStretchFactor;
        this.setScale(0.2F);
        this.previousMotion = new Vec3(motionX, motionY, motionZ);
        this.initModules = data.initModules;
        this.renderModules = data.renderModules;
        this.updateModules = data.updateModules;
        this.collisionModules = data.collisionModules;
        this.forces = data.forces;
        this.subEmitters = data.subEmitters;
        this.trailModules = data.renderModules.stream().filter(m -> m instanceof TrailModule).map(m -> (TrailModule)m).collect(Collectors.toList());
        this.scale = data.particleSettings.getParticleSize();
        this.lifetime = data.particleSettings.getParticleLifetime();
        this.initModules.forEach(m -> m.run(this));
    }
    public QuasarParticle() {
        super(null, 0, 0, 0);
    }

    public void setScale(float scale) {
        this.scale = scale;
        this.setSize(scale * 0.5f, scale * 0.5f);
    }

    public float getScale() {
        return scale;
    }

    public double getXDelta() {
        return (float) xd;
    }

    public double getYDelta() {
        return (float) yd;
    }

    public double getZDelta() {
        return (float) zd;
    }

    public void setXDelta(double x) {
        this.xd = x;
    }

    public void setYDelta(double y) {
        this.yd = y;
    }

    public void setZDelta(double z) {
        this.zd = z;
    }

    public void setDeltaMovement(Vec3 delta) {
        this.xd = delta.x;
        this.yd = delta.y;
        this.zd = delta.z;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public Vec3 getDeltaMovement() {
        return new Vec3(xd, yd, zd);
    }

    public boolean isOnGround() {
        return onGround;
    }

    public boolean stoppedByCollision() {
        return stoppedByCollision;
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    @Override
    public void tick() {
        initModules.forEach(m -> m.run(this));
        hasPhysics = true;
        Vec3 motion = new Vec3(xd, yd, zd);
        position = new Vec3(x, y, z);
        if(!shouldCollide && !this.collisionModules.isEmpty()) {
            shouldCollide = true;
        }

        super.tick();

        updateModules.forEach(m -> m.run(this));
        forces.forEach(force -> force.applyForce(this));
        if ((stoppedByCollision || this.onGround)) {
            collisionModules.forEach(m -> m.run(this));
        }
        previousMotion = motion;
        previousPosition = position;

        // parent tick
        // end parent tick

        if (this.xd == 0) {
            this.stoppedByCollision = true;
        }
        if (this.zd == 0) {
            this.stoppedByCollision = true;
        }
    }

    @Override
    public void remove() {
        previousPositions = null;
        super.remove();
    }
    private static final double MAXIMUM_COLLISION_VELOCITY_SQUARED = Mth.square(100.0D);
    @Override
    public void move(double pX, double pY, double pZ) {
        if (!this.stoppedByCollision) {
            double d0 = pX;
            double d1 = pY;
            double d2 = pZ;
            if (this.shouldCollide && this.hasPhysics && (pX != 0.0D || pY != 0.0D || pZ != 0.0D) && pX * pX + pY * pY + pZ * pZ < MAXIMUM_COLLISION_VELOCITY_SQUARED) {
                Vec3 vec3 = Entity.collideBoundingBox((Entity)null, new Vec3(pX, pY, pZ), this.getBoundingBox(), this.level, List.of());
                pX = vec3.x;
                pY = vec3.y;
                pZ = vec3.z;
            }

            if (pX != 0.0D || pY != 0.0D || pZ != 0.0D) {
                this.setBoundingBox(this.getBoundingBox().move(pX, pY, pZ));
                this.setLocationFromBoundingbox();
            }

            if (Math.abs(d1) >= (double)1.0E-5F && Math.abs(pY) < (double)1.0E-5F) {
                this.stoppedByCollision = shouldCollide;
            }

            this.onGround = d1 != pY && d1 < 0.0D;
            if (d0 != pX) {
                this.xd = 0.0D;
            }

            if (d2 != pZ) {
                this.zd = 0.0D;
            }

        }
    }

    private static ResourceLocation STONE_TEXTURE = new ResourceLocation("minecraft", "textures/block/dirt.png");

    private RenderData renderData = null;
    private float pitch = 0;
    private float oPitch = 0;
    private float yaw = 0;
    private float oYaw = 0;
    @Override
    public void render(VertexConsumer builder, Camera renderInfo, float partialTicks) {
        if(renderData == null) {
            renderData = new RenderData(scale, pitch, yaw, roll, rCol, gCol, bCol, alpha);
        }
        renderModules.forEach(m -> m.apply(this, partialTicks, renderData));
        rCol = renderData.getR();
        gCol = renderData.getG();
        bCol = renderData.getB();
        alpha = renderData.getA();
        this.yaw = renderData.getYaw();
        this.pitch = renderData.getPitch();
        this.roll = renderData.getRoll();
        if (!renderInfo.isInitialized()) {
            return;
        }
        Vec3 projectedView = renderInfo.getPosition();
        double ageMultiplier = 1 - Math.pow(Mth.clamp(age + partialTicks, 0, lifetime), 3) / Math.pow(lifetime, 3);
        float lX = (float) (Mth.lerp(partialTicks, this.xo, this.x));
        float lY = (float) (Mth.lerp(partialTicks, this.yo, this.y));
        float lZ = (float) (Mth.lerp(partialTicks, this.zo, this.z));
        if (!renderData.getTrails().isEmpty()) {
            if (previousPositions.length == 0) {
                previousPositions = new Vector4f[100];
                for (int i = 0; i < previousPositions.length; i++) {
                    previousPositions[i] = new Vector4f(lX, lY, lZ, 1.0f);
                }
            } else {
                for (int i = previousPositions.length - 1; i > 0; i--) {
                    previousPositions[i] = previousPositions[i - 1];
                }
                previousPositions[0] = new Vector4f(lX, lY, lZ, 1.0f);
            }
            for(int t = 0; t < renderData.getTrails().size(); t++){
                TrailSettings trail = renderData.getTrails().get(t);
                if(trail.getTrailFrequency() == 0) continue;
                Vector4f[] trimmedPositions = new Vector4f[trail.getTrailLength()];
                System.arraycopy(previousPositions, 0, trimmedPositions, 0, trail.getTrailLength());
                Vector4f[] trailPoints = new Vector4f[trimmedPositions.length / trail.getTrailFrequency()];
                for (int i = 0; i < trailPoints.length; i++) {
                    trailPoints[i] = trail.getTrailPointModifier().apply(trimmedPositions[i * trail.getTrailFrequency()], i, new Vec3(xd, yd, zd));
                }
                QuasarClient.delayedRenders.add(ps -> {
                    if (previousPositions != null && previousPositions.length > 0) {
                        Color color = Color.fromFloat(rCol, gCol, bCol, alpha);
                        if(trail.getTrailColor() != null) {
                            color = Color.fromRGBA((int) (trail.getTrailColor().x() * 255), (int) (trail.getTrailColor().y() * 255), (int) (trail.getTrailColor().z() * 255), (int) (trail.getTrailColor().w() * 255));
                        }
                        VFXHelper.renderStreamLine(
                                ps,
                                Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderTypes.translucent(trail.getTrailTexture())),
                                emissive ? LightTexture.FULL_BRIGHT : getLightColor(partialTicks),
                                MathUtil.copyVector4fArray(trailPoints),
                                color,
                                (index) -> {
                                    return trail.getTrailWidthModifier().apply(index, (float) ageMultiplier);
                                }
                        );
                    }
                });
            }
        }
        if (!emissive) {
            RenderSystem.setShaderTexture(0, AllSpecialTextures.BLANK.getLocation());
            rCol = 0.5f;
            gCol = 0.5f;
            bCol = 0.5f;
            alpha = 1;
        }
        float lerpedX = (float) (Mth.lerp(partialTicks, this.xo, this.x) - projectedView.x());
        float lerpedY = (float) (Mth.lerp(partialTicks, this.yo, this.y) - projectedView.y());
        float lerpedZ = (float) (Mth.lerp(partialTicks, this.zo, this.z) - projectedView.z());

        int light = emissive ? LightTexture.FULL_BRIGHT : getLightColor(partialTicks);
        for (int i = 0; i < 6; i++) {
            if (onGround) {
                pitch = 0;
                yaw = 0;
            }
            Vec3[] faceVerts = new Vec3[]{
                    CUBE[i * 4],
                    CUBE[i * 4 + 1],
                    CUBE[i * 4 + 2],
                    CUBE[i * 4 + 3]
            };

            for (int j = 0; j < 4; j++) {
                Vec3 vec = faceVerts[j].scale(-1);
                if (vec.z < 0 && velocityStretchFactor != 0.0f) {
                    vec = new Vec3(vec.x, vec.y, vec.z * (1 + velocityStretchFactor));
                }
                vec = vec
                        .xRot(pitch)
                        .yRot(yaw)
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
    public ParticleRenderType getRenderType() {
        if (emissive) {
            return RENDER_TYPE_EMISSIVE;
        } else {
            return RENDER_TYPE_FLAT;
        }
    }

    public Vec3 getPos() {
        return new Vec3(x, y, z);
    }

    public void addForce(Vec3 force) {
        this.xd += force.x;
        this.yd += force.y;
        this.zd += force.z;
    }

    public void addForce(double x, double y, double z) {
        this.xd += x;
        this.yd += y;
        this.zd += z;
    }

    public void modifyForce(double modifier) {
        this.xd *= modifier;
        this.yd *= modifier;
        this.zd *= modifier;
    }

    public void modifyForce(Vec3 modifier) {
        this.xd *= modifier.x;
        this.yd *= modifier.y;
        this.zd *= modifier.z;
    }

    public int getAge() {
        return age;
    }

    public void setRotation(Vec3 rot) {
        this.xRot = rot.x;
        this.yRot = rot.y;
        this.zRot = rot.z;
    }

    public void setColor(Vector4f color) {
        this.rCol = color.x();
        this.gCol = color.y();
        this.bCol = color.z();
        this.alpha = color.w();
    }

    public static class Factory implements ParticleProvider<QuasarParticleData> {

        @Override
        public Particle createParticle(@NotNull QuasarParticleData data, ClientLevel world, double x, double y, double z, double motionX,
                                       double motionY, double motionZ) {
            QuasarParticle particle = new QuasarParticle(data, world, x, y, z, motionX, motionY, motionZ);
            particle.shouldCollide = data.shouldCollide;
            particle.faceVelocity = data.faceVelocity;
            particle.velocityStretchFactor = data.velocityStretchFactor;
            return particle;
        }
    }
}