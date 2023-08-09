package coffee.amo.quasar.mixin.client;

import coffee.amo.quasar.util.ModelSetAccessor;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(EntityModelSet.class)
public class EntityModelSetMixin implements ModelSetAccessor {
    @Shadow
    private Map<ModelLayerLocation, LayerDefinition> roots;
    @Override
    public LayerDefinition getLayerDefinition(ModelLayerLocation location) {
        return roots.get(location);
    }
}
