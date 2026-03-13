package software.bernie.geckolib3.world.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.WorldServer;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores a small amount of NBT data with each world in order to track the last
 * free IDs for various tasks (such as animation data IDs for items).
 */
public class GeckoLibIdTracker extends WorldSavedData {
	private static final String NAME = "geckolib_ids";
	private final Map<String, Integer> usedIds = new HashMap<>();

	public GeckoLibIdTracker() {
		super(NAME);
	}

	public GeckoLibIdTracker(String name) {
		super(name);
	}

	public static GeckoLibIdTracker from(WorldServer world) {
		// Always use the overworld's map storage for consistent IDs across dimensions
		WorldServer overworld = MinecraftServer.getServer().worldServerForDimension(0);
		GeckoLibIdTracker tracker = (GeckoLibIdTracker) overworld.mapStorage.loadData(GeckoLibIdTracker.class, NAME);
		if (tracker == null) {
			tracker = new GeckoLibIdTracker();
			overworld.mapStorage.setData(NAME, tracker);
		}
		return tracker;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		this.usedIds.clear();
		for (Object key : tag.func_150296_c()) {
			String keyStr = (String) key;
			if (tag.hasKey(keyStr, 99)) {
				this.usedIds.put(keyStr, tag.getInteger(keyStr));
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		for (Map.Entry<String, Integer> entry : this.usedIds.entrySet()) {
			tag.setInteger(entry.getKey(), entry.getValue());
		}
	}

	public int getNextId(Type type) {
		Integer current = this.usedIds.get(type.key);
		final int id = (current == null ? -1 : current) + 1;
		this.usedIds.put(type.key, id);
		this.markDirty();
		return id;
	}

	public enum Type {
		ITEM("Item");

		final String key;

		Type(String key) {
			this.key = key;
		}
	}
}
