/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.example;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import software.bernie.example.registry.ItemRegistry;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.handler.PlayerLoginHandler;
import software.bernie.geckolib3.network.NetworkHandler;
import software.bernie.geckolib3.particles.BedrockLibrary;
import software.bernie.geckolib3.resource.AnimationLibrary;
import software.bernie.geckolib3.resource.ModelLibrary;

import java.io.File;

@Mod(modid = GeckoLib.ModID, version = GeckoLib.VERSION)
public class GeckoLibMod {
	public static boolean DISABLE_IN_DEV = false;
	private static CreativeTabs geckolibItemGroup;
	private boolean deobfuscatedEnvironment = true;
	public static GeckoLibMod instance;
	public static BedrockLibrary particleLibraryInstance;
	public static ModelLibrary modelLibraryInstance;
	public static AnimationLibrary animationLibraryInstance;
	public static CreativeTabs getGeckolibItemGroup() {
		if (geckolibItemGroup == null) {
			geckolibItemGroup = new CreativeTabs(CreativeTabs.getNextID(), "geckolib_examples") {
				@Override
				public Item getTabIconItem() {
					return (ItemRegistry.JACK_IN_THE_BOX);
				}
			};
		}

		return geckolibItemGroup;
	}

	public GeckoLibMod() {
		instance = this;
		if (!DISABLE_IN_DEV) {
			MinecraftForge.EVENT_BUS.register(new CommonListener());
			FMLCommonHandler.instance().bus().register(new PlayerLoginHandler());
			MinecraftForge.EVENT_BUS.register(new PlayerLoginHandler());
		}
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		CommonListener.onRegisterBlocks();
		CommonListener.onRegisterItems();
		CommonListener.onRegisterEntities();
		particleLibraryInstance = new BedrockLibrary(new File("./particle"));
		particleLibraryInstance.reload();
		modelLibraryInstance = new ModelLibrary(new File("./models"));
		modelLibraryInstance.reload(false);
		animationLibraryInstance = new AnimationLibrary(new File("./animations"));
		animationLibraryInstance.reload(false);
		NetworkHandler.init();
	}

	@SideOnly(Side.CLIENT)
	@Mod.EventHandler
	public void registerRenderers(FMLInitializationEvent event) {
        if(event.getSide()==Side.CLIENT){
            ClientListener.registerReplacedRenderers(event);
            ClientListener.registerRenderers(event);
        }
	}


}
