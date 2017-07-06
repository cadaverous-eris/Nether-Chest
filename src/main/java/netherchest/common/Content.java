package netherchest.common;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import netherchest.NetherChest;
import netherchest.client.renderers.TileEntityNetherChestRenderer;
import netherchest.common.blocks.BlockNetherChest;
import netherchest.common.tileentity.TileEntityNetherChest;

@ObjectHolder("netherchest")
@Mod.EventBusSubscriber(modid = NetherChest.MODID)
public class Content {

	@ObjectHolder("nether_chest")
	public static final Block NETHER_CHEST = null;
	
	@ObjectHolder("nether_chest")
	public static final Item NETHER_CHEST_ITEM = null;

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		event.getRegistry().register(new BlockNetherChest());
		
		GameRegistry.registerTileEntity(TileEntityNetherChest.class, NetherChest.MODID + ":tileentitynetherchest");
	}
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(new ItemBlock(NETHER_CHEST).setRegistryName(NETHER_CHEST.getRegistryName()));
	}

	@SubscribeEvent
	public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {

		Item item = Item.getByNameOrId(Config.RECIPE);

		if (item == null) {
			throw new UnsupportedOperationException();
		}

		GameRegistry.addShapedRecipe(new ResourceLocation(NetherChest.MODID, "nether_chest_recipe"), null, new ItemStack(NETHER_CHEST), "INI", "NCN", "INI", 'I',
				new ItemStack(Items.IRON_INGOT), 'N', new ItemStack(Blocks.NETHER_BRICK), 'C', new ItemStack(item));

	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
    public static void setupModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(NETHER_CHEST_ITEM, 0,
				new ModelResourceLocation(NETHER_CHEST_ITEM.getRegistryName(), "inventory"));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityNetherChest.class, new TileEntityNetherChestRenderer());
	}

}
