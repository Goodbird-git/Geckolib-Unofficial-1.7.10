package software.bernie.geckolib3.model;

import java.util.Collections;

import javax.annotation.Nullable;

import cpw.mods.fml.common.FMLCommonHandler;
import software.bernie.geckolib3.file.AnimationFile;
import com.eliotlash.molang.MolangParser;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import software.bernie.geckolib3.animation.AnimationTicker;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.processor.AnimationProcessor;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.exception.GeoModelException;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.model.provider.IAnimatableModelProvider;
import software.bernie.geckolib3.molang.MolangRegistrar;
import software.bernie.geckolib3.resource.GeckoLibCache;
import software.bernie.geckolib3.util.MolangUtils;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AnimatedGeoModel<T extends IAnimatable> extends GeoModelProvider<T>
        implements IAnimatableModel<T>, IAnimatableModelProvider<T> {
    private final AnimationProcessor animationProcessor;
    private GeoModel currentModel;

    protected AnimatedGeoModel() {
        this.animationProcessor = new AnimationProcessor(this);
    }

    public void registerBone(GeoBone bone) {
        registerModelRenderer(bone);

        for (GeoBone childBone : bone.childBones) {
            registerBone(childBone);
        }
    }

    @Override
    public void setLivingAnimations(T entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        // Each animation has it's own collection of animations (called the
        // EntityAnimationManager), which allows for multiple independent animations
        AnimationData manager = entity.getFactory().getOrCreateAnimationData(uniqueID);
        if (manager.ticker == null) {
            AnimationTicker ticker = new AnimationTicker(manager);
            manager.ticker = ticker;
            FMLCommonHandler.instance().bus().register(ticker);
        }
        if (!Minecraft.getMinecraft().isGamePaused() || manager.shouldPlayWhilePaused) {
            seekTime = manager.tick + Minecraft.getMinecraft().timer.renderPartialTicks;
        } else {
            seekTime = manager.tick;
        }

        AnimationEvent<T> predicate;
        if (customPredicate == null) {
            predicate = new AnimationEvent<T>(entity, 0, 0, (float) (manager.tick - lastGameTickTime), false, Collections.emptyList());
        } else {
            predicate = customPredicate;
        }

        predicate.animationTick = seekTime;
        animationProcessor.preAnimationSetup(predicate.getAnimatable(), seekTime);
        if (!this.animationProcessor.getModelRendererList().isEmpty()) {
            animationProcessor.tickAnimation(entity, uniqueID, seekTime, predicate, MolangRegistrar.getParser(),
                    shouldCrashOnMissing);
        }
    }

    @Override
    public AnimationProcessor getAnimationProcessor() {
        return this.animationProcessor;
    }

    public void registerModelRenderer(IBone modelRenderer) {
        animationProcessor.registerModelRenderer(modelRenderer);
    }

    @Override
    public Animation getAnimation(String name, IAnimatable animatable) {
        AnimationFile file = GeckoLibCache.getInstance().getAnimations().get(this.getAnimationFileLocation((T) animatable));
        if (file != null) {
            return file.getAnimation(name);
        }
        return null;
    }

    @Override
    public GeoModel getModel(ResourceLocation location) {
        GeoModel model = super.getModel(location);
        if (model == null) {
            throw new GeoModelException(location, "Could not find model.");
        }
        if (model != currentModel) {
            this.animationProcessor.clearModelRendererList();
            for (GeoBone bone : model.topLevelBones) {
                registerBone(bone);
            }
            this.currentModel = model;
        }
        return model;
    }

    @Override
    public void setMolangQueries(IAnimatable animatable, double currentTick) {
        MolangParser parser = MolangRegistrar.getParser();
        Minecraft minecraftInstance = Minecraft.getMinecraft();
        float partialTick = minecraftInstance.timer.renderPartialTicks;

        parser.setValue("query.actor_count", minecraftInstance.theWorld.loadedEntityList.size());
        parser.setValue("query.time_of_day", MolangUtils.normalizeTime(minecraftInstance.theWorld.getTotalWorldTime()));
        parser.setValue("query.moon_phase", minecraftInstance.theWorld.getMoonPhase());

        if (animatable instanceof Entity) {
            Entity entity = (Entity) animatable;
            Entity camera = minecraftInstance.renderViewEntity;

            Vec3 entityCamera = Vec3.createVectorHelper(camera.prevPosX + (camera.posX - camera.prevPosX) * partialTick,
                    camera.prevPosY + (camera.posY - camera.prevPosY) * partialTick,
                    camera.prevPosZ + (camera.posZ - camera.prevPosZ) * partialTick);
            Vec3 entityPosition = Vec3.createVectorHelper(entity.prevPosX + (entity.posX - entity.prevPosX) * partialTick,
                    entity.prevPosY + (entity.posY - entity.prevPosY) * partialTick,
                    entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTick);
            double distance = entityCamera.addVector(ActiveRenderInfo.objectX, ActiveRenderInfo.objectY, ActiveRenderInfo.objectZ).distanceTo(entityPosition);

            parser.setValue("query.distance_from_camera", distance);
            parser.setValue("query.is_on_ground", MolangUtils.booleanToFloat(((Entity) animatable).onGround));
            parser.setValue("query.is_in_water", MolangUtils.booleanToFloat(((Entity) animatable).isInWater()));
            // Should probably check specifically whether it's in rain?
            parser.setValue("query.is_in_water_or_rain", MolangUtils.booleanToFloat(((Entity) animatable).isWet()));

            if (animatable instanceof EntityLivingBase) {
                EntityLivingBase livingEntity = (EntityLivingBase) animatable;
                parser.setValue("query.health", livingEntity.getHealth());
                parser.setValue("query.max_health", livingEntity.getMaxHealth());
                parser.setValue("query.is_on_fire", MolangUtils.booleanToFloat(livingEntity.isBurning()));

                double dx = livingEntity.motionX;
                double dz = livingEntity.motionZ;
                float groundSpeed = MathHelper.sqrt_double((dx * dx) + (dz * dz));
                parser.setValue("query.ground_speed", groundSpeed);

                float yawSpeed = this.getYaw(livingEntity, Minecraft.getMinecraft().timer.renderPartialTicks)
                        - this.getYaw(livingEntity, (float) (Minecraft.getMinecraft().timer.renderPartialTicks - 0.1));
                parser.setValue("query.yaw_speed", yawSpeed);
            }
        }
    }

    private float getYaw(EntityLivingBase entity, float tick) {
        return entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * tick;
    }

    @Override
    public double getCurrentTick() {
        return (Minecraft.getSystemTime() / 50d);
    }
}
