package coffee.amo.quasar.util;

import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;

public class MathUtil {

    public static float angleCos(Vector3f v1, Vector3f v2) {
        float x = v1.x(), y = v1.y(), z = v1.z();
        float length1Squared = Math.fma(x, x, Math.fma(y, y, z * z));
        float length2Squared = Math.fma(v2.x(), v2.x(), Math.fma(v2.y(), v2.y(), v2.z() * v2.z()));
        float dot = Math.fma(x, v2.x(), Math.fma(y, v2.y(), z * v2.z()));
        return dot / (float) Math.sqrt(length1Squared * length2Squared);
    }

    public static float angle(Vector3f v1, Vector3f v2) {
        float cos = angleCos(v1, v2);
        // This is because sometimes cos goes above 1 or below -1 because of lost precision
        cos = cos < 1 ? cos : 1;
        cos = cos > -1 ? cos : -1;
        return (float) Math.acos(cos);
    }

    public static Vector4f vec4fFromVec3(Vec3 vec3, float w) {
        return new Vector4f((float) vec3.x, (float) vec3.y, (float) vec3.z, w);
    }

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
    public static Vec3 rotateQuat(Vec3 V, Quaternionf Q)
    {
        Quaternionf q=new Quaternionf((float)V.x,(float)V.y,(float)V.z,0.0f);
        Quaternionf Q2 = new Quaternionf(q);
        q.mul(Q2);
        Q2.conjugate();
        Q2.mul(q);
        return new Vec3(Q2.x(),Q2.y(),Q2.z());
    }
    /**
     * Rotates a vector by the inverse of a quaternion
     *
     * @param V The vector to be rotated
     * @param Q The quaternion to rotate by
     * @return The rotated vector
     */
    public static Vec3 rotateQuatReverse(Vec3 V, Quaternionf Q)
    {
        Quaternionf q=new Quaternionf((float)V.x,(float)V.y,(float)V.z,0.0f);
        Quaternionf Q2 = new Quaternionf(q);
        Q2.conjugate();
        q.mul(Q2);
        Q2.conjugate();
        Q2.mul(q);
        return new Vec3(Q2.x(),Q2.y(),Q2.z());
    }


    public static int colorFromVec4f(Vector4f color) {
        float r = color.x();
        float g = color.y();
        float b = color.z();
        float a = color.w();
        return ((int) (r * 255) << 16) | ((int) (g * 255) << 8) | ((int) (b * 255)) | ((int) (a * 255) << 24);
    }

    public static Vec3[] vec4fToVec3List(Vector4f[] vec4fList) {
        Vec3[] vec3List = new Vec3[vec4fList.length];
        for (int i = 0; i < vec4fList.length; i++) {
            vec3List[i] = new Vec3(vec4fList[i].x(), vec4fList[i].y(), vec4fList[i].z());
        }
        return vec3List;
    }
}
