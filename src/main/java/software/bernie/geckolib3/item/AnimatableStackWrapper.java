package software.bernie.geckolib3.item;

import net.minecraft.item.ItemStack;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * A wrapper that makes an ItemStack animatable without requiring the Item class itself
 * to implement IAnimatable. This is useful for dynamic items like CustomNPC+ scripted/linked items
 * where the model, texture, and animation are defined per-stack via NBT rather than per-Item class.
 *
 * Each wrapper holds a reference to the ItemStack and provides its own AnimationFactory.
 * Animation state is keyed by a unique ID derived from the stack's NBT data.
 */
public class AnimatableStackWrapper implements IAnimatable {
    private final ItemStack stack;
    private final AnimationFactory factory;
    private BiConsumer<AnimatableStackWrapper, AnimationData> controllerRegistrar;
    private Object userData;
    private int ownerEntityId;
    private int slotIndex;

    public AnimatableStackWrapper(ItemStack stack) {
        this.stack = stack;
        this.factory = GeckoLibUtil.createFactory(this);
    }

    /**
     * Sets the owner entity ID and slot index for per-instance animation identity.
     * This ensures identical items in different slots or held by different entities
     * each get their own animation state.
     */
    public AnimatableStackWrapper withIdentity(int ownerEntityId, int slotIndex) {
        this.ownerEntityId = ownerEntityId;
        this.slotIndex = slotIndex;
        return this;
    }

    public int getOwnerEntityId() { return ownerEntityId; }
    public int getSlotIndex() { return slotIndex; }

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

    /**
     * Gets the attached user data, or null if none was set.
     */
    public Object getUserData() {
        return userData;
    }

    public ItemStack getStack() {
        return stack;
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
     * Generates a unique ID for this stack based on its NBT data.
     * This ensures each unique item configuration gets its own animation state.
     */
    public int getUniqueId() {
        return Objects.hash(
            stack.getItem(),
            stack.getItemDamage(),
            stack.hasTagCompound() ? stack.getTagCompound().toString() : 0,
            ownerEntityId,
            slotIndex
        );
    }

    /**
     * Wraps an ItemStack for use with GeoItemStackRenderer.
     */
    public static AnimatableStackWrapper of(ItemStack stack) {
        return new AnimatableStackWrapper(stack);
    }
}
