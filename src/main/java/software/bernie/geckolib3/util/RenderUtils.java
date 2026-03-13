package software.bernie.geckolib3.util;

import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;

import javax.vecmath.Vector3f;

/**
 * Static rendering utility methods for bone and cube transforms.
 * Delegates to the GeckoLib MatrixStack.
 */
public class RenderUtils {
	public static void moveToPivot(GeoCube cube, MatrixStack stack) {
		Vector3f pivot = cube.pivot;
		stack.translate(pivot.x / 16, pivot.y / 16, pivot.z / 16);
	}

	public static void moveBackFromPivot(GeoCube cube, MatrixStack stack) {
		Vector3f pivot = cube.pivot;
		stack.translate(-pivot.x / 16, -pivot.y / 16, -pivot.z / 16);
	}

	public static void moveToPivot(GeoBone bone, MatrixStack stack) {
		stack.moveToPivot(bone);
	}

	public static void moveBackFromPivot(GeoBone bone, MatrixStack stack) {
		stack.moveBackFromPivot(bone);
	}

	public static void scale(GeoBone bone, MatrixStack stack) {
		stack.scale(bone);
	}

	public static void translate(GeoBone bone, MatrixStack stack) {
		stack.translate(bone);
	}

	public static void rotate(GeoBone bone, MatrixStack stack) {
		stack.rotate(bone);
	}

	public static void rotate(GeoCube cube, MatrixStack stack) {
		stack.rotate(cube);
	}
}
