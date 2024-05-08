package software.bernie.geckolib3.item;

import net.minecraft.item.ItemStack;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class AnimatableStackWrapper implements IAnimatable {
    private ItemStack stack;

    private AnimatableStackWrapper(ItemStack stack){
        this.stack = stack;
    }

    @Override
    public void registerControllers(AnimationData data) {
        getAnimatable().registerControllers(data);
    }

    @Override
    public AnimationFactory getFactory() {
        return getAnimatable().getFactory();
    }

    private IAnimatable getAnimatable(){
        return (IAnimatable) stack.getItem();
    }

    public ItemStack getStack(){
        return stack;
    }

    public static AnimatableStackWrapper wrapItemStack(ItemStack stack){
        if(stack==null || !(stack.getItem() instanceof IAnimatable)) return null;
        return new AnimatableStackWrapper(stack);
    }
}
