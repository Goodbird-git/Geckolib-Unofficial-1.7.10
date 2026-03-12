package software.bernie.geckolib3.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.file.ItemDisplayFile;
import software.bernie.geckolib3.resource.GeckoLibCache;

import java.util.HashMap;

public class PacketRemoveItemDisplay implements IMessage, IMessageHandler<PacketRemoveItemDisplay, IMessage> {
    private String resLoc;

    public PacketRemoveItemDisplay() {
    }

    public PacketRemoveItemDisplay(String resLoc) {
        this.resLoc = resLoc;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        resLoc = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, resLoc);
    }

    @Override
    public IMessage onMessage(PacketRemoveItemDisplay message, MessageContext ctx) {
        HashMap<ResourceLocation, ItemDisplayFile> displays = GeckoLibCache.getInstance().getItemDisplays();
        ResourceLocation resourceLocation = new ResourceLocation("custom", message.resLoc);
        displays.remove(resourceLocation);
        return null;
    }
}
