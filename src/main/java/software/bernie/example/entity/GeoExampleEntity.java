package software.bernie.example.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import software.bernie.example.GeckoLibMod;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.particles.BedrockLibrary;
import software.bernie.geckolib3.particles.emitter.BedrockEmitter;

public class GeoExampleEntity extends EntityCreature implements IAnimatable, IAnimationTickable {
	private AnimationFactory factory = new AnimationFactory(this);
	public BedrockEmitter emitter;
	public String particleName = "snow";
	public boolean restart = false;
	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
		event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.bat.fly", true));
		return PlayState.CONTINUE;
	}

	public GeoExampleEntity(World worldIn) {
		super(worldIn);
		this.ignoreFrustumCheck = true;
		this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.setSize(5F, 5F);
		emitter = new BedrockEmitter();
		emitter.setScheme(GeckoLibMod.particleLibraryInstance.presets.get(particleName));
		emitter.setTarget(Minecraft.getMinecraft().thePlayer);
		emitter.start();
	}

	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<GeoExampleEntity>(this, "controller", 50, this::predicate));
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}

	@Override
	public int tickTimer() {
		return ticksExisted;
	}

	@Override
	public void onLivingUpdate() {
		//super.onLivingUpdate();
		if(worldObj.isRemote) {
			//setRotation(0,0);
			if (!restart && emitter.scheme!=null && !emitter.scheme.toReload) {
				emitter.update();
			} else {
				emitter.stop();
				emitter = new BedrockEmitter();
				emitter.setScheme(BedrockLibrary.instance.presets.get(particleName));
				emitter.setTarget(this);
				emitter.start();
				restart = false;
			}
		}
	}

	@Override
	public void tick() {
		super.onUpdate();

	}
}
