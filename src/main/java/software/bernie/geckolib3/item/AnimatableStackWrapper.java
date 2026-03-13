package software.bernie.geckolib3.item;

import net.minecraft.item.ItemStack;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.function.BiConsumer;

/**
 * A wrapper that makes an ItemStack animatable without requiring the Item class itself
 * to implement IAnimatable. This is useful for dynamic items like CustomNPC+ scripted/linked items
 * where the model, texture, and animation are defined per-stack via NBT rather than per-Item class.
 *
 * Uses a shared external AnimationFactory (following the wiki's singleton pattern)
 * and stable GeckoLibID-based unique IDs for animation state isolation.
 */
public class AnimatableStackWrapper implements IAnimatable {
    private ItemStack stack;
    private final AnimationFactory factory;
    private BiConsumer<AnimatableStackWrapper, AnimationData> controllerRegistrar;
    private Object userData;

    public AnimatableStackWrapper(ItemStack stack, AnimationFactory sharedFactory) {
        this.stack = stack;
        this.factory = sharedFactory;
    }

    /**
     * Sets a callback that will register animation controllers when animation data is first created.
     */
    public AnimatableStackWrapper withControllerRegistrar(BiConsumer<AnimatableStackWrapper, AnimationData> registrar) {
        this.controllerRegistrar = registrar;
        return this;
    }

    /**
     * Attaches arbitrary user data to this wrapper, allowing the renderer/model provider
     * to access context without re-resolving it from the stack.
     */
    public AnimatableStackWrapper withUserData(Object userData) {
        this.userData = userData;
        return this;
    }

    public Object getUserData() {
        return userData;
    }

    public ItemStack getStack() {
        return stack;
    }

    /**
     * Updates the underlying ItemStack reference without creating a new wrapper.
     * This preserves the controller's {@code this.animatable} reference, preventing
     * stale wrapper issues when the ItemStack is recreated (e.g., inventory sync).
     */
    public void setStack(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public void registerControllers(AnimationData data) {
        if (controllerRegistrar != null) {
            controllerRegistrar.accept(this, data);
        }
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    /**
     * Returns the stable unique ID for this stack, using GeckoLib's built-in ID system.
     * If the stack has a GeckoLibID NBT tag (assigned by guaranteeIDForStack on the server),
     * that stable ID is returned. Otherwise falls back to a hash of the item class.
     */
    public int getUniqueId() {
        return GeckoLibUtil.getIDFromStack(stack);
    }

    /**
     * Wraps an ItemStack for use with a shared factory.
     */
    public static AnimatableStackWrapper of(ItemStack stack, AnimationFactory sharedFactory) {
        return new AnimatableStackWrapper(stack, sharedFactory);
    }
}
