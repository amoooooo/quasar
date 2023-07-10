package coffee.amo.dndmod;

import coffee.amo.dndmod.client.CubeParticleData;
import coffee.amo.dndmod.net.DNDNetworking;
import coffee.amo.dndmod.particlesystems.ParticleSystem;
import coffee.amo.dndmod.particlesystems.ParticleSystemManager;
import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.awt.*;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Dndmod.MODID)
public class Dndmod {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "dndmod";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    private static float randomFloat(Level level) {
        return level.random.nextFloat();
    }

    public Dndmod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        MinecraftForge.EVENT_BUS.register(this);
        AllParticleTypes.register(modEventBus);
        DNDNetworking.init();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Dndmodclient.onCtorClient(modEventBus, forgeEventBus));
    }

    @SubscribeEvent
    public void serverTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            ParticleSystemManager.getInstance().tick();
        }
    }

    public Vec3 getColor(Level level){
        if(level.random.nextFloat() < 0.25) {
            return new Vec3(1,1,1);
        }
        return new Vec3(104 * (1-(level.random.nextFloat() * 0.1)), 52 * (1-(level.random.nextFloat() * 0.1)), 235 * (1-(level.random.nextFloat() * 0.1)));
    }

    @SubscribeEvent
    public void itemUse(LivingEntityUseItemEvent.Start event) {
        if (event.getItem().getItem() == Items.GLOW_INK_SAC && !event.getEntity().level.isClientSide) {
            ParticleSystemManager.getInstance().clear();
        }
        if (event.getItem().getItem() == Items.GOLDEN_APPLE && !event.getEntity().level.isClientSide) {
            ParticleSystemManager.getInstance().clear();
            if(!event.getEntity().level.isClientSide) {
                ServerLevel serverLevel = (ServerLevel) event.getEntity().level;
                Vec3 color = getColor(serverLevel);
                CubeParticleData data = new CubeParticleData((float) (getColor(serverLevel).x/255f), (float) (getColor(serverLevel).y/255f), (float) (getColor(serverLevel).z/255f), 0.05f, 12, false, (serverLevel.random.nextFloat() * -0.01f));
                // get a vec3 of the direction the player is looking
                Vec3 look = event.getEntity().getLookAngle();
                ParticleSystem dessication = new ParticleSystem(serverLevel, data, event.getEntity().position(), new Vec3(0, 0, 0), randomFloat(serverLevel) * 0.1f + 0.1f);
                // limit the direction to the xz plane
                look = new Vec3(look.x, 0.0, look.z).normalize();
                dessication.setDirection(new Vec3(serverLevel.random.nextFloat(), serverLevel.random.nextFloat(), serverLevel.random.nextFloat()), 0.8f);
                dessication.setPosition(() -> event.getEntity().position());
                dessication.setLoop(false);
                dessication.setEmissionCount(10);
                dessication.setEmissionRate(1);
                dessication.setMaxLifetime(1);
                dessication.setEmissionShape(ParticleSystem.Shape.CYLINDER);
                dessication.setEmissionArea(() -> new Vec3(0.5, event.getEntity().getBbHeight(), 0.5));

                ParticleSystemManager.getInstance().addParticleSystem(dessication);

                // set the cooldown for golden apple to 20
                ((Player)event.getEntity()).getCooldowns().addCooldown(Items.GOLDEN_APPLE, 20);
//                serverLevel.playSound(null, event.getEntity().blockPosition(), SoundEvents.SAND_BREAK, event.getEntity().getSoundSource(), 2.0f, 0.5f);
//                serverLevel.getEntities(null, event.getEntity().getBoundingBox().expandTowards(look.scale(6.0f))).forEach((entity) -> {
//                    if (entity.isAlive() && !entity.isSpectator() && entity.isPickable() && entity != event.getEntity()) {
//                        entity.hurt(DamageSource.DRY_OUT, 1.0f);
//                        MobEffectInstance darkness = new MobEffectInstance(MobEffects.DARKNESS, 200, 0, false, true);
//                        ((LivingEntity)entity).addEffect(darkness);
//                    }
//                });
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onSnowballBreak(ProjectileImpactEvent event) {
        if (event.getProjectile() instanceof Fireball) {
            Level level = event.getProjectile().level;
            if (!level.isClientSide) {
                ServerLevel serverLevel = (ServerLevel) level;
                CubeParticleData flameData = new CubeParticleData(0.75f, 0.35f, 0.15f, 0.25f, 12, false, 0.02f * (level.random.nextFloat() - 0.75f));
                CubeParticleData emberData = new CubeParticleData(0.75f, 0.55f, 0.25f, 0.125f, 12, false, -0.03f * level.random.nextFloat());
                CubeParticleData sparkData = new CubeParticleData(0.95f, 0.55f, 0.25f, 0.125f, 24, true, 0.05f);
                CubeParticleData smokeData = new CubeParticleData(0.25f, 0.25f, 0.25f, 0.2f, 8, false, -0.04f);
                Vec3 motion = event.getProjectile().getDeltaMovement().normalize();

                // flames
                ParticleSystem flames = new ParticleSystem(serverLevel, flameData, event.getProjectile().position(), new Vec3(0, 0, 0), 0.2f);
                Vec3 pushed = motion.scale(0.2f);
                flames.setDirection(pushed, 0.25f);
                flames.setLoop(false);
                flames.setEmissionCount(8);
                flames.setEmissionRate(1);
                flames.setMaxLifetime(40);
                flames.setEmissionShape(ParticleSystem.Shape.DISC);
                flames.setEmissionArea(() -> new Vec3(3.0, 0.5, 3.0));

                // embders
                ParticleSystem embers = new ParticleSystem(serverLevel, emberData, event.getProjectile().position(), new Vec3(0, 0, 0), 0.3f);
                embers.setDirection(pushed, 0.25f);
                embers.setLoop(false);
                embers.setEmissionCount(16);
                embers.setEmissionRate(1);
                embers.setMaxLifetime(40);
                embers.setEmissionShape(ParticleSystem.Shape.SPHERE);
                embers.setEmissionArea(() -> new Vec3(3.0, 1.0, 3.0));

                // sparks
                ParticleSystem sparks = new ParticleSystem(serverLevel, sparkData, event.getProjectile().position(), new Vec3(0, 0, 0), 0.6f);
                sparks.setDirection(motion.scale(-1), 2.0f);
                sparks.setLoop(false);
                sparks.setEmissionCount(32);
                sparks.setEmissionRate(1);
                sparks.setMaxLifetime(1);
                sparks.setEmissionShape(ParticleSystem.Shape.POINT);

                // smoke
                ParticleSystem smoke = new ParticleSystem(serverLevel, smokeData, event.getProjectile().position().add(0.0, 0.5, 0.0), new Vec3(0, 0, 0), 0.0f);
                smoke.setDirection(motion, 2.0f);
                smoke.setLoop(false);
                smoke.setEmissionCount(24);
                smoke.setEmissionRate(1);
                smoke.setMaxLifetime(40);
                smoke.setEmissionShape(ParticleSystem.Shape.CYLINDER);
                smoke.setEmissionArea(() -> new Vec3(3.0, 2.0, 3.0));

                ParticleSystemManager.getInstance().addParticleSystem(flames);
                ParticleSystemManager.getInstance().addParticleSystem(embers);
                ParticleSystemManager.getInstance().addParticleSystem(sparks);
                ParticleSystemManager.getInstance().addParticleSystem(smoke);
            }

        }
    }

    @SubscribeEvent
    public void entityJoinLevelEvent(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Fireball) {
            Level level = event.getEntity().level;
            if (!level.isClientSide) {
                ServerLevel serverLevel = (ServerLevel) level;
                CubeParticleData data = new CubeParticleData(0.75f, 0.35f, 0.15f, 0.05f, 12, false, 0.02f * (level.random.nextFloat() - 0.5f));
//                    serverLevel.sendParticles(data, event.getProjectile().position().x, event.getProjectile().position().y, event.getProjectile().position().z, 250, 0.75,0.015,0.75, 0.1);
                CubeParticleData data2 = new CubeParticleData(0.95f, 0.55f, 0.25f, 0.025f, 24, true, -0.01f);
//                    serverLevel.sendParticles(data2, event.getProjectile().position().x, event.getProjectile().position().y, event.getProjectile().position().z, 400, 0.75,0.3,0.75, 0.30);
//                    CubeParticleData data3 = new CubeParticleData(0.25f, 0.25f, 0.25f, 0.05f, 8, false, -0.01f);
//                    serverLevel.sendParticles(data3, event.getProjectile().position().x, event.getProjectile().position().y + 0.25f, event.getProjectile().position().z, 250, 0.75,0.5,0.75, 0.05);

                // make a vec3 pointing in the direction of the owner from the projectile
                Vec3 direction = new Vec3(0, 0, 0);
                // swap the x and z components with each other
                direction = new Vec3(1, direction.y, 1);
                ParticleSystem system = new ParticleSystem(serverLevel, data2, event.getEntity().position(), direction, 0.30f);
                system.setPosition(() -> event.getEntity().position());
                // get the direction of the projectile from the motion vector
                Vec3 motion = event.getEntity().getDeltaMovement().normalize();
                // get the opposite direction
                motion = motion.scale(-1);
                system.setDirection(motion, 0.5f);
                system.setLoop(true);
                system.setEmissionCount(15);
                system.setEmissionRate(1);
                system.setOrbit(0.5f);
                system.setEmissionShape(ParticleSystem.Shape.SPHERE);
                system.setLinkedEntity(event.getEntity());
                ParticleSystemManager.getInstance().addParticleSystem(system);
            }
        }
    }
}
