package software.bernie.geckolib3.renderers.geo;

import net.geckominecraft.client.renderer.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.example.config.ConfigHandler;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;

/**
 * Extended entity renderer that automates rendering of held items and armor
 * at designated bone positions on a GeckoLib animated entity.
 *
 * Subclasses specify which bones correspond to equipment slots by implementing
 * the abstract identification methods. The renderer then automatically renders
 * the appropriate items/armor/blocks at those bone positions during the
 * recursive bone traversal.
 *
 * Ported from GeckoLib 1.16 concept, adapted for Minecraft 1.7.10 GL rendering.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class ExtendedGeoEntityRenderer<T extends EntityLivingBase & IAnimatable>
		extends GeoEntityRenderer<T> {

	protected T currentEntityBeingRendered;
	protected float currentPartialTicks;

	public ExtendedGeoEntityRenderer(AnimatedGeoModel<T> modelProvider) {
		super(modelProvider);
	}

	@Override
	public void doRender(net.minecraft.entity.Entity entity, double x, double y, double z,
						 float entityYaw, float partialTicks) {
		if (entity instanceof EntityLivingBase) {
			this.currentEntityBeingRendered = (T) entity;
			this.currentPartialTicks = partialTicks;
		}
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	public void renderRecursively(Tessellator builder, T animatable, GeoBone bone,
								  float red, float green, float blue, float alpha) {
		MATRIX_STACK.push();

		MATRIX_STACK.translate(bone);
		MATRIX_STACK.moveToPivot(bone);
		MATRIX_STACK.rotate(bone);
		MATRIX_STACK.scale(bone);
		MATRIX_STACK.moveBackFromPivot(bone);

		// Track bone transforms
		if (bone.isTrackingXform()) {
			bone.setModelSpaceXform(MATRIX_STACK.getModelMatrix());
			GeoBone parent = bone.getParent();
			if (parent != null && parent.isTrackingXform()) {
				Matrix4f inverseParent = new Matrix4f(parent.getModelSpaceXform());
				inverseParent.invert();
				Matrix4f localXform = new Matrix4f(inverseParent);
				localXform.mul(MATRIX_STACK.getModelMatrix());
				bone.setLocalSpaceXform(localXform);
			} else {
				bone.setLocalSpaceXform(MATRIX_STACK.getModelMatrix());
			}
		}

		if (isBoneRenderOverriden(animatable, bone)) {
			drawOverridenBone(animatable, bone);
			MATRIX_STACK.pop();
			return;
		}

		// Render cubes for this bone
		if (!bone.isHidden()) {
			for (GeoCube cube : bone.childCubes) {
				MATRIX_STACK.push();
				GlStateManager.pushMatrix();
				try {
					renderCube(builder, cube, red, green, blue, alpha);
				} catch (Exception e) {
					if (ConfigHandler.debugPrintStacktraces) {
						e.printStackTrace();
					}
				} finally {
					GlStateManager.popMatrix();
					MATRIX_STACK.pop();
				}
			}
		}

		// After rendering this bone's cubes, handle equipment rendering
		if (currentEntityBeingRendered != null) {
			handleExtendedRendering(bone);
		}

		// Recurse into child bones
		if (!bone.childBonesAreHiddenToo()) {
			for (GeoBone childBone : bone.childBones) {
				renderRecursively(builder, animatable, childBone, red, green, blue, alpha);
			}
		}

		MATRIX_STACK.pop();
	}

	/**
	 * Handles rendering of items, armor, and blocks at the current bone position.
	 */
	protected void handleExtendedRendering(GeoBone bone) {
		// Check for held items
		ItemStack heldItem = getHeldItemForBone(bone.getName(), currentEntityBeingRendered);
		if (heldItem != null) {
			renderItemAtBone(bone, heldItem);
		}

		// Check for held blocks
		ItemStack heldBlock = getHeldBlockForBone(bone.getName(), currentEntityBeingRendered);
		if (heldBlock != null && heldBlock.getItem() instanceof ItemBlock) {
			renderBlockAtBone(bone, heldBlock);
		}

		// Check for armor
		ItemStack armorItem = getArmorForBone(bone.getName(), currentEntityBeingRendered);
		if (armorItem != null) {
			renderArmorAtBone(bone, armorItem);
		}
	}

	/**
	 * Renders an ItemStack at the given bone's current matrix position.
	 */
	protected void renderItemAtBone(GeoBone bone, ItemStack stack) {
		preRenderItem(bone, stack, currentEntityBeingRendered);

		// End the current tessellation draw to switch to item rendering
		// Item rendering uses its own draw calls
		GL11.glPushMatrix();
		try {
			// Apply the bone's accumulated model transform
			Matrix4f mat = MATRIX_STACK.getModelMatrix();
			float[] glMat = new float[] {
				mat.m00, mat.m10, mat.m20, mat.m30,
				mat.m01, mat.m11, mat.m21, mat.m31,
				mat.m02, mat.m12, mat.m22, mat.m32,
				mat.m03, mat.m13, mat.m23, mat.m33
			};
			java.nio.FloatBuffer buf = org.lwjgl.BufferUtils.createFloatBuffer(16);
			buf.put(glMat);
			buf.flip();
			GL11.glMultMatrix(buf);

			// Move to pivot point for proper positioning
			GL11.glTranslatef(bone.rotationPointX / 16f, bone.rotationPointY / 16f, bone.rotationPointZ / 16f);

			// Scale down for item rendering
			float scale = 0.35f;
			GL11.glScalef(scale, scale, scale);

			GlStateManager.enableLighting();
			GlStateManager.enableTexture2D();

			// Render as a 3D item in the world
			RenderItem.getInstance().renderItemAndEffectIntoGUI(
					Minecraft.getMinecraft().fontRenderer,
					Minecraft.getMinecraft().renderEngine,
					stack, 0, 0);
		} catch (Exception e) {
			if (ConfigHandler.debugPrintStacktraces) {
				e.printStackTrace();
			}
		} finally {
			GL11.glPopMatrix();
		}

		postRenderItem(bone, stack, currentEntityBeingRendered);
	}

	/**
	 * Renders a Block item at the given bone's current matrix position.
	 */
	protected void renderBlockAtBone(GeoBone bone, ItemStack blockStack) {
		preRenderBlock(bone, blockStack, currentEntityBeingRendered);

		GL11.glPushMatrix();
		try {
			Matrix4f mat = MATRIX_STACK.getModelMatrix();
			float[] glMat = new float[] {
				mat.m00, mat.m10, mat.m20, mat.m30,
				mat.m01, mat.m11, mat.m21, mat.m31,
				mat.m02, mat.m12, mat.m22, mat.m32,
				mat.m03, mat.m13, mat.m23, mat.m33
			};
			java.nio.FloatBuffer buf = org.lwjgl.BufferUtils.createFloatBuffer(16);
			buf.put(glMat);
			buf.flip();
			GL11.glMultMatrix(buf);

			GL11.glTranslatef(bone.rotationPointX / 16f, bone.rotationPointY / 16f, bone.rotationPointZ / 16f);

			float scale = 0.3f;
			GL11.glScalef(scale, scale, scale);

			GlStateManager.enableLighting();
			GlStateManager.enableTexture2D();

			// Render block in-world using RenderBlocks
			ItemBlock itemBlock = (ItemBlock) blockStack.getItem();
			Block block = itemBlock.field_150939_a;
			Minecraft.getMinecraft().renderEngine.bindTexture(
					net.minecraft.client.renderer.texture.TextureMap.locationBlocksTexture);
			RenderBlocks.getInstance().renderBlockAsItem(block, blockStack.getItemDamage(), 1.0f);
		} catch (Exception e) {
			if (ConfigHandler.debugPrintStacktraces) {
				e.printStackTrace();
			}
		} finally {
			GL11.glPopMatrix();
		}

		postRenderBlock(bone, blockStack, currentEntityBeingRendered);
	}

	/**
	 * Renders armor at the given bone's current matrix position.
	 */
	protected void renderArmorAtBone(GeoBone bone, ItemStack armorStack) {
		if (!(armorStack.getItem() instanceof ItemArmor)) return;
		ItemArmor armorItem = (ItemArmor) armorStack.getItem();

		// Check if this armor has a GeoArmorRenderer (per-entity if available)
		GeoArmorRenderer geoArmorRenderer = GeoArmorRenderer.getRenderer(armorItem.getClass(), currentEntityBeingRendered);
		if (geoArmorRenderer != null) {
			// Use GeckoLib geo armor rendering
			GL11.glPushMatrix();
			try {
				Matrix4f mat = MATRIX_STACK.getModelMatrix();
				float[] glMat = new float[] {
					mat.m00, mat.m10, mat.m20, mat.m30,
					mat.m01, mat.m11, mat.m21, mat.m31,
					mat.m02, mat.m12, mat.m22, mat.m32,
					mat.m03, mat.m13, mat.m23, mat.m33
				};
				java.nio.FloatBuffer buf = org.lwjgl.BufferUtils.createFloatBuffer(16);
				buf.put(glMat);
				buf.flip();
				GL11.glMultMatrix(buf);

				int slot = getArmorSlotForBone(bone.getName(), currentEntityBeingRendered);
				if (slot >= 0) {
					geoArmorRenderer.setCurrentItem(currentEntityBeingRendered, armorStack, slot);
					geoArmorRenderer.applyEntityStats(currentEntityBeingRendered).applySlot(slot);
					geoArmorRenderer.render(currentPartialTicks);
				}
			} catch (Exception e) {
				if (ConfigHandler.debugPrintStacktraces) {
					e.printStackTrace();
				}
			} finally {
				GL11.glPopMatrix();
			}
		}
		// Vanilla armor rendering can be added here if needed
	}

	// ---- Abstract methods for subclasses to implement ----

	/**
	 * Returns the ItemStack to render at the given bone, or null.
	 * Typically used for hand bones to show held items.
	 */
	@Nullable
	protected abstract ItemStack getHeldItemForBone(String boneName, T currentEntity);

	/**
	 * Returns a block ItemStack to render at the given bone, or null.
	 */
	@Nullable
	protected abstract ItemStack getHeldBlockForBone(String boneName, T currentEntity);

	/**
	 * Returns the armor ItemStack to render at the given bone, or null.
	 */
	@Nullable
	protected ItemStack getArmorForBone(String boneName, T currentEntity) {
		return null;
	}

	/**
	 * Returns the armor slot index (0-3) for the given bone name, or -1 if not an armor bone.
	 */
	protected int getArmorSlotForBone(String boneName, T currentEntity) {
		return -1;
	}

	/**
	 * Called before rendering an item at a bone.
	 */
	protected void preRenderItem(GeoBone bone, ItemStack stack, T currentEntity) {
	}

	/**
	 * Called after rendering an item at a bone.
	 */
	protected void postRenderItem(GeoBone bone, ItemStack stack, T currentEntity) {
	}

	/**
	 * Called before rendering a block at a bone.
	 */
	protected void preRenderBlock(GeoBone bone, ItemStack stack, T currentEntity) {
	}

	/**
	 * Called after rendering a block at a bone.
	 */
	protected void postRenderBlock(GeoBone bone, ItemStack stack, T currentEntity) {
	}
}
