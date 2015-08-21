package com.cout970.magneticraft.client.tilerender;

import org.lwjgl.opengl.GL11;

import com.cout970.magneticraft.client.model.ModelBrassPipe;
import com.cout970.magneticraft.parts.micro.PartBrassPipe;
import com.cout970.magneticraft.util.RenderUtil;

import codechicken.lib.vec.Vector3;

public class TileRenderBrassPipe {
	
	private ModelBrassPipe model = new ModelBrassPipe();

	public void render(PartBrassPipe part, Vector3 pos) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) pos.x + 0.5F, (float) pos.y - 0.5F, (float) pos.z + 0.5F);
		GL11.glColor4f(1, 1, 1, 1);
		RenderUtil.bindTexture(ModelTextures.BRASS_PIPE);
		model.renderStatic(0.0625f);
		int con = part.connections == -1 ? 0 : part.connections;
		model.renderDynamic(0.0625f, con, part.interactions);
		GL11.glPopMatrix();
	}
}
