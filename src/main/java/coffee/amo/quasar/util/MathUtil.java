package coffee.amo.quasar.util;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector4f;
import net.minecraft.world.phys.Vec3;

public class MathUtil {

    public static Vector4f[] copyVector4fArray(Vector4f[] array) {
        Vector4f[] copy = new Vector4f[array.length];
        for (int i = 0; i < array.length; i++) {
            copy[i] = copyVector4f(array[i]);
        }
        return copy;
    }

    public static Vector4f copyVector4f(Vector4f vector) {
        return new Vector4f(vector.x(), vector.y(), vector.z(), vector.w());
    }
    /**
     * Rotates a vector by a quaternion
     *
     * @param V The vector to be rotated
     * @param Q The quaternion to rotate by
     * @return The rotated vector
     */
    public static Vec3 rotateQuat(Vec3 V, Quaternion Q)
    {
        Quaternion q=new Quaternion((float)V.x,(float)V.y,(float)V.z,0.0f);
        Quaternion Q2 = Q.copy();
        q.mul(Q2);
        Q2.conj();
        Q2.mul(q);
        return new Vec3(Q2.i(),Q2.j(),Q2.k());
    }
    /**
     * Rotates a vector by the inverse of a quaternion
     *
     * @param V The vector to be rotated
     * @param Q The quaternion to rotate by
     * @return The rotated vector
     */
    public static Vec3 rotateQuatReverse(Vec3 V, Quaternion Q)
    {
        Quaternion q=new Quaternion((float)V.x,(float)V.y,(float)V.z,0.0f);
        Quaternion Q2 = Q.copy();
        Q2.conj();
        q.mul(Q2);
        Q2.conj();
        Q2.mul(q);
        return new Vec3(Q2.i(),Q2.j(),Q2.k());
    }
    
}
