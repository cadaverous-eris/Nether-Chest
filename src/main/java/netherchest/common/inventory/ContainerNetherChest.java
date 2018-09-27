package netherchest.common.inventory;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import netherchest.common.tileentity.TileEntityNetherChest;

public class ContainerNetherChest extends Container {

	TileEntityNetherChest te;

	public ContainerNetherChest(TileEntityNetherChest te, EntityPlayer player) {
		this.te = te;
		te.openInventory(player);
		addOwnSlots();
		addPlayerSlots(player.inventory);
	}

	public TileEntityNetherChest getTile() {
		return this.te;
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		this.getTile().closeInventory(playerIn);
	}

	protected void addPlayerSlots(IInventory playerinventory) {
		for (int row = 0; row < 3; ++row) {
			for (int col = 0; col < 9; ++col) {
				int x = 8 + col * 18;
				int y = row * 18 + 84;
				this.addSlotToContainer(new Slot(playerinventory, col + row * 9 + 9, x, y));
			}
		}

		for (int row = 0; row < 9; ++row) {
			int x = 8 + row * 18;
			int y = 142;
			this.addSlotToContainer(new Slot(playerinventory, row, x, y));
		}
	}

	public void addOwnSlots() {
		ExtendedItemStackHandler handler = this.te.getHandler();
		int slotIndex = 0;
		for (int row = 0; row < 3; ++row) {
			for (int col = 0; col < 9; ++col) {
				int x = 8 + col * 18;
				int y = row * 18 + 17 + 1;
				this.addSlotToContainer(new SlotExtended(handler, slotIndex, x, y));
				slotIndex++;
			}
		}
	}

	@Nullable
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = (Slot) this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (index < 3 * 9) {
				if (!this.mergeItemStack(itemstack1, 3 * 9, this.inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(itemstack1, 0, 3 * 9, false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
		}

		return itemstack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

}
