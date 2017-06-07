package netherchest.common.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class ExtendedItemStackHandler
		implements IItemHandler, IItemHandlerModifiable, INBTSerializable<NBTTagCompound> {

	protected NonNullList<ExtendedItemStack> stacks;

	public ExtendedItemStackHandler() {
		this(1);
	}

	public ExtendedItemStackHandler(int size) {
		stacks = NonNullList.<ExtendedItemStack>withSize(size, new ExtendedItemStack());
	}

	public ExtendedItemStackHandler(NonNullList<ExtendedItemStack> stacks) {
		this.stacks = stacks;
	}

	public void setSize(int size) {
		stacks = NonNullList.<ExtendedItemStack>withSize(size, new ExtendedItemStack());
	}

	@Override
	public int getSlots() {
		return stacks.size();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		validateSlotIndex(slot);
		return stacks.get(slot).getTopStack().copy();
	}

	public ExtendedItemStack getExtendedStackInSlot(int slot) {
		validateSlotIndex(slot);
		return stacks.get(slot);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		validateSlotIndex(slot);
		if (simulate) {
			ExtendedItemStack temp = stacks.get(slot).copy();
			return temp.add(stack);
		} else {
			ExtendedItemStack temp = stacks.get(slot).copy();
			if (!temp.add(stack).isEmpty()) {
				onContentsChanged(slot);
			}
			return stacks.get(slot).add(stack);
		}
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		validateSlotIndex(slot);
		if (amount <= 0 || stacks.get(slot).isEmpty()) {
			return ItemStack.EMPTY;
		}
		if (simulate) {
			ExtendedItemStack temp = stacks.get(slot).copy();
			return temp.take(amount);
		} else {
			onContentsChanged(slot);
			return stacks.get(slot).take(amount);
		}
	}

	@Override
	public int getSlotLimit(int slot) {
		validateSlotIndex(slot);
		return stacks.get(slot).getMaxCount();
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagList nbtTagList = new NBTTagList();
		for (int i = 0; i < stacks.size(); i++) {
			if (!stacks.get(i).isEmpty()) {
				NBTTagCompound itemTag = stacks.get(i).writeToNBT();
				itemTag.setInteger("Slot", i);
				nbtTagList.appendTag(itemTag);
			}
		}
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("Items", nbtTagList);
		nbt.setInteger("Size", stacks.size());
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		setSize(nbt.hasKey("Size", Constants.NBT.TAG_INT) ? nbt.getInteger("Size") : stacks.size());
		NBTTagList tagList = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
			int slot = itemTags.getInteger("Slot");

			if (slot >= 0 && slot < stacks.size()) {
				ExtendedItemStack temp = new ExtendedItemStack();
				temp.readFromNBT(itemTags);
				stacks.set(slot, temp);
			}
		}
		onLoad();
	}

	protected void validateSlotIndex(int slot) {
		if (slot < 0 || slot >= stacks.size())
			throw new RuntimeException("Slot " + slot + " not in valid range - [0," + stacks.size() + ")");
	}

	protected void onLoad() {

	}

	protected void onContentsChanged(int slot) {
		
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		validateSlotIndex(slot);
		ExtendedItemStack temp = new ExtendedItemStack();
		temp.add(stack);
		stacks.set(slot, temp);
		onContentsChanged(slot);
	}
	
	public void setExtendedStackInSlot(int slot, ExtendedItemStack stack) {
		validateSlotIndex(slot);
		stacks.set(slot, stack);
	}

}
