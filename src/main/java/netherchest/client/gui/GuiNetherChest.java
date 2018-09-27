package netherchest.client.gui;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import netherchest.NetherChest;
import netherchest.common.inventory.ContainerNetherChest;
import netherchest.common.tileentity.TileEntityNetherChest;

public class GuiNetherChest extends GuiContainer {
	
	public static final int WIDTH = 176;
    public static final int HEIGHT = 166;
    
    private static final ResourceLocation background = new ResourceLocation(NetherChest.MODID, "textures/gui/nether_chest.png");
    
    private TileEntityNetherChest te;
    private InventoryPlayer playerinventory;
    
    public GuiNetherChest(ContainerNetherChest container, InventoryPlayer playerinventory) {
    	super(container);
    	
    	this.playerinventory = playerinventory;
		this.te = container.getTile();
		xSize = WIDTH;
        ySize = HEIGHT;
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString(this.te.getDisplayName().getUnformattedText(), 8, 6, 4210752);
        this.fontRenderer.drawString(this.playerinventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 4, 4210752);
    }

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}

}
