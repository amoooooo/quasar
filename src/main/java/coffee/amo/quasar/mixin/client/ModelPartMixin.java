package coffee.amo.quasar.mixin.client;

import coffee.amo.quasar.util.ModelPartExtension;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(ModelPart.class)
public class ModelPartMixin implements ModelPartExtension {

    @Unique
    private String name = "root";

    @Inject(method = "<init>", at = @At("TAIL"))
    private void quasar$setChildNames(List<ModelPart.Cube> pCubes, Map<String, ModelPart> pChildren, CallbackInfo ci){
        for(Map.Entry<String, ModelPart> child : pChildren.entrySet()){
            ((ModelPartExtension)(Object)child.getValue()).setName(child.getKey());
        }
    }


    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name == null ? "" : this.name;
    }
}
