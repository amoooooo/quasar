package coffee.amo.dndmod;

import coffee.amo.dndmod.client.CubeParticle;
import coffee.amo.dndmod.mixin.client.ParticleEngineAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = Dndmod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class Dndmodclient {
    public static void onCtorClient(IEventBus modEventBus, IEventBus forgeEventBus) {
        modEventBus.addListener(AllParticleTypes::registerFactories);
    }

    public static void onParticleFactories(RegisterParticleProvidersEvent event) {
        event.register(ParticleRegistry.CUBE.get(), CubeParticle.Factory::new);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            AtomicInteger particleCount = new AtomicInteger();
            ((ParticleEngineAccessor)Minecraft.getInstance().particleEngine).getParticles().forEach((type, particles) -> {
                particles.forEach(particle -> {
                    if(particle instanceof CubeParticle) {
                        particleCount.getAndIncrement();
                    }
                });
                if(particleCount.get() > 1000) {
                    List<Particle> toRemove = particles.stream().filter(particle -> {
                        if(Minecraft.getInstance().level == null) {
                            return false;
                        }
                        if( Minecraft.getInstance().level.random.nextFloat() < 0.1f) {
                            return true;
                        } else {
                            return false;
                        }
                    }).toList();
                    particles.removeAll(toRemove);
                }
            });
        }
    }
}
