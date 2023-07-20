package coffee.amo.quasar.registry;

import coffee.amo.quasar.entity.BlackHoleEntity;
import coffee.amo.quasar.entity.BlackHoleEntityRenderer;
import coffee.amo.quasar.entity.SlashEntity;
import coffee.amo.quasar.entity.SlashEntityRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityRegistry {
    public static DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, "quasar");

    public static RegistryObject<EntityType<SlashEntity>> SLASH_ENTITY = ENTITY_TYPES.register("slash_entity", () -> EntityType.Builder.<SlashEntity>of(SlashEntity::new, MobCategory.MISC).build("slash_entity"));
    public static RegistryObject<EntityType<BlackHoleEntity>> BLACK_HOLE_ENTITY = ENTITY_TYPES.register("black_hole_entity", () -> EntityType.Builder.<BlackHoleEntity>of(BlackHoleEntity::new, MobCategory.MISC).build("black_hole_entity"));

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class Client {
        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(SLASH_ENTITY.get(), SlashEntityRenderer::new);
            event.registerEntityRenderer(BLACK_HOLE_ENTITY.get(), BlackHoleEntityRenderer::new);
        }
    }
}
