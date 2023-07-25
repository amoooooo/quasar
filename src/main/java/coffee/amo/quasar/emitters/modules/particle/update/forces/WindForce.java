package coffee.amo.quasar.emitters.modules.particle.update.forces;

import coffee.amo.quasar.client.particle.QuasarParticle;
import coffee.amo.quasar.emitters.modules.ModuleType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * A force that applies a wind force to a particle.
 * @see AbstractParticleForce
 * @see coffee.amo.quasar.emitters.modules.particle.update.UpdateModule
 *
 * <p>
 *     Wind forces are useful for simulating wind.
 *     The strength of the force is determined by the strength parameter.
 *     The falloff parameter is unused.
 *     The direction and speed of the wind is determined by the windDirection and windSpeed parameters.
 *     The windDirection parameter is a vector that determines the direction of the wind.
 *     The windSpeed parameter determines the speed of the wind.
 *     The windSpeed parameter is measured in blocks per tick.
 */
public class WindForce extends AbstractParticleForce {
    public static final Codec<WindForce> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Vec3.CODEC.fieldOf("wind_direction").forGetter(WindForce::getWindDirection),
                    Codec.FLOAT.fieldOf("wind_speed").forGetter(WindForce::getWindSpeed),
                    Codec.FLOAT.fieldOf("strength").forGetter(WindForce::getStrength),
                    Codec.FLOAT.fieldOf("falloff").forGetter(WindForce::getFalloff)
            ).apply(instance, WindForce::new)
    );
    Vec3 windDirection;
    public Vec3 getWindDirection() {
        return windDirection;
    }
    float windSpeed;
    public float getWindSpeed() {
        return windSpeed;
    }
    public WindForce(Vec3 windDirection, float windSpeed, float strength, float falloff) {
        this.windDirection = windDirection;
        this.windSpeed = windSpeed;
        this.strength = strength;
        this.falloff = falloff;
    }
    @Override
    public void applyForce(QuasarParticle particle) {
        particle.addForce(windDirection.scale(windSpeed));
    }

    @NotNull
    @Override
    public ModuleType<?> getType() {
        return ModuleType.WIND;
    }

    @Override
    public WindForce copy() {
        return new WindForce(windDirection, windSpeed, strength, falloff);
    }

    
}
