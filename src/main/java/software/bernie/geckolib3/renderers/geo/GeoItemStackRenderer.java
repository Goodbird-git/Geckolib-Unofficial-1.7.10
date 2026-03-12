package software.bernie.geckolib3.renderers.geo;

import net.geckominecraft.client.renderer.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.item.AnimatableStackWrapper;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.example.config.ConfigHandler;

import java.util.Collections;

/**
 * A GeoLib renderer for ItemStacks that uses {@link AnimatableStackWrapper} to decouple
 * animation state from the Item class. This allows dynamic items (where model/texture/animation
 * are determined per-stack via NBT) to use GeckoLib's full animation system.
 *
 * Unlike {@link GeoItemRenderer}, this renderer does not require the Item to implement IAnimatable.
 * Instead, it wraps the ItemStack in an AnimatableStackWrapper.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class GeoItemStackRenderer implements IGeoRenderer<AnimatableStackWrapper> {
    static {
        AnimationController.addModelFetcher((IAnimatable object) -> {
            if (object instanceof AnimatableStackWrapper) {
                return null; // Animation is managed externally via the wrapper
            }
            return null;
        });
    }

    protected AnimatedGeoModel<AnimatableStackWrapper> modelProvider;
    protected AnimatableStackWrapper currentWrapper;
    protected ItemStack currentItemStack;

    public GeoItemStackRenderer(AnimatedGeoModel<AnimatableStackWrapper> modelProvider) {
        this.modelProvider = modelProvider;
    }

    public void setModel(AnimatedGeoModel<AnimatableStackWrapper> model) {
        this.modelProvider = model;
    }

    @Override
    public AnimatedGeoModel<AnimatableStackWrapper> getGeoModelProvider() {
        return modelProvider;
    }

    /**
     * Renders the item stack using the GeckoLib model system.
     * Call this from your IItemRenderer or mixin injection point.
     *
     * @param wrapper   The AnimatableStackWrapper wrapping the ItemStack
     * @param itemStack The actual ItemStack being rendered
     */
    public void render(AnimatableStackWrapper wrapper, ItemStack itemStack) {
        this.currentWrapper = wrapper;
        this.currentItemStack = itemStack;

        GeoModel model;
        try {
            model = modelProvider.getModel(modelProvider.getModelLocation(wrapper));
        } catch (Exception e) {
            if (ConfigHandler.debugPrintStacktraces) {
                e.printStackTrace();
            }
            return;
        }

        AnimationEvent itemEvent = new AnimationEvent(wrapper, 0, 0,
            Minecraft.getMinecraft().timer.renderPartialTicks, false,
            Collections.singletonList(itemStack));
        modelProvider.setLivingAnimations(wrapper, this.getUniqueID(wrapper), itemEvent);

        GlStateManager.pushMatrix();
        try {
//            GlStateManager.translate(0, 0.01f, 0);
//            GlStateManager.translate(0.5, 0.5, 0.5);

            Minecraft.getMinecraft().renderEngine.bindTexture(getTextureLocation(wrapper));
            Color renderColor = getRenderColor(wrapper, 0f);
//            GL11.glRotatef(90, 0, 1, 0);
            render(model, wrapper, 0, (float) renderColor.getRed() / 255f,
                (float) renderColor.getGreen() / 255f,
                (float) renderColor.getBlue() / 255f,
                (float) renderColor.getAlpha() / 255f);
        } catch (Exception e) {
            if (ConfigHandler.debugPrintStacktraces) {
                e.printStackTrace();
            }
        } finally {
            GlStateManager.popMatrix();
        }
    }

    @Override
    public ResourceLocation getTextureLocation(AnimatableStackWrapper instance) {
        return this.modelProvider.getTextureLocation(instance);
    }

    @Override
    public Integer getUniqueID(AnimatableStackWrapper animatable) {
        return animatable.getUniqueId();
    }
}
