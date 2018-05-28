package netherchest.client.renderers;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import netherchest.common.tileentity.TileEntityNetherChest;

public class TileEntityNetherChestRenderer extends TileEntitySpecialRenderer<TileEntityNetherChest> {

	public static TextureAtlasSprite portalSprite = null;
	
	private static final ResourceLocation TEXTURE = new ResourceLocation("netherchest:textures/model/nether_chest.png");
	
	private final ModelChest model = new ModelChest();
	private final Random rand = new Random();

	@Override
	public void render(TileEntityNetherChest te, double x, double y, double z, float partialTicks,
			int destroyStage, float alpha) {
		
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
		
		if (te.lidAngle > 0.01F) {
			renderPortal(x, y, z, partialTicks, te.getRenderTick(), 1F);
		}

	}
	
	private void renderPortal(double x, double y, double z, float partialTicks, int renderTick, float lidAngle) {
		GlStateManager.pushAttrib();
        //GlStateManager.disableCull();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.depthMask(false);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
        int dfunc = GL11.glGetInteger(GL11.GL_DEPTH_FUNC);
		GlStateManager.depthFunc(GL11.GL_LEQUAL);
		int func = GL11.glGetInteger(GL11.GL_ALPHA_TEST_FUNC);
		float ref = GL11.glGetFloat(GL11.GL_ALPHA_TEST_REF);
		GlStateManager.alphaFunc(GL11.GL_ALWAYS, 0);
		
		GlStateManager.disableRescaleNormal();
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		
		if (portalSprite != null) {
			bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			
			Tessellator tess = Tessellator.getInstance();
	        BufferBuilder buffer = tess.getBuffer();
	        
	        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
			
	        int q = 12;
	        
	        for (int pZ = 0; pZ < q; pZ++) {
	        	for (int pX = 0; pX < q; pX++) {
	        		double minX = 0.25 + (pX * ((1d / q) * 0.5));
	        		double maxX = minX + ((1d / q) * 0.5);
	        		double minZ = 0.25 + (pZ * ((1d / q) * 0.5));
	        		double maxZ = minZ + ((1d / q) * 0.5);
	        		
	        		double dU = portalSprite.getMaxU() - portalSprite.getMinU();
	        		double dV = portalSprite.getMaxV() - portalSprite.getMinV();
	        		double minU = portalSprite.getMinU() + (dU * (pX * (1d / q)));
	        		double maxU = portalSprite.getMinU() + (dU * ((pX + 1) * (1d / q)));
	        		double minV = portalSprite.getMinV() + (dV * (pZ * (1d / q)));
	        		double maxV = portalSprite.getMinV() + (dV * ((pZ + 1) * (1d / q)));
	        		
	        		double y1 = 0.6251 + yOffset(pX, pZ, partialTicks, renderTick) * lidAngle;
	        		double y2 = 0.6251 + yOffset(pX, pZ + 1, partialTicks, renderTick) * lidAngle;
	        		double y3 = 0.6251 + yOffset(pX + 1, pZ + 1, partialTicks, renderTick) * lidAngle;
	        		double y4 = 0.6251 + yOffset(pX + 1, pZ, partialTicks, renderTick) * lidAngle;
	        		
	        		buffer.pos(minX, y1, minZ).color(1F, 1F, 1F, 1F).tex(minU, minV).lightmap(240, 240).endVertex();
	        		buffer.pos(minX, y2, maxZ).color(1F, 1F, 1F, 1F).tex(minU, maxV).lightmap(240, 240).endVertex();
	        		buffer.pos(maxX, y3, maxZ).color(1F, 1F, 1F, 1F).tex(maxU, maxV).lightmap(240, 240).endVertex();
	        		buffer.pos(maxX, y4, minZ).color(1F, 1F, 1F, 1F).tex(maxU, minV).lightmap(240, 240).endVertex();
	        	}
	        }
	        
			tess.draw();
		}
		
		
		GlStateManager.popMatrix();
		
		GlStateManager.alphaFunc(func, ref);
		GlStateManager.depthFunc(dfunc);
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.depthMask(true);
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        //GlStateManager.enableCull();
        GlStateManager.popAttrib();
	}
	
	private double yOffset(int x, int z, float partialTicks, int renderTick) {
		double xScale = (-Math.abs(x - 6) + 6) * 0.125; // 0 - 1
		double zScale = (-Math.abs(z - 6) + 6) * 0.125; // 0 - 1
		
		if (xScale == 0 || zScale == 0) return 0;
		double scale = (zScale + xScale) / 2; // 0 - 1
		
		int offset = (((x * 4673) % 19) * ((z * 3491) % 23)) % 341 + ((x * 4327) % 26);
		double amp = (scale * 0.0625);
		return amp + (amp * MathHelper.sin((renderTick - offset) * 0.05F));
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
        BufferBuilder buffer = tess.getBuffer();
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

}
