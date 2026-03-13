package software.bernie.geckolib3.core.manager;

import java.util.HashMap;

import software.bernie.geckolib3.core.IAnimatable;

public class AnimationFactory {
	protected final IAnimatable animatable;
	private final HashMap<Integer, AnimationData> animationDataMap = new HashMap<>();

	public AnimationFactory(IAnimatable animatable) {
		this.animatable = animatable;
	}

	/**
	 * This creates or gets the cached animation manager for any unique ID. For
	 * itemstacks, this is typically a hashcode of their nbt. For entities it should
	 * be their unique uuid. For tile entities you can use nbt or just one constant
	 * value since they are not singletons.
	 *
	 * @param uniqueID A unique integer ID. For every ID the same animation manager
	 *                 will be returned.
	 * @return the animatable manager
	 */
	public AnimationData getOrCreateAnimationData(int uniqueID) {
		if (!this.animationDataMap.containsKey(uniqueID)) {
			AnimationData data = new AnimationData();
			this.animatable.registerControllers(data);
			this.animationDataMap.put(uniqueID, data);
		}
		return this.animationDataMap.get(uniqueID);
	}

	/**
	 * Creates or gets cached animation data, using the specified registrant
	 * to register controllers instead of the factory's stored animatable.
	 * This allows a shared factory to serve multiple wrapper instances,
	 * each registering their own controllers when their ID is first seen.
	 *
	 * @param uniqueID   A unique integer ID
	 * @param registrant The IAnimatable that should register controllers for new entries
	 * @return the animatable manager
	 */
	public AnimationData getOrCreateAnimationData(int uniqueID, IAnimatable registrant) {
		if (!this.animationDataMap.containsKey(uniqueID)) {
			AnimationData data = new AnimationData();
			registrant.registerControllers(data);
			this.animationDataMap.put(uniqueID, data);
		}
		return this.animationDataMap.get(uniqueID);
	}

	/**
	 * @deprecated Use {@link AnimationFactory#getOrCreateAnimationData(int)}
	 */
	@Deprecated
	public AnimationData getOrCreateAnimationData(Integer uniqueID) {
		return getOrCreateAnimationData((int) uniqueID);
	}
}
