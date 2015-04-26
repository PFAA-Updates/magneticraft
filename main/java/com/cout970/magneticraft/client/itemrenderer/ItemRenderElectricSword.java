package com.cout970.magneticraft.client.itemrenderer;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;

import org.lwjgl.opengl.GL11;

import com.cout970.magneticraft.client.model.ModelElectricSword;
import com.cout970.magneticraft.client.model.ModelReactorVessel;
import com.cout970.magneticraft.client.tilerender.ModelTextures;
import com.cout970.magneticraft.util.RenderUtil;

public class ItemRenderElectricSword implements IItemRenderer{

	public ModelElectricSword model = new ModelElectricSword();
	
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		switch (type) {
		case ENTITY: {
			render(0.0F, -0.5F, 0.0F, 1.0F,item,type);
			return;
		}
		case EQUIPPED: {
			render(1.0F, -0.45F, 1.25F, 2.0F,item,type);
			return;
		}
		case INVENTORY: {
			render(0.0F, -1.25F, 0.0F, 1.0F,item,type);
			return;
		}
		case EQUIPPED_FIRST_PERSON: {
			render(0.3F, -0.1F, 0.5F, 1.25F,item,type);
			return;
		}
		default:
			return;
		}
	}
	
	public void render(float x, float y, float z, float scale, ItemStack i,ItemRenderType type){
		GL11.glPushMatrix();
//		GL11.glDisable(GL11.GL_LIGHTING);
		RenderHelper.enableStandardItemLighting();
		GL11.glScalef(scale, scale, scale);
		GL11.glTranslatef(x, y, z);
		GL11.glRotatef(180F, 0, 0, 1);
		GL11.glRotatef(180F, 0, 1, 0);
		if(type == ItemRenderType.EQUIPPED){
			GL11.glRotatef(-45F, 0, 0, 1);
			GL11.glRotatef(-45F, 1, 0, 0);
			GL11.glTranslatef(0, -0.6F, 0);
		}
		GL11.glTranslatef(0, -1.5f, 0);
		RenderUtil.bindTexture(ModelTextures.SWORD);
		model.renderStatic(0.0625f);
//		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}
}