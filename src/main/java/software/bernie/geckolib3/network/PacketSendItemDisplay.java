package software.bernie.geckolib3.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.file.ItemDisplayFile;
import software.bernie.geckolib3.resource.GeckoLibCache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class PacketSendItemDisplay implements IMessage, IMessageHandler<PacketSendItemDisplay, IMessage> {
    private ItemDisplayFile displayFile;
    private String resLoc;

    public PacketSendItemDisplay() {
    }

    public PacketSendItemDisplay(ItemDisplayFile displayFile, String resLoc) {
        this.displayFile = displayFile;
        this.resLoc = resLoc;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        try {
            resLoc = ByteBufUtils.readUTF8String(buf);
            byte[] bytes = new byte[buf.readInt()];
            buf.readBytes(bytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
            ItemDisplayFile file = (ItemDisplayFile) objectInputStream.readObject();
            objectInputStream.close();
            this.displayFile = file;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(displayFile);
            objectOutputStream.flush();
            objectOutputStream.close();
            byte[] bytes = byteArrayOutputStream.toByteArray();
            ByteBufUtils.writeUTF8String(buf, resLoc);
            buf.writeInt(bytes.length);
            buf.writeBytes(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IMessage onMessage(PacketSendItemDisplay message, MessageContext ctx) {
        if (message.displayFile != null) {
            HashMap<ResourceLocation, ItemDisplayFile> displays = GeckoLibCache.getInstance().getItemDisplays();
            ResourceLocation resourceLocation = new ResourceLocation("custom", message.resLoc);
            displays.remove(resourceLocation);
            displays.put(resourceLocation, message.displayFile);
        }
        return null;
    }
}
