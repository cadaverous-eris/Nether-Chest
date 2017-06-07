package netherchest.common;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import netherchest.NetherChest;
import netherchest.common.blocks.BlockNetherChest;
import netherchest.common.tileentity.TileEntityNetherChest;

public class Content {
	
	private static int nextEntityId = 0;
	
	public static BlockNetherChest NETHER_CHEST;
	
	public static void init() {
		NETHER_CHEST = new BlockNetherChest();
		
		GameRegistry.registerTileEntity(TileEntityNetherChest.class, NetherChest.MODID + "tileEntityNetherChest");
		
		initRecipes();
	}
	
	private static void initRecipes() {

		Item item = Item.getByNameOrId(Config.RECIPE);
		
		if (item == null) {
			throw new UnsupportedOperationException();
		}
		
		GameRegistry.addShapedRecipe(new ItemStack(NETHER_CHEST), "INI", "NCN", "INI", 'I', new ItemStack(Items.IRON_INGOT), 'N', new ItemStack(Blocks.NETHER_BRICK), 'C', new ItemStack(item));
		
	}
	
	@SideOnly(Side.CLIENT)
	public static void initClient() {
		NETHER_CHEST.initModel();
	}

}
