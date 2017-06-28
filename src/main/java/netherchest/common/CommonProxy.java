package netherchest.common;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import netherchest.NetherChest;
import netherchest.client.GuiProxy;
import netherchest.common.blocks.BlockNetherChest;

public class CommonProxy {
	
	public static Configuration config;
	
	public void preInit(FMLPreInitializationEvent event) {
		
		File directory = event.getModConfigurationDirectory();
        config = new Configuration(new File(directory.getPath(), "netherchest.cfg"));
        Config.readConfig();
		
	}
	
	public void init(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(NetherChest.instance, new GuiProxy());
	}
	
	public void postInit(FMLPostInitializationEvent event) {
		
		if (config.hasChanged()) {
            config.save();
        }
		
	}

}
