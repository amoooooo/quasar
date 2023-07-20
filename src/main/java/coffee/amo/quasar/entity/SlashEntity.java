package coffee.amo.quasar.entity;

import coffee.amo.quasar.ParticleRegistry;
import coffee.amo.quasar.net.DNDNetworking;
import coffee.amo.quasar.net.packets.CubeParticlePacket;
import coffee.amo.quasar.registry.EntityRegistry;
import cofh.core.client.particle.options.ColorParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import team.lodestar.lodestone.network.screenshake.PositionedScreenshakePacket;
import team.lodestar.lodestone.registry.common.LodestonePacketRegistry;
import team.lodestar.lodestone.systems.easing.Easing;

import java.util.List;
import java.util.function.Supplier;
/**
 * TEST ENTITY
 */
public class SlashEntity extends LivingEntity {
    public SlashEntity(Level pLevel) {
        this(EntityRegistry.SLASH_ENTITY.get(), pLevel);
    }

    public static Supplier<Float> getRandomSize(Level level) {
        return () -> level.random.nextFloat() * 0.1f;
    }

    public SlashEntity(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void tick() {
        setDiscardFriction(true);
        super.tick();
        if (this.tickCount == 2) {
            if (!level.isClientSide) {
                DNDNetworking.sendToAll(new CubeParticlePacket("slash", this.getId()));
                LodestonePacketRegistry.LODESTONE_CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(blockPosition())), new PositionedScreenshakePacket(10,
                        position(), 64f, 64f, Easing.EXPO_OUT).setIntensity(1f, 0));
            }
        }
        // face the direction of travel
        Vec3 motion = getDeltaMovement();
        float yaw = (float) Math.toDegrees(Math.atan2(motion.x, motion.z));
        this.setYRot(yaw);
        if (this.level.isClientSide && this.tickCount < 15) {
            ClientLevel world = (ClientLevel) this.level;
            ColorParticleOptions options = new ColorParticleOptions(ParticleRegistry.FOG.get(), 2.0f, 40.0f, 0.0f, 0xFF555555);
            for (int i = 0; i < level.random.nextInt(4) + 9 * this.getDeltaMovement().x; i++) {
                world.addParticle(options, this.getX() + (3 * (level.random.nextFloat() - 0.5)), this.getY() + 1, this.getZ() + 1.25 * (level.random.nextFloat() - 0.5),
                        (level.random.nextFloat() - 0.5) * 0.1, (level.random.nextFloat() + 0.5) * 0.01, (level.random.nextFloat() - 0.5) * 0.1);
            }
            world.addParticle(ParticleRegistry.CRACK.get(), this.getX(), this.getY() + 0.01, this.getZ(),
                    0, 0, 0);
            world.addParticle(ParticleRegistry.CRACK.get(), this.getX(), this.getY() + 0.001, this.getZ(),
                    0, 69420, 0);
            world.addParticle(ParticleRegistry.CRACK.get(), this.getX() + 1, this.getY() + 0.01, this.getZ(),
                    0, 0, 0);
            world.addParticle(ParticleRegistry.CRACK.get(), this.getX() + 1, this.getY() + 0.001, this.getZ(),
                    0, 69420, 0);
            world.addParticle(ParticleRegistry.CRACK.get(), this.getX() + this.getDeltaMovement().length() - 0.25, this.getY() + 0.001, this.getZ(),
                    0, 69420, 0);
            world.addParticle(ParticleRegistry.CRACK.get(), this.getX() + this.getDeltaMovement().length() - 0.5, this.getY() + 0.01, this.getZ(),
                    0, 0, 0);
            world.addParticle(ParticleRegistry.CRACK.get(), this.getX() + this.getDeltaMovement().length() - 0.5, this.getY() + 0.001, this.getZ(),
                    0, 69420, 0);
        }
        // slow down delta movement exponentially the longer it's been alive
        this.setDeltaMovement(this.getDeltaMovement().scale(0.9 - 0.02 * this.tickCount));
        this.setYRot((float) Math.toDegrees(Math.atan2(1.0, 0.0)));
        if (this.tickCount >= 10) {
            this.setDeltaMovement(0.0, 0.0, 0.0);
            if (!level.isClientSide && this.tickCount == 10) {
                DNDNetworking.sendToAll(new CubeParticlePacket("slash_end", this.getId()));

            } else if (!level.isClientSide && this.tickCount == 20) {
                LodestonePacketRegistry.LODESTONE_CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(blockPosition())), new PositionedScreenshakePacket(10,
                        position(), 64f, 64f, Easing.EXPO_OUT).setIntensity(2f, 0));
            }
        }
        if (this.tickCount > 20) {
            this.discard();
        }
    }

    @Override
    public HumanoidArm getMainArm() {
        return null;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return List.of();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes().add(Attributes.FOLLOW_RANGE, 16.0D).add(Attributes.ATTACK_KNOCKBACK).add(Attributes.MAX_HEALTH, 10.0D);
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot pSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot pSlot, ItemStack pStack) {

    }

    @Override
    protected AABB makeBoundingBox() {
        return new AABB(getX() - 0.05, getY(), getZ() - 0.5, getX() + 0.05, getY() + 2.0, getZ() + 0.5);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
