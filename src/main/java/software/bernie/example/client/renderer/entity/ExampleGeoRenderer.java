package software.bernie.example.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import software.bernie.example.client.model.entity.ExampleEntityModel;
import software.bernie.example.entity.GeoExampleEntity;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.particles.emitter.BedrockEmitter;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.util.MatrixStack;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

public class ExampleGeoRenderer extends GeoEntityRenderer<GeoExampleEntity> {

	public ExampleGeoRenderer() {
		super(new ExampleEntityModel());
	}

	@Override
	public void renderAfter(GeoModel model, GeoExampleEntity animatable, float ticks, float red, float green, float blue, float alpha) {
		BedrockEmitter emitter = animatable.emitter;
		emitter.prevGlobal.x=emitter.lastGlobal.x;
		emitter.prevGlobal.y=emitter.lastGlobal.y;
		emitter.prevGlobal.z=emitter.lastGlobal.z;

		emitter.lastGlobal.x=animatable.posX;
		emitter.lastGlobal.y=animatable.posY;
		emitter.lastGlobal.z=animatable.posZ;
		RenderHelper.disableStandardItemLighting();

		boolean shouldSit = (animatable.ridingEntity != null && animatable.ridingEntity.shouldRiderSit());
		Pair<Float,Float> rotations = calculateRotations(animatable,ticks,shouldSit);
		deapplyRotations(animatable,this.handleRotationFloat(animatable, ticks),rotations.getKey(),ticks);

		GL11.glTranslated(-animatable.posX,-animatable.posY,-animatable.posZ);
		emitter.rotation.setIdentity();

		MATRIX_STACK.push();
		//MATRIX_STACK.rotateY((float) (Math.PI/2));
//		MATRIX_STACK.translate(1,1,0);
//		MATRIX_STACK.rotateY((float) (Math.PI/2));
//		MATRIX_STACK.scale(5,5,5);

		Matrix4f full = MATRIX_STACK.getModelMatrix();
		emitter.rotation = new Matrix3f(full.m00,full.m01,full.m02,full.m10,full.m11,full.m12,full.m20,full.m21,full.m22);
		emitter.lastGlobal.x+=full.m03;
		emitter.lastGlobal.y+=full.m13;
		emitter.lastGlobal.z+=full.m23;

		MATRIX_STACK.pop();
		emitter.render(ticks);
		RenderHelper.enableStandardItemLighting();
	}
}
