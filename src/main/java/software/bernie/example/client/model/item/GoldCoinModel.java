package software.bernie.example.client.model.item;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.item.AnimatableStackWrapper;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class GoldCoinModel extends AnimatedGeoModel<AnimatableStackWrapper> {
    @Override
    public ResourceLocation getModelLocation(AnimatableStackWrapper object) {
        int coinNumber = object.getStack().stackSize;
        if(coinNumber<21) {
            return new ResourceLocation(GeckoLib.ModID, "geo/coinpilesmall.geo.json");
        }
        if(coinNumber<42){
            return new ResourceLocation(GeckoLib.ModID, "geo/coinpilemedium.geo.json");
        }
        return new ResourceLocation(GeckoLib.ModID, "geo/coinpilelarge.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(AnimatableStackWrapper object) {
        return new ResourceLocation(GeckoLib.ModID, "textures/items/coinpilesmall.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(AnimatableStackWrapper animatable) {
        return new ResourceLocation(GeckoLib.ModID, "animations/none.animations.json");
    }
}
