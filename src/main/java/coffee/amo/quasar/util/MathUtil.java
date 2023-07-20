package coffee.amo.quasar.util;

import com.mojang.math.Vector4f;

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
}
