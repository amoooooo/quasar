package coffee.amo.quasar;

import coffee.amo.quasar.entity.BlackHoleEntity;
import coffee.amo.quasar.entity.SlashEntity;
import coffee.amo.quasar.registry.EntityRegistry;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Quasar.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {
    @SubscribeEvent
    public static void attributeRegistry(EntityAttributeCreationEvent event) {
        event.put(EntityRegistry.SLASH_ENTITY.get(), SlashEntity.createAttributes().build());
        event.put(EntityRegistry.BLACK_HOLE_ENTITY.get(), BlackHoleEntity.createAttributes().build());
    }
}
