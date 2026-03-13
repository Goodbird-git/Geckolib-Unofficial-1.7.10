package software.bernie.geckolib3.network;

/**
 * Interface for objects that can have their animation state synchronized
 * from the server to the client.
 */
public interface ISyncable {
	/**
	 * Called on the client when an animation sync packet arrives.
	 *
	 * @param id    The animation data ID (e.g., from GeckoLibUtil.guaranteeIDForStack)
	 * @param state An application-defined state value
	 */
	void onAnimationSync(int id, int state);

	/**
	 * Returns a unique key for this syncable. Defaults to the class name.
	 */
	default String getSyncKey() {
		return this.getClass().getName();
	}
}
