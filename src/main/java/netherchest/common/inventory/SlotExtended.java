package netherchest.common.inventory;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

public class SlotExtended extends Slot {

	private static IInventory emptyInventory = new InventoryBasic("[Null]", true, 0);
	private final ExtendedItemStackHandler itemHandler;
	private final int index;

	public SlotExtended(ExtendedItemStackHandler itemHandler, int index, int xPosition, int yPosition) {
		super(emptyInventory, index, xPosition, yPosition);
		this.itemHandler = itemHandler;
		this.index = index;
	}

	@Override
	public boolean isItemValid(@Nonnull ItemStack stack) {
		if (stack.isEmpty())
			return false;

		ExtendedItemStackHandler handler = this.getItemHandler();
		ItemStack remainder;
		ExtendedItemStack currentStack = handler.getExtendedStackInSlot(index);
		handler.setExtendedStackInSlot(index, new ExtendedItemStack());
		remainder = handler.insertItem(index, stack, true);
		handler.setExtendedStackInSlot(index, currentStack);
		boolean v = remainder.isEmpty() || remainder.getCount() < stack.getCount();
		return v;
	}

	@Override
	@Nonnull
	public ItemStack getStack() {
		return this.getItemHandler().getStackInSlot(index);
	}

	@Nonnull
	public ExtendedItemStack getExtendedStack() {
		return this.getItemHandler().getExtendedStackInSlot(index);
	}

	@Override
	public void putStack(@Nonnull ItemStack stack) {
		this.getItemHandler().setStackInSlot(index, stack);
		this.onSlotChanged();
	}

	@Override
	public void onSlotChange(@Nonnull ItemStack p_75220_1_, @Nonnull ItemStack p_75220_2_) {
		getItemHandler().onContentsChanged(index);
	}

	@Override
	public int getSlotStackLimit() {
		return this.itemHandler.getSlotLimit(this.index);
	}

	@Override
	public int getItemStackLimit(@Nonnull ItemStack stack) {
		ItemStack temp = stack.copy();
		temp.setCount(1);
		ExtendedItemStackHandler handler = this.getItemHandler();
		ExtendedItemStack currentStack = handler.getExtendedStackInSlot(index);

		if (handler.insertItem(index, temp, true).isEmpty()) {
			//return currentStack.getMaxCount();
			int t = Math.min(currentStack.getMaxCount() - currentStack.getCount(), stack.getMaxStackSize());
			t = currentStack.getMaxCount() - currentStack.getCount();
			int ret = currentStack.getTopStack().getCount() + t;
			ret = currentStack.getMaxCount();
			//System.out.println(ret);
			return ret;
			//return currentStack.getTopStack().getMaxStackSize();
		} else {
			return currentStack.getTopStack().getCount();
		}
	}

	@Override
	public boolean canTakeStack(EntityPlayer playerIn) {
		return !this.getItemHandler().extractItem(index, 1, true).isEmpty();
	}

	@Override
	@Nonnull
	public ItemStack decrStackSize(int amount) {
		return this.getItemHandler().extractItem(index, amount, false);
	}

	public ExtendedItemStackHandler getItemHandler() {
		return itemHandler;
	}

	@Override
	public boolean isSameInventory(Slot other) {
		return other instanceof SlotExtended && ((SlotExtended) other).getItemHandler() == this.itemHandler;
	}

}
