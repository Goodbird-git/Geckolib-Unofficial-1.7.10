package software.bernie.geckolib3.renderers.geo.layer;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;

import java.util.function.Function;

/**
 * Generic base class for more advanced layer renderers.
 * Provides utility to re-render the current model with a different texture or render type.
 *
 * Originally developed for Chocolate Quest Repoured by DerToaster98.
 * Adapted for 1.7.10.
 */
public abstract class AbstractLayerGeo<T extends EntityLivingBase & IAnimatable> extends GeoLayerRenderer<T> {

	protected final Function<T, ResourceLocation> funcGetCurrentTexture;
	protected final Function<T, ResourceLocation> funcGetCurrentModel;
	protected final GeoEntityRenderer<T> geoRendererInstance;

	public AbstractLayerGeo(GeoEntityRenderer<T> renderer,
							Function<T, ResourceLocation> funcGetCurrentTexture,
							Function<T, ResourceLocation> funcGetCurrentModel) {
		super(renderer);
		this.geoRendererInstance = renderer;
		this.funcGetCurrentTexture = funcGetCurrentTexture;
		this.funcGetCurrentModel = funcGetCurrentModel;
	}

	/**
	 * Re-renders the current model with the layer's texture.
	 * Useful for overlay layers (capes, markings, etc.)
	 */
	protected void reRenderCurrentModelInRenderer(T entity, float partialTicks, Color renderColor) {
		ResourceLocation modelLoc = this.funcGetCurrentModel.apply(entity);
		ResourceLocation textureLoc = this.funcGetCurrentTexture.apply(entity);

		GeoModel model = this.getEntityModel().getModel(modelLoc);
		Minecraft.getMinecraft().renderEngine.bindTexture(textureLoc);

		this.getRenderer().render(model, entity, partialTicks,
				renderColor.getRed() / 255f,
				renderColor.getGreen() / 255f,
				renderColor.getBlue() / 255f,
				renderColor.getAlpha() / 255f);
	}
}
