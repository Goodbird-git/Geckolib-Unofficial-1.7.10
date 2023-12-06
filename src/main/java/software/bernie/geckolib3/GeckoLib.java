package software.bernie.geckolib3;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import software.bernie.example.GeckoLibMod;
import software.bernie.geckolib3.particles.BedrockLibrary;
import software.bernie.geckolib3.resource.ResourceListener;

public class GeckoLib {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String ModID = "geckolib3";
	public static boolean hasInitialized;
	public static final String VERSION = "3.0.30";

	/**
	 * This method MUST be called in your mod's constructor or during
	 * onInitializeClient in fabric, otherwise models and animations won't be
	 * loaded.
	 */
	public static void initialize() {
		if (!hasInitialized) {
			callFuture(new FutureTask<>(() -> {
				if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
					doOnlyOnClient();
				}
			}, null));
		}
		hasInitialized = true;
	}

	public static void callFuture(FutureTask<?> task)
	{
		try
		{
			task.run();
			task.get(); // Forces the exception to be thrown if any
		}
		catch (InterruptedException | ExecutionException e)
		{
			FMLLog.getLogger().fatal("Exception caught executing FutureTask: {}", e.toString(), e);
		}
	}

	@SideOnly(Side.CLIENT)
	private static void doOnlyOnClient() {
		ResourceListener.registerReloadListener();
	}
}
