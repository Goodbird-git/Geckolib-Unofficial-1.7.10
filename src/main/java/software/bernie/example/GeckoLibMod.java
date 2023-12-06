/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.example;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import software.bernie.example.block.tile.BotariumTileEntity;
import software.bernie.example.block.tile.FertilizerTileEntity;
import software.bernie.example.client.renderer.armor.PotatoArmorRenderer;
import software.bernie.example.client.renderer.entity.*;
import software.bernie.example.client.renderer.item.JackInTheBoxRenderer;
import software.bernie.example.client.renderer.tile.BotariumTileRenderer;
import software.bernie.example.client.renderer.tile.FertilizerTileRenderer;
import software.bernie.example.entity.*;
import software.bernie.example.item.PotatoArmorItem;
import software.bernie.example.registry.BlockRegistry;
import software.bernie.example.registry.ItemRegistry;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.handler.PlayerLoginHandler;
import software.bernie.geckolib3.network.NetworkHandler;
import software.bernie.geckolib3.particles.BedrockLibrary;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;
import software.bernie.geckolib3.renderers.geo.GeoReplacedEntityRenderer;
import software.bernie.geckolib3.renderers.geo.RenderBlockItem;
import software.bernie.geckolib3.resource.AnimationLibrary;
import software.bernie.geckolib3.resource.ModelLibrary;

import java.io.File;

@Mod(modid = GeckoLib.ModID, version = GeckoLib.VERSION)
public class GeckoLibMod {
	public static boolean DISABLE_IN_DEV = false;
	private static CreativeTabs geckolibItemGroup;
	private boolean deobfuscatedEnvironment;
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
		deobfuscatedEnvironment = true;//(Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
		if (deobfuscatedEnvironment && !DISABLE_IN_DEV) {
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
		if (deobfuscatedEnvironment && !DISABLE_IN_DEV) {
			RenderingRegistry.registerEntityRenderingHandler(GeoExampleEntityLayer.class,
					new LERenderer());
			RenderingRegistry.registerEntityRenderingHandler(GeoExampleEntity.class, new ExampleGeoRenderer());
			RenderingRegistry.registerEntityRenderingHandler(BikeEntity.class, new BikeGeoRenderer());
			RenderingRegistry.registerEntityRenderingHandler(GeoNpcEntity.class, new GeoNpcRenderer());

			GeoArmorRenderer.registerArmorRenderer(PotatoArmorItem.class, new PotatoArmorRenderer());

			bindRender(BlockRegistry.BOTARIUM_BLOCK, new BotariumTileEntity(), new BotariumTileRenderer());
			bindRender(BlockRegistry.FERTILIZER_BLOCK, new FertilizerTileEntity(), new FertilizerTileRenderer());
			//bindRender(BlockRegistry.DIAGONAL_BLOCK, new DiagonalTileEntity(), new DiagonalTileRenderer());

			MinecraftForgeClient.registerItemRenderer(ItemRegistry.JACK_IN_THE_BOX,new JackInTheBoxRenderer());
		}
	}

	public void bindRender(Block block, TileEntity tile, TileEntitySpecialRenderer tesr){
		ClientRegistry.bindTileEntitySpecialRenderer(tile.getClass(), tesr);
		Item blockItem = ItemBlock.getItemFromBlock(block);
		MinecraftForgeClient.registerItemRenderer(blockItem,new RenderBlockItem(tesr, tile));
	}

	@SideOnly(Side.CLIENT)
	@Mod.EventHandler
	public void registerReplacedRenderers(FMLInitializationEvent event) {
		if (deobfuscatedEnvironment && !DISABLE_IN_DEV) {
			GeckoLib.initialize();

			ReplacedCreeperRenderer creeperRenderer = new ReplacedCreeperRenderer();
			RenderManager.instance.entityRenderMap.put(EntityCreeper.class, creeperRenderer);
			GeoReplacedEntityRenderer.registerReplacedEntity(ReplacedCreeperEntity.class, creeperRenderer);
		}
	}
}
