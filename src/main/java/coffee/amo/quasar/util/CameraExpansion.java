package coffee.amo.quasar.util;

import net.minecraft.world.phys.AABB;

public interface CameraExpansion {
    public boolean isBoundingBoxInFrustum(AABB aabbIn);
}
