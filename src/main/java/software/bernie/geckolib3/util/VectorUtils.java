package software.bernie.geckolib3.util;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;

public class VectorUtils {
	public static Vector3d fromArray(double[] array) {
		Validate.validIndex(ArrayUtils.toObject(array), 2);
		return new Vector3d(array[0], array[1], array[2]);
	}

	public static Vector3f fromArray(float[] array) {
		Validate.validIndex(ArrayUtils.toObject(array), 2);
		return new Vector3f(array[0], array[1], array[2]);
	}

	public static Vector3f convertDoubleToFloat(Vector3d vector) {
		return new Vector3f((float) vector.x, (float) vector.y, (float) vector.z);
	}

	public static Vector3d convertFloatToDouble(Vector3f vector) {
		return new Vector3d(vector.x, vector.y, vector.z);
	}

    public static void transform(Vector4f vector4f, Matrix4f pMatrix) {
        float f = vector4f.x;
        float f1 = vector4f.y;
        float f2 = vector4f.z;
        float f3 = vector4f.w;
        vector4f.x = pMatrix.m00 * f + pMatrix.m01 * f1 + pMatrix.m02 * f2 + pMatrix.m03 * f3;
        vector4f.y = pMatrix.m10 * f + pMatrix.m11 * f1 + pMatrix.m12 * f2 + pMatrix.m13 * f3;
        vector4f.z = pMatrix.m20 * f + pMatrix.m21 * f1 + pMatrix.m22 * f2 + pMatrix.m23 * f3;
        vector4f.w = pMatrix.m30 * f + pMatrix.m31 * f1 + pMatrix.m32 * f2 + pMatrix.m33 * f3;
    }
}
