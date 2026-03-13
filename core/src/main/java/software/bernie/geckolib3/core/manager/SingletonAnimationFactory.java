package software.bernie.geckolib3.core.manager;

import java.util.HashMap;

import software.bernie.geckolib3.core.IAnimatable;

/**
 * AnimationFactory implementation for singleton/flyweight objects such as Items.
 * Utilises a keyed map to differentiate different instances of the object.
 */
public class SingletonAnimationFactory extends AnimationFactory {
	private final HashMap<Integer, AnimationData> animationDataMap = new HashMap<>();

	public SingletonAnimationFactory(IAnimatable animatable) {
		super(animatable);
	}

	@Override
	public AnimationData getOrCreateAnimationData(int uniqueID) {
		if (!this.animationDataMap.containsKey(uniqueID)) {
			AnimationData data = new AnimationData();
			this.animatable.registerControllers(data);
			this.animationDataMap.put(uniqueID, data);
		}
		return this.animationDataMap.get(uniqueID);
	}

	@Override
	public AnimationData getOrCreateAnimationData(int uniqueID, IAnimatable registrant) {
		if (!this.animationDataMap.containsKey(uniqueID)) {
			AnimationData data = new AnimationData();
			registrant.registerControllers(data);
			this.animationDataMap.put(uniqueID, data);
		}
		return this.animationDataMap.get(uniqueID);
	}
}
