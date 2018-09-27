package netherchest.common.inventory;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public class ExtendedItemStackHandler extends ItemStackHandler {

	public ExtendedItemStackHandler() {
		this(1);
	}

	public ExtendedItemStackHandler(int size) {
		super(size);
	}

	public ExtendedItemStackHandler(NonNullList<ItemStack> stacks) {
		super(stacks);
	}

	@Override
    public int getSlotLimit(int slot) {
        return 64 * 8;
    }
	
	@Override
    protected int getStackLimit(int slot, @Nonnull ItemStack stack)  {
        return Math.min(getSlotLimit(slot), stack.getMaxStackSize() * 8);
    }
	
	@Override
	public void onContentsChanged(int slot) {

    }
	
	@Override
    public void deserializeNBT(NBTTagCompound nbt) {
        setSize(nbt.hasKey("Size", Constants.NBT.TAG_INT) ? nbt.getInteger("Size") : stacks.size());
        NBTTagList tagList = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
            int slot = itemTags.getInteger("Slot");

            if (slot >= 0 && slot < stacks.size()) {
            	if (itemTags.hasKey("StackList", Constants.NBT.TAG_LIST)) { // migrate from old ExtendedItemStack system
        			ItemStack stack = ItemStack.EMPTY;
            		NBTTagList stackTagList = itemTags.getTagList("StackList", Constants.NBT.TAG_COMPOUND);
        			for (int j = 0; j < stackTagList.tagCount(); j++) {
        				NBTTagCompound itemTag = stackTagList.getCompoundTagAt(j);
        				ItemStack temp = new ItemStack(itemTag);
        				if (!temp.isEmpty()) {
        					if (stack.isEmpty()) stack = temp;
        					else stack.grow(temp.getCount());
        				}
        			}
        			if (!stack.isEmpty()) {
        				int count = stack.getCount();
            			count = Math.min(count, getStackLimit(slot, stack));
            			stack.setCount(count);
            			
            			stacks.set(slot, stack);
        			}
        		} else {
        			stacks.set(slot, new ItemStack(itemTags));
        		}
            }
        }
        onLoad();
    }

}
