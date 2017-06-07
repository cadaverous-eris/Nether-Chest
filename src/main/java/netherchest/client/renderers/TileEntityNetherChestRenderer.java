package netherchest.client.renderers;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import netherchest.common.tileentity.TileEntityNetherChest;

public class TileEntityNetherChestRenderer extends TileEntitySpecialRenderer<TileEntityNetherChest> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("netherchest:textures/model/nether_chest.png");
	private final ModelChest model = new ModelChest();
	private final Random rand = new Random();

	public void renderTileEntityAt(TileEntityNetherChest te, double x, double y, double z, float partialTicks,
			int destroyStage) {
		
		GlStateManager.enableDepth();
		GlStateManager.depthFunc(515);
		GlStateManager.depthMask(true);
		int i;

		if (te.hasWorld()) {
			Block block = te.getBlockType();
			i = te.getBlockMetadata();
		} else {
			i = 0;
		}

		if (destroyStage >= 0) {
			this.bindTexture(DESTROY_STAGES[destroyStage]);
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.scale(4.0F, 4.0F, 1.0F);
			GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
			GlStateManager.matrixMode(5888);
		} else {
			this.bindTexture(TEXTURE);
		}

		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		if (destroyStage < 0) {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		}
		GlStateManager.translate((float) x, (float) y + 1.0F, (float) z + 1.0F);
		GlStateManager.scale(1.0F, -1.0F, -1.0F);
		GlStateManager.translate(0.5F, 0.5F, 0.5F);
		int j = 0;

		if (i == 2) {
			j = 180;
		}

		if (i == 3) {
			j = 0;
		}

		if (i == 4) {
			j = 90;
		}

		if (i == 5) {
			j = -90;
		}

		GlStateManager.rotate((float) j, 0.0F, 1.0F, 0.0F);
		GlStateManager.translate(-0.5F, -0.5F, -0.5F);
		float f = te.prevLidAngle + (te.lidAngle - te.prevLidAngle) * partialTicks;
		f = 1.0F - f;
		f = 1.0F - f * f * f;
		model.chestLid.rotateAngleX = -(f * ((float) Math.PI / 2F));
		model.renderAll();
		
		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		if (destroyStage >= 0) {
			GlStateManager.matrixMode(5890);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
		}
		
		//renderShade(x, y, z, partialTicks, te.getRenderTick());
		
		if (te.lidAngle > 0.25F) {
			renderOpenEffects(x, y, z, te.lidAngle);
		}

	}
	
	private void renderOpenEffects(double x, double y, double z, float angle) {
        
		GlStateManager.pushAttrib();
        GlStateManager.disableCull();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.depthMask(false);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
        GlStateManager.disableTexture2D();
        int dfunc = GL11.glGetInteger(GL11.GL_DEPTH_FUNC);
		GlStateManager.depthFunc(GL11.GL_LEQUAL);
		int func = GL11.glGetInteger(GL11.GL_ALPHA_TEST_FUNC);
		float ref = GL11.glGetFloat(GL11.GL_ALPHA_TEST_REF);
		GlStateManager.alphaFunc(GL11.GL_ALWAYS, 0);
		
		GlStateManager.disableRescaleNormal();
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		
		renderLightRay(angle);
        
        GlStateManager.popMatrix();
		
		GlStateManager.alphaFunc(func, ref);
		GlStateManager.depthFunc(dfunc);
        GlStateManager.enableTexture2D();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.depthMask(true);
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.popAttrib();
	}
	
	private void renderLightRay(float angle) {
		int red = 150;
		int green = 0;
		int blue = 100;
		int alpha = (int) (128 * Math.sqrt(angle));
		
		Tessellator tess = Tessellator.getInstance();
        VertexBuffer buffer = tess.getBuffer();
        //buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
        
        for (int i = 0; i < 4; i++) {
        	
        buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
        
        double minXZ = 0.25 + (0.0625 * i);
		double maxXZ = 1 - minXZ;
		double deltaXZ = 0.125;
		
		double minY = 0.625;
		double maxY = minY + 0.375 - (0.0625 * i);
        
        buffer.pos((minXZ + maxXZ) / 2.0, maxY, (minXZ + maxXZ) / 2.0).color(red, green, blue, 0).endVertex();
        buffer.pos(minXZ, minY, minXZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minXZ, minY, maxXZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxXZ, minY, maxXZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxXZ, minY, minXZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minXZ, minY, minXZ).color(red, green, blue, alpha).endVertex();
        
        tess.draw();
        
        }

	}
	
	private void renderShade(double x, double y, double z, float partialTicks, int renderTick) {
		
		int red = 255;
		int green = 0;
		int blue = 0;
		int alpha = 50;
		
		double r = 0.4375D + (MathHelper.sin(renderTick / 6F) * 0.03125);
		
		GlStateManager.pushAttrib();
        GlStateManager.disableCull();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.depthMask(false);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
        GlStateManager.disableTexture2D();
        int dfunc = GL11.glGetInteger(GL11.GL_DEPTH_FUNC);
		GlStateManager.depthFunc(GL11.GL_LEQUAL);
		int func = GL11.glGetInteger(GL11.GL_ALPHA_TEST_FUNC);
		float ref = GL11.glGetFloat(GL11.GL_ALPHA_TEST_REF);
		GlStateManager.alphaFunc(GL11.GL_ALWAYS, 0);
		
		GlStateManager.disableRescaleNormal();
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5D, y + 0.4375D, z + 0.5D);
		
		Tessellator tess = Tessellator.getInstance();
        VertexBuffer buffer = tess.getBuffer();
        
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        
        buffer.pos(r, r, r).color(red, green, blue, alpha).endVertex();
        buffer.pos(-r, r, r).color(red, green, blue, alpha).endVertex();
        buffer.pos(-r, -r, r).color(red, green, blue, alpha).endVertex();
        buffer.pos(r, -r, r).color(red, green, blue, alpha).endVertex();
        
        buffer.pos(r, r, -r).color(red, green, blue, alpha).endVertex();
        buffer.pos(-r, r, -r).color(red, green, blue, alpha).endVertex();
        buffer.pos(-r, -r, -r).color(red, green, blue, alpha).endVertex();
        buffer.pos(r, -r, -r).color(red, green, blue, alpha).endVertex();
        
        buffer.pos(r, r, r).color(red, green, blue, alpha).endVertex();
        buffer.pos(r, r, -r).color(red, green, blue, alpha).endVertex();
        buffer.pos(r, -r, -r).color(red, green, blue, alpha).endVertex();
        buffer.pos(r, -r, r).color(red, green, blue, alpha).endVertex();
        
        buffer.pos(-r, r, r).color(red, green, blue, alpha).endVertex();
        buffer.pos(-r, r, -r).color(red, green, blue, alpha).endVertex();
        buffer.pos(-r, -r, -r).color(red, green, blue, alpha).endVertex();
        buffer.pos(-r, -r, r).color(red, green, blue, alpha).endVertex();
        
        buffer.pos(r, r, r).color(red, green, blue, alpha).endVertex();
        buffer.pos(-r, r, r).color(red, green, blue, alpha).endVertex();
        buffer.pos(-r, r, -r).color(red, green, blue, alpha).endVertex();
        buffer.pos(r, r, -r).color(red, green, blue, alpha).endVertex();
        
        buffer.pos(r, -r, r).color(red, green, blue, alpha).endVertex();
        buffer.pos(-r, -r, r).color(red, green, blue, alpha).endVertex();
        buffer.pos(-r, -r, -r).color(red, green, blue, alpha).endVertex();
        buffer.pos(r, -r, -r).color(red, green, blue, alpha).endVertex();
        	
        tess.draw();
        
        GlStateManager.popMatrix();
		
		GlStateManager.alphaFunc(func, ref);
		GlStateManager.depthFunc(dfunc);
        GlStateManager.enableTexture2D();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.depthMask(true);
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.popAttrib();
		
	}

}
