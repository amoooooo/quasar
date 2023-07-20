package coffee.amo.quasar;

import coffee.amo.quasar.client.QuasarParticleData;
import coffee.amo.quasar.entity.BlackHoleEntity;
import coffee.amo.quasar.entity.SlashEntity;
import coffee.amo.quasar.net.DNDNetworking;
import coffee.amo.quasar.net.packets.CubeParticlePacket;
import coffee.amo.quasar.emitters.ParticleContext;
import coffee.amo.quasar.emitters.ParticleEmitter;
import coffee.amo.quasar.emitters.ParticleSystemManager;
import coffee.amo.quasar.registry.AllParticleTypes;
import coffee.amo.quasar.registry.EntityRegistry;
import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Quasar.MODID)
public class Quasar {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "quasar";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    private static float randomFloat(Level level) {
        return (float) Math.random();
    }

    public Quasar() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        MinecraftForge.EVENT_BUS.register(this);
        AllParticleTypes.register(modEventBus);
        ParticleRegistry.PARTICLES.register(modEventBus);
        EntityRegistry.ENTITY_TYPES.register(modEventBus);
        DNDNetworking.init();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> QuasarClient.onCtorClient(modEventBus, forgeEventBus));
    }

    @SubscribeEvent
    public void tick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.START && event.side.isClient()) {
            ParticleSystemManager.getInstance().tick();
        }
    }

    public Vec3 getColor(Level level){
        if(Math.random() < 0.25) {
            return new Vec3(1,1,1);
        }
        return new Vec3(104 * (1-(Math.random() * 0.1)), 52 * (1-(Math.random() * 0.1)), 235 * (1-(Math.random() * 0.1)));
    }

    @SubscribeEvent
    public void itemUse(LivingEntityUseItemEvent.Start event) {
        if (event.getItem().getItem() == Items.GLOW_INK_SAC && !event.getEntity().level.isClientSide) {
            ParticleSystemManager.getInstance().clear();
        }
        if (event.getItem().getItem() == Items.GOLDEN_APPLE && event.getEntity().level.isClientSide) {
            ParticleSystemManager.getInstance().clear();
        }
        if(event.getItem().getItem() == Items.SPYGLASS){
            SlashEntity slash = new SlashEntity(event.getEntity().level);
            slash.setPos(0, -60, 0);
            // y rot should only be straight flat
            slash.setYRot(event.getEntity().getYRot());
            Vec3 look = event.getEntity().getLookAngle().scale(1.0f);
            slash.setDeltaMovement(5.0, 0, 0);
            event.getEntity().level.addFreshEntity(slash);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event){
        if(!event.getLevel().isClientSide()) {
            if(event.getPlacedBlock().is(Blocks.HOPPER)){
                ServerLevel level = (ServerLevel) event.getLevel();
                BlackHoleEntity blackHole = new BlackHoleEntity(level);
                blackHole.setPos(event.getPos().getX() + 0.5, event.getPos().getY() + 1, event.getPos().getZ() + 0.5);
                blackHole.setNoGravity(true);
                level.addFreshEntity(blackHole);
            }
        }
    }
    @SubscribeEvent
    public void onSnowballBreak(ProjectileImpactEvent event) {
        if (event.getProjectile() instanceof ThrowableItemProjectile thrown) {
            if(thrown.getItem().getItem() == Items.SNOWBALL){
                Level level = event.getProjectile().level;
//                BlackHoleEntity blackHole = new BlackHoleEntity(level);
//                blackHole.setPos(event.getProjectile().getX(), event.getProjectile().getY(), event.getProjectile().getZ());
//                blackHole.setNoGravity(true);
//                level.addFreshEntity(blackHole);
                if(!level.isClientSide) {
                    DNDNetworking.sendToAll(new CubeParticlePacket("void", event.getProjectile().getId()));
                }
            }
            if(thrown.getItem().getItem() == Items.EGG) {
                if(!event.getProjectile().level.isClientSide){
                    DNDNetworking.sendToAll(new CubeParticlePacket("vortex", event.getProjectile().getId()));
                }
//                event.getEntity().discard();
//                event.setCanceled(true);
            }

        }
    }

    @SubscribeEvent
    public void entityAttackEvent(LivingHurtEvent event){
        if(!event.getEntity().level.isClientSide) return;
        // check if the target is blocking and if the attacker has a sword in their hand
        if(event.getEntity().isBlocking()){
            if(event.getSource().getDirectEntity() != null){
                LivingEntity attacker = (LivingEntity) event.getSource().getDirectEntity();
                LivingEntity target = event.getEntity();
                if(attacker.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SwordItem) {
                    // get the direction of the attack
                    Vec3 direction = target.position().subtract(attacker.position()).normalize();
                }
            }
        }
    }

    @SubscribeEvent
    public void entityJoinLevelEvent(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof FireworkRocketEntity) {
            Level level = event.getEntity().level;
        }
        if(event.getEntity() instanceof Fireball) {
            if(event.getLevel().isClientSide()) {
//                ParticleRegistry.runFireballParticles(event.getEntity());
            }
        }
        if(event.getEntity() instanceof ArmorStand){
            if(event.getLevel().isClientSide()){
            }
        }
    }
}
