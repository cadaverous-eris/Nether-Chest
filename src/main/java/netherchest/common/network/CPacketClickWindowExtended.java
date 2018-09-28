package netherchest.common.network;

import java.io.IOException;
import java.lang.reflect.Field;

import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.server.SPacketConfirmTransaction;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CPacketClickWindowExtended extends CPacketClickWindow {

	/** The id of the window which was clicked. 0 for player inventory. */
    private int windowId;
    /** Id of the clicked slot */
    private int slotId;
    /** Button used */
    private int packedClickData;
    /** A unique number for the action, used for transaction handling */
    private short actionNumber;
    /** The item stack present in the slot */
    private ItemStack clickedItem = ItemStack.EMPTY;
    /** Inventory operation mode */
    private ClickType mode;

    public CPacketClickWindowExtended() {
    	
    }

    @SideOnly(Side.CLIENT)
    public CPacketClickWindowExtended(int windowIdIn, int slotIdIn, int usedButtonIn, ClickType modeIn, ItemStack clickedItemIn, short actionNumberIn) {
        this.windowId = windowIdIn;
        this.slotId = slotIdIn;
        this.packedClickData = usedButtonIn;
        this.clickedItem = clickedItemIn.copy();
        this.actionNumber = actionNumberIn;
        this.mode = modeIn;
    }
    
    @Override
    public void processPacket(INetHandlerPlayServer inethandler) {
        if (inethandler instanceof NetHandlerPlayServer) {
        	NetHandlerPlayServer handler = (NetHandlerPlayServer) inethandler;
        	
        	PacketThreadUtil.checkThreadAndEnqueue(this, handler, handler.player.getServerWorld());
            handler.player.markPlayerActive();

            if (handler.player.openContainer.windowId == this.getWindowId() && handler.player.openContainer.getCanCraft(handler.player)) {
                if (handler.player.isSpectator()) {
                    NonNullList<ItemStack> nonnulllist = NonNullList.<ItemStack>create();

                    for (int i = 0; i < handler.player.openContainer.inventorySlots.size(); ++i) {
                        nonnulllist.add(((Slot)handler.player.openContainer.inventorySlots.get(i)).getStack());
                    }

                    handler.player.connection.sendPacket(new SPacketWindowItemsExtended(handler.player.openContainer.windowId, nonnulllist));
                    handler.player.connection.sendPacket(new SPacketSetSlotExtended(-1, -1, handler.player.inventory.getItemStack()));
                } else {
                    ItemStack itemstack2 = handler.player.openContainer.slotClick(this.getSlotId(), this.getUsedButton(), this.getClickType(), handler.player);

                    if (ItemStack.areItemStacksEqualUsingNBTShareTag(this.getClickedItem(), itemstack2)) {
                    	handler.player.connection.sendPacket(new SPacketConfirmTransaction(this.getWindowId(), this.getActionNumber(), true));
                    	handler.player.isChangingQuantityOnly = true;
                    	handler.player.openContainer.detectAndSendChanges();
                    	handler.player.updateHeldItem();
                    	handler.player.isChangingQuantityOnly = false;
                    } else {
                    	IntHashMap<Short> pendingTransactions = getPendingTransactions(handler);
                    	if (pendingTransactions != null) {
                    		pendingTransactions.addKey(handler.player.openContainer.windowId, Short.valueOf(this.getActionNumber()));
                    	}
                        handler.player.connection.sendPacket(new SPacketConfirmTransaction(this.getWindowId(), this.getActionNumber(), false));
                        handler.player.openContainer.setCanCraft(handler.player, false);
                        NonNullList<ItemStack> nonnulllist1 = NonNullList.<ItemStack>create();

                        for (int j = 0; j < handler.player.openContainer.inventorySlots.size(); ++j) {
                            ItemStack itemstack = ((Slot)handler.player.openContainer.inventorySlots.get(j)).getStack();
                            ItemStack itemstack1 = itemstack.isEmpty() ? ItemStack.EMPTY : itemstack;
                            nonnulllist1.add(itemstack1);
                        }

                        handler.player.connection.sendPacket(new SPacketWindowItemsExtended(handler.player.openContainer.windowId, nonnulllist1));
                        handler.player.connection.sendPacket(new SPacketSetSlotExtended(-1, -1, handler.player.inventory.getItemStack()));
                    }
                }
            }
        } else {
        	inethandler.processClickWindow(this);
        }
    }
    
    static IntHashMap<Short> getPendingTransactions(NetHandlerPlayServer handler) {
    	for (Field field : NetHandlerPlayServer.class.getDeclaredFields()) {
    		field.setAccessible(true);
    		if (field.getType().isAssignableFrom(IntHashMap.class)) {
    			try {
					return (IntHashMap<Short>) field.get(handler);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
    		}
    	}
    	
    	return null;
    }
    
    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.windowId = buf.readByte();
        this.slotId = buf.readShort();
        this.packedClickData = buf.readByte();
        this.actionNumber = buf.readShort();
        this.mode = (ClickType)buf.readEnumValue(ClickType.class);
        this.clickedItem = NetworkUtils.readExtendedItemStack(buf);
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeByte(this.windowId);
        buf.writeShort(this.slotId);
        buf.writeByte(this.packedClickData);
        buf.writeShort(this.actionNumber);
        buf.writeEnumValue(this.mode);
        NetworkUtils.writeExtendedItemStackFromClientToServer(buf, this.clickedItem);
    }

    @Override
    public int getWindowId() {
        return this.windowId;
    }

    @Override
    public int getSlotId() {
        return this.slotId;
    }

    @Override
    public int getUsedButton() {
        return this.packedClickData;
    }

    @Override
    public short getActionNumber() {
        return this.actionNumber;
    }
    
    @Override
    public ItemStack getClickedItem() {
        return this.clickedItem;
    }

    @Override
    public ClickType getClickType() {
        return this.mode;
    }
	
}
