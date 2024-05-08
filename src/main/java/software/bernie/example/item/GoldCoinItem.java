package software.bernie.example.item;

import net.minecraft.item.Item;
import software.bernie.example.GeckoLibMod;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class GoldCoinItem extends Item implements IAnimatable {
    public AnimationFactory factory = new AnimationFactory(this);

    private <P extends Item & IAnimatable> PlayState predicate(AnimationEvent<P> event) {
        // Not setting an animation here as that's handled in onItemRightClick
        return PlayState.CONTINUE;
    }

    public GoldCoinItem() {
        super();
        this.setCreativeTab(GeckoLibMod.getGeckolibItemGroup());
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "popupController", 20, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
