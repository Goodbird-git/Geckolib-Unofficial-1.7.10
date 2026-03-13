package software.bernie.geckolib3.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

/**
 * Server-to-client packet that triggers an animation sync on a registered ISyncable.
 */
public class PacketSyncAnimation implements IMessage, IMessageHandler<PacketSyncAnimation, IMessage> {
	private String key;
	private int id;
	private int state;

	public PacketSyncAnimation() {
	}

	public PacketSyncAnimation(String key, int id, int state) {
		this.key = key;
		this.id = id;
		this.state = state;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.key = ByteBufUtils.readUTF8String(buf);
		this.id = buf.readInt();
		this.state = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, this.key);
		buf.writeInt(this.id);
		buf.writeInt(this.state);
	}

	@Override
	public IMessage onMessage(PacketSyncAnimation message, MessageContext ctx) {
		ISyncable syncable = GeckoLibNetwork.getSyncable(message.key);
		if (syncable != null) {
			syncable.onAnimationSync(message.id, message.state);
		} else {
			System.err.println("[GeckoLib] Syncable on the server is missing on the client for " + message.key);
		}
		return null;
	}
}
