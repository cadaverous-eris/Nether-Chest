package netherchest.common.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;

public class ExtendedItemStack {
	
	public NonNullList<ItemStack> stacks;

	public ExtendedItemStack() {
		this(8);
	}

	public ExtendedItemStack(int size) {
		stacks = NonNullList.<ItemStack>withSize(size, ItemStack.EMPTY);
	}

	public ExtendedItemStack(ExtendedItemStack eis) {
		stacks = NonNullList.<ItemStack>withSize(eis.stacks.size(), ItemStack.EMPTY);
		for (int i = 0; i < stacks.size(); i++) {
			stacks.set(i, eis.stacks.get(i).copy());
		}
	}

	public ExtendedItemStack copy() {
		return new ExtendedItemStack(this);
	}
	
	protected void setSize(int size) {
		stacks = NonNullList.<ItemStack>withSize(size, ItemStack.EMPTY);
	}

	// returns a copy of the highest nonempty stack in the stack list
	public ItemStack getTopStack() {
		for (int i = stacks.size() - 1; i >= 0; i--) {
			if (!stacks.get(i).isEmpty()) {
				return stacks.get(i).copy();
			}
		}
		return ItemStack.EMPTY;
	}

	public boolean isEmpty() {
		return getTopStack().isEmpty();
	}

	public int getCount() {
		int c = 0;
		for (ItemStack stack : stacks) {
			c += stack.getCount();
		}
		return c;
	}

	public int getMaxCount() {
		if (isEmpty()) {
			return 64 * stacks.size();
		}

		return getTopStack().getMaxStackSize() * stacks.size();
	}

	// inserts the given stack into the stack list if possible, and returns a
	// stack of the remaining items
	public ItemStack add(ItemStack stack) {
		if (!isItemValid(stack)) {
			return stack;
		}

		ItemStack temp = stack.copy();
		for (int i = 0; i < stacks.size(); i++) {
			if (stacks.get(i).isEmpty()) {
				stacks.set(i, temp);
				return ItemStack.EMPTY;
			} else {
				int a = stacks.get(i).getMaxStackSize() - stacks.get(i).getCount();
				a = Math.min(temp.getCount(), a);
				if (a > 0) {
					stacks.get(i).grow(a);
					temp.shrink(a);
				}
			}
			if (temp.isEmpty()) {
				return ItemStack.EMPTY;
			}
		}

		return temp;
	}

	// removes the specified amount of items from the stack list and returns a
	// stack of the removed items
	public ItemStack take(int amount) {
		if (isEmpty()) {
			return ItemStack.EMPTY;
		}

		if (amount <= 0) {
			return ItemStack.EMPTY;
		}

		if (amount > getTopStack().getMaxStackSize()) {
			amount = getTopStack().getMaxStackSize();
		}

		ItemStack temp = getTopStack().copy();
		temp.setCount(0);
		for (int i = stacks.size() - 1; i >= 0 && amount > 0; i--) {
			if (!stacks.get(i).isEmpty()) {
				int a = Math.min(amount, stacks.get(i).getCount());
				temp.grow(a);
				amount -= a;
			}
		}
		shrink(temp.getCount());
		return temp;
	}

	// increases the total item count
	public void grow(int amount) {
		if (isEmpty() || amount <= 0) {
			return;
		}

		int a = Math.min(amount, getMaxCount() - getCount());
		for (int i = 0; i < stacks.size() && a > 0; i++) {
			if (stacks.get(i).isEmpty()) {
				ItemStack temp = getTopStack().copy();
				temp.setCount(Math.min(a, temp.getMaxStackSize()));
				stacks.set(i, temp);
				a -= temp.getCount();
			} else {
				int b = Math.min(stacks.get(i).getMaxStackSize() - stacks.get(i).getCount(), a);
				stacks.get(i).grow(b);
				a -= b;
			}
		}
	}

	// reduces the total itemcount
	public void shrink(int amount) {
		if (isEmpty() || amount <= 0) {
			return;
		}

		for (int i = stacks.size() - 1; i >= 0 && amount > 0; i--) {
			if (!stacks.get(i).isEmpty()) {
				int a = Math.min(stacks.get(i).getCount(), amount);
				stacks.get(i).shrink(a);
				if (stacks.get(i).isEmpty()) {
					stacks.set(i, ItemStack.EMPTY);
				}
				amount -= a;
			}
		}
	}

	public List<ItemStack> getAllStacks() {
		List<ItemStack> list = new ArrayList<ItemStack>();
		for (ItemStack stack : stacks) {
			if (!stack.isEmpty()) {
				list.add(stack);
			}
		}
		return list;
	}

	public boolean isItemValid(ItemStack stack) {
		ItemStack top = getTopStack();
		if (top.isEmpty()) {
			return true;
		}
		if (stack.isEmpty()) {
			return false;
		}
		return (stack.isItemEqual(top) && ItemStack.areItemStackTagsEqual(stack, top));
	}

	public NBTTagCompound writeToNBT() {
		NBTTagList tagList = new NBTTagList();
		for (ItemStack stack : stacks) {
			if (!stack.isEmpty()) {
				NBTTagCompound itemTag = stack.serializeNBT();
				tagList.appendTag(itemTag);
			}
		}
		
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("Size", stacks.size());
		tag.setTag("StackList", tagList);
		return tag;
	}
	
	public void readFromNBT(NBTTagCompound nbt) {
		
		if (nbt.hasKey("Size", 3)) {
			setSize(nbt.getInteger("Size"));
		}
		
		if (nbt.hasKey("StackList", 9)) {
			NBTTagList tagList = nbt.getTagList("StackList", 10);
			for (int i = 0; i < tagList.tagCount() && i < stacks.size(); i++) {
				NBTTagCompound itemTag = tagList.getCompoundTagAt(i);
				ItemStack temp = new ItemStack(itemTag);
				stacks.set(i, temp);
			}
		}
		
	}

}
