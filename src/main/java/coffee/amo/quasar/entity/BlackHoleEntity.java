package coffee.amo.quasar.entity;

import coffee.amo.quasar.emitters.ParticleSystemManager;
import coffee.amo.quasar.emitters.modules.particle.update.forces.PointAttractorForce;
import coffee.amo.quasar.emitters.modules.particle.update.forces.VortexForce;
import coffee.amo.quasar.registry.EntityRegistry;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;
import java.util.function.Supplier;

/**
 * TEST ENTITY
 */
public class BlackHoleEntity extends LivingEntity {
    public PointAttractorForce attractorForce;
    public VortexForce vortexForce;
    public BlackHoleEntity(Level pLevel) {
        this(EntityRegistry.BLACK_HOLE_ENTITY.get(), pLevel);
    }

    public static Supplier<Float> getRandomSize(Level level) {
        return () -> level.random.nextFloat() * 0.1f;
    }

    public BlackHoleEntity(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount > 240) {
            if(this.level.isClientSide){
                ParticleSystemManager.getInstance().removeForcesFromParticles(position(), 15.0f, attractorForce, vortexForce);
            }
            this.discard();
        }
        if(level.isClientSide) {
            if(this.tickCount == 1){
                attractorForce = new PointAttractorForce(this.position().add(0.0,0.25,0.0), 15.0f, 0.1f, 1.0f, true, false);
                vortexForce = new VortexForce(new Vec3(0.0, -1.0, 0.0), this.position().add(0.0,0.25,0.0), 10.0f, 0.05f, 1.0f);
            }
            ParticleSystemManager.getInstance().applyForceToParticles(position(), 15.0f, attractorForce, vortexForce);
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
