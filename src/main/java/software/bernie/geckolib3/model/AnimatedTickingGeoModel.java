package software.bernie.geckolib3.model;

import net.minecraft.client.Minecraft;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.molang.MolangRegistrar;

import javax.annotation.Nullable;
import java.util.Collections;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AnimatedTickingGeoModel<T extends IAnimatable & IAnimationTickable> extends AnimatedGeoModel<T> {
    public AnimatedTickingGeoModel() {
    }

    public boolean isInitialized() {
        return !this.getAnimationProcessor().getModelRendererList().isEmpty();
    }

    @Override
    public void setLivingAnimations(T entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        // Each animation has it's own collection of animations (called the
        // EntityAnimationManager), which allows for multiple independent animations
        AnimationData manager = entity.getFactory().getOrCreateAnimationData(uniqueID);
        if (manager.startTick == null) {
            manager.startTick = (double) (entity.tickTimer() + Minecraft.getMinecraft().timer.renderPartialTicks);
        }

        if (!Minecraft.getMinecraft().isGamePaused() || manager.shouldPlayWhilePaused) {
            manager.tick = (entity.tickTimer() + Minecraft.getMinecraft().timer.renderPartialTicks);
            double gameTick = manager.tick;
            double deltaTicks = gameTick - lastGameTickTime;
            seekTime += deltaTicks;
            lastGameTickTime = gameTick;
        }

        AnimationEvent<T> predicate;
        if (customPredicate == null) {
            predicate = new AnimationEvent<T>(entity, 0, 0, 0, false, Collections.emptyList());
        } else {
            predicate = customPredicate;
        }

        predicate.animationTick = seekTime;
        getAnimationProcessor().preAnimationSetup(predicate.getAnimatable(), seekTime);
        if (!this.getAnimationProcessor().getModelRendererList().isEmpty()) {
            getAnimationProcessor().tickAnimation(entity, uniqueID, seekTime, predicate,
                MolangRegistrar.getParser(), shouldCrashOnMissing);
        }

        if (!Minecraft.getMinecraft().isGamePaused() || manager.shouldPlayWhilePaused) {
            codeAnimations(entity, uniqueID, customPredicate);
        }
    }

    public void codeAnimations(T entity, Integer uniqueID, AnimationEvent<?> customPredicate) {

    }
}
