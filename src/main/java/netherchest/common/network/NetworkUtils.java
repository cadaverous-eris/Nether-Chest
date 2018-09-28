package netherchest.common.network;

import java.io.IOException;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

public class NetworkUtils {
	
	public static void writeExtendedItemStack(PacketBuffer buf, ItemStack stack) {
		if (stack.isEmpty()) {
            buf.writeShort(-1);
        } else {
            buf.writeShort(Item.getIdFromItem(stack.getItem()));
            buf.writeShort(stack.getCount());
            buf.writeShort(stack.getMetadata());
            NBTTagCompound nbttagcompound = null;

            if (stack.getItem().isDamageable() || stack.getItem().getShareTag()) {
                nbttagcompound = stack.getItem().getNBTShareTag(stack);
            }

            buf.writeCompoundTag(nbttagcompound);
        }
	}
	
	public static void writeExtendedItemStackFromClientToServer(PacketBuffer buf, ItemStack stack) {
		if (stack.isEmpty()) {
            buf.writeShort(-1);
        } else {
            buf.writeShort(Item.getIdFromItem(stack.getItem()));
            buf.writeShort(stack.getCount());
            buf.writeShort(stack.getMetadata());
            NBTTagCompound nbttagcompound = null;

            if (stack.getItem().isDamageable() || stack.getItem().getShareTag()) {
            	nbttagcompound = stack.getTagCompound();
            }

            buf.writeCompoundTag(nbttagcompound);
        }
	}
	
	public static ItemStack readExtendedItemStack(PacketBuffer buf) throws IOException {
		int i = buf.readShort();

        if (i < 0) {
            return ItemStack.EMPTY;
        } else {
            int j = buf.readShort();
            int k = buf.readShort();
            ItemStack itemstack = new ItemStack(Item.getItemById(i), j, k);
            itemstack.setTagCompound(buf.readCompoundTag());
            return itemstack;
        }
	}

}
