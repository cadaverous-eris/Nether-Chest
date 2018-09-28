package netherchest.common.network;

import java.io.IOException;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SPacketSetSlotExtended extends SPacketSetSlot {
	
	private int windowId;
    private int slot;
    private ItemStack item = ItemStack.EMPTY;

    public SPacketSetSlotExtended() {
    	
    }

    public SPacketSetSlotExtended(int windowIdIn, int slotIn, ItemStack itemIn) {
        this.windowId = windowIdIn;
        this.slot = slotIn;
        this.item = itemIn.copy();
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.windowId = buf.readByte();
        this.slot = buf.readShort();
        this.item = NetworkUtils.readExtendedItemStack(buf);
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeByte(this.windowId);
        buf.writeShort(this.slot);
        NetworkUtils.writeExtendedItemStack(buf, this.item);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getWindowId() {
        return this.windowId;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getSlot() {
        return this.slot;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getStack() {
        return this.item;
    }

}
