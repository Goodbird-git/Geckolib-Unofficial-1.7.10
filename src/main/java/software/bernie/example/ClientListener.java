package software.bernie.example;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;
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
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;
import software.bernie.geckolib3.renderers.geo.GeoReplacedEntityRenderer;
import software.bernie.geckolib3.renderers.geo.RenderBlockItem;

public class ClientListener {
    @SideOnly(Side.CLIENT)
    public static void registerRenderers(FMLInitializationEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(GeoExampleEntityLayer.class,
            new LERenderer());
        RenderingRegistry.registerEntityRenderingHandler(GeoExampleEntity.class, new ExampleGeoRenderer());
        RenderingRegistry.registerEntityRenderingHandler(BikeEntity.class, new BikeGeoRenderer());
        RenderingRegistry.registerEntityRenderingHandler(GeoNpcEntity.class, new GeoNpcRenderer());

        GeoArmorRenderer.registerArmorRenderer(PotatoArmorItem.class, new PotatoArmorRenderer());

        bindRender(BlockRegistry.BOTARIUM_BLOCK, new BotariumTileEntity(), new BotariumTileRenderer());
        bindRender(BlockRegistry.FERTILIZER_BLOCK, new FertilizerTileEntity(), new FertilizerTileRenderer());
        //bindRender(BlockRegistry.DIAGONAL_BLOCK, new DiagonalTileEntity(), new DiagonalTileRenderer());

        MinecraftForgeClient.registerItemRenderer(ItemRegistry.JACK_IN_THE_BOX, new JackInTheBoxRenderer());
    }

    public static void bindRender(Block block, TileEntity tile, TileEntitySpecialRenderer tesr){
        ClientRegistry.bindTileEntitySpecialRenderer(tile.getClass(), tesr);
        Item blockItem = ItemBlock.getItemFromBlock(block);
        MinecraftForgeClient.registerItemRenderer(blockItem,new RenderBlockItem(tesr, tile));
    }

    @SideOnly(Side.CLIENT)
    public static void registerReplacedRenderers(FMLInitializationEvent event) {
        ReplacedCreeperRenderer creeperRenderer = new ReplacedCreeperRenderer();
        RenderManager.instance.entityRenderMap.put(EntityCreeper.class, creeperRenderer);
        GeoReplacedEntityRenderer.registerReplacedEntity(ReplacedCreeperEntity.class, creeperRenderer);
    }
}
