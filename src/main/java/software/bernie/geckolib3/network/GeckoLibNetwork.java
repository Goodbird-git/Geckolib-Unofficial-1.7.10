package software.bernie.geckolib3.network;

import net.minecraft.entity.player.EntityPlayerMP;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Central network system for GeckoLib animation synchronization.
 * Allows the server to trigger animations on clients via ISyncable objects.
 */
public class GeckoLibNetwork {
	private static final Map<String, Supplier<ISyncable>> SYNCABLES = new HashMap<>();

	/**
	 * Sends an animation sync packet to a specific player.
	 *
	 * @param player   The player to send the sync to
	 * @param syncable The syncable object
	 * @param id       The animation data ID
	 * @param state    An application-defined state value
	 */
	public static void syncAnimation(EntityPlayerMP player, ISyncable syncable, int id, int state) {
		final String key = syncable.getSyncKey();
		if (!SYNCABLES.containsKey(key)) {
			throw new IllegalArgumentException("Syncable not registered for " + key);
		}
		NetworkHandler.sendToPlayer(new PacketSyncAnimation(key, id, state), player);
	}

	/**
	 * Sends an animation sync packet to all players.
	 *
	 * @param syncable The syncable object
	 * @param id       The animation data ID
	 * @param state    An application-defined state value
	 */
	public static void syncAnimationToAll(ISyncable syncable, int id, int state) {
		final String key = syncable.getSyncKey();
		if (!SYNCABLES.containsKey(key)) {
			throw new IllegalArgumentException("Syncable not registered for " + key);
		}
		NetworkHandler.sendToAll(new PacketSyncAnimation(key, id, state));
	}

	/**
	 * Retrieves the ISyncable registered under the given key.
	 */
	public static ISyncable getSyncable(String key) {
		final Supplier<ISyncable> supplier = SYNCABLES.get(key);
		return supplier == null ? null : supplier.get();
	}

	/**
	 * Registers an ISyncable for network synchronization.
	 * The syncable is stored as a supplier to avoid holding strong references.
	 *
	 * @param syncable The syncable to register
	 */
	public static void registerSyncable(ISyncable syncable) {
		final String key = syncable.getSyncKey();
		final ISyncable ref = syncable;
		if (SYNCABLES.putIfAbsent(key, () -> ref) != null) {
			throw new IllegalArgumentException("Syncable already registered for " + key);
		}
		System.out.println("[GeckoLib] Registered syncable for " + key);
	}
}
