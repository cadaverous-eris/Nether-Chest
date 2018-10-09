package netherchest.common.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import netherchest.NetherChest;

public class PacketHandler {
	
	public static SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(NetherChest.MODID);
	
	private static int id = 0;
	
	public static void registerMessages() {
		INSTANCE.registerMessage(MessageSyncExtendedSlotContents.Handler.class, MessageSyncExtendedSlotContents.class, id++, Side.CLIENT);
		
		//INSTANCE.registerMessage(MessageClickWindowExtended.Handler.class, MessageClickWindowExtended.class, id++, Side.SERVER);
	}

}
