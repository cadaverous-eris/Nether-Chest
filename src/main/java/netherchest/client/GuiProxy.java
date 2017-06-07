package netherchest.client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import netherchest.client.gui.GuiNetherChest;
import netherchest.common.blocks.BlockNetherChest;
import netherchest.common.inventory.ContainerNetherChest;
import netherchest.common.tileentity.TileEntityNetherChest;

public class GuiProxy implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		TileEntity te = world.getTileEntity(pos);
		if (ID == BlockNetherChest.GUI_ID && te instanceof TileEntityNetherChest) {
			return new ContainerNetherChest((TileEntityNetherChest) te, player);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		TileEntity te = world.getTileEntity(pos);
		if (ID == BlockNetherChest.GUI_ID && te instanceof TileEntityNetherChest) {
			return new GuiNetherChest(new ContainerNetherChest((TileEntityNetherChest) te, player), player.inventory);
		}
		return null;
	}

}
