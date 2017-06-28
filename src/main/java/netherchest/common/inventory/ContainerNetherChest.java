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

			if (slot instanceof SlotExtended) {
				if (((SlotExtended) slot).getExtendedStack().isEmpty()) {
					slot.putStack(ItemStack.EMPTY);
				} else if (itemstack1.isEmpty()) {
					((SlotExtended) slot).getExtendedStack().shrink(itemstack.getCount());
				}
			} else if (itemstack1.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
		}

		return itemstack;
	}

	@Override
	protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
		boolean flag = false;
		int i = startIndex;

		if (reverseDirection) {
			i = endIndex - 1;
		}

		// if (stack.isStackable()) {
		while (!stack.isEmpty()) {
			if (reverseDirection) {
				if (i < startIndex) {
					break;
				}
			} else if (i >= endIndex) {
				break;
			}

			Slot slot = (Slot) this.inventorySlots.get(i);
			ItemStack itemstack = slot.getStack();

			if (mergeFilledSlot(stack, slot)) {
				flag = true;
			}

			if (reverseDirection) {
				--i;
			} else {
				++i;
			}
		}
		// }

		if (!stack.isEmpty()) {
			if (reverseDirection) {
				i = endIndex - 1;
			} else {
				i = startIndex;
			}

			while (true) {
				if (reverseDirection) {
					if (i < startIndex) {
						break;
					}
				} else if (i >= endIndex) {
					break;
				}

				Slot slot1 = (Slot) this.inventorySlots.get(i);
				ItemStack itemstack1 = slot1.getStack();

				if (mergeEmptySlot(stack, slot1)) {
					flag = true;
					break;
				}

				if (reverseDirection) {
					--i;
				} else {
					++i;
				}
			}
		}

		return flag;
	}

	protected boolean mergeFilledSlot(ItemStack stack, Slot slot) {
		boolean flag = false;
		ItemStack itemstack = slot.getStack();

		if (!(slot instanceof SlotExtended)) {
			if (!itemstack.isEmpty() && itemstack.getItem() == stack.getItem()
					&& (!stack.getHasSubtypes() || stack.getMetadata() == itemstack.getMetadata())
					&& ItemStack.areItemStackTagsEqual(stack, itemstack)) {
				int j = itemstack.getCount() + stack.getCount();
				int maxSize = Math.min(slot.getSlotStackLimit(), stack.getMaxStackSize());

				if (j <= maxSize) {
					stack.setCount(0);
					itemstack.setCount(j);
					slot.onSlotChanged();
					flag = true;
				} else if (itemstack.getCount() < maxSize) {
					stack.shrink(maxSize - itemstack.getCount());
					itemstack.setCount(maxSize);
					slot.onSlotChanged();
					flag = true;
				}
			}
		} else {
			SlotExtended slotExt = (SlotExtended) slot;
			ExtendedItemStack itemstackExt = slotExt.getExtendedStack();

			if (!itemstackExt.isEmpty() && itemstack.getItem() == stack.getItem()
					&& (stack.getMetadata() == itemstack.getMetadata())
					&& ItemStack.areItemStackTagsEqual(stack, itemstack)) {
				int j = itemstackExt.getCount() + stack.getCount();
				int maxSize = Math.min(slotExt.getSlotStackLimit(), itemstackExt.getMaxCount());

				if (j <= maxSize) {
					itemstackExt.grow(stack.getCount());
					stack.setCount(0);
					slot.onSlotChanged();
					flag = true;
				} else if (itemstackExt.getCount() < maxSize) {
					stack.shrink(maxSize - itemstackExt.getCount());
					itemstackExt.grow(itemstackExt.getMaxCount() - itemstackExt.getCount());
					slot.onSlotChanged();
					flag = true;
				}
			}
		}
		return flag;
	}

	protected boolean mergeEmptySlot(ItemStack stack, Slot slot1) {
		boolean flag = false;
		ItemStack itemstack1 = slot1.getStack();

		if (!(slot1 instanceof SlotExtended)) {
			if (itemstack1.isEmpty() && slot1.isItemValid(stack)) {
				if (stack.getCount() > slot1.getSlotStackLimit()) {
					slot1.putStack(stack.splitStack(slot1.getSlotStackLimit()));
				} else {
					slot1.putStack(stack.splitStack(stack.getCount()));
				}

				slot1.onSlotChanged();
				flag = true;
			}
		} else {
			SlotExtended slotExt = (SlotExtended) slot1;
			ExtendedItemStack itemstackExt = slotExt.getExtendedStack();

			if (itemstackExt.isEmpty() && slotExt.isItemValid(stack)) {
				if (stack.getCount() > slotExt.getSlotStackLimit()) {
					slotExt.putStack(stack.splitStack(slot1.getSlotStackLimit()));
					stack.shrink(slotExt.getSlotStackLimit());
				} else {
					slot1.putStack(stack);
					stack.setCount(0);
				}

				slot1.onSlotChanged();
				flag = true;
			}
		}
		return flag;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
		ItemStack itemstack = ItemStack.EMPTY;
		InventoryPlayer inventoryplayer = player.inventory;

		if (slotId >= 0 && slotId < this.inventorySlots.size()) {
			Slot slot = this.inventorySlots.get(slotId);
			if (slot != null) {
				if (slot instanceof SlotExtended) {
					if (clickTypeIn == ClickType.QUICK_CRAFT) {
						// return ItemStack.EMPTY;
						return this.slotClick(slotId, dragType, ClickType.PICKUP, player);
					}

					if ((clickTypeIn == ClickType.PICKUP || clickTypeIn == ClickType.QUICK_MOVE)
							&& (dragType == 0 || dragType == 1)) {
						ItemStack itemstack11 = slot.getStack();
						ItemStack itemstack13 = inventoryplayer.getItemStack();

						if (!itemstack11.isEmpty() && !itemstack13.isEmpty()) {
							if (!itemstack11.isItemEqual(itemstack13)
									|| !ItemStack.areItemStackTagsEqual(itemstack11, itemstack13)) {
								return ItemStack.EMPTY;
							}
						}

						if (!itemstack11.isEmpty() && slot.canTakeStack(player)) {

							if (!itemstack13.isEmpty() && slot.isItemValid(itemstack13)) {
								if (itemstack11.getItem() == itemstack13.getItem()
										&& itemstack11.getMetadata() == itemstack13.getMetadata()
										&& ItemStack.areItemStackTagsEqual(itemstack11, itemstack13)) {
									int j2 = dragType == 0 ? itemstack13.getCount() : 1;

									if (j2 > slot.getItemStackLimit(itemstack13)
											- ((SlotExtended) slot).getExtendedStack().getCount()) {
										j2 = slot.getItemStackLimit(itemstack13)
												- ((SlotExtended) slot).getExtendedStack().getCount();
									}

									if (j2 > ((SlotExtended) slot).getExtendedStack().getMaxCount()
											- itemstack13.getCount()) {
										j2 = ((SlotExtended) slot).getExtendedStack().getMaxCount()
												- itemstack13.getCount();
									}

									if (j2 < 0) {
										j2 = 0;
									}

									itemstack13.shrink(j2);
									((SlotExtended) slot).getExtendedStack().grow(j2);
									((SlotExtended) slot).onSlotChanged();
									return ItemStack.EMPTY;
								}
							}
						}
					}
				}
			}
		}
		return super.slotClick(slotId, dragType, clickTypeIn, player);
	}

}
