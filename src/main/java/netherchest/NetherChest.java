package netherchest;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import netherchest.common.CommonProxy;
import netherchest.common.Content;

@Mod(modid = NetherChest.MODID, name = NetherChest.NAME, version = NetherChest.VERSION, useMetadata = true)
public class NetherChest {
	
	public static final String MODID = "netherchest";
	public static final String NAME = "Nether Chest";
	public static final String VERSION = "0.2.2";
	
	public NetherChest() {
		//MinecraftForge.EVENT_BUS.register(Content.class);
	}
	
	@SidedProxy(clientSide = "netherchest.client.ClientProxy", serverSide = "netherchest.common.CommonProxy")
	public static CommonProxy proxy;
	
	@Mod.Instance
	public static NetherChest instance;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}

}
