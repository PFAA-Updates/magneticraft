package com.cout970.magneticraft.client.tilerender;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import com.cout970.magneticraft.tileentity.TilePolimerizer;
import com.cout970.magneticraft.tileentity.TileTurbineControl;
import com.cout970.magneticraft.util.RenderUtil;
import com.cout970.magneticraft.util.multiblock.MB_Register;
import com.cout970.magneticraft.util.multiblock.Multiblock;

public class TileRenderTurbine extends TileEntitySpecialRenderer{

	@Override
	public void renderTileEntityAt(TileEntity t, double x, double y, double z, float frames) {
		TileTurbineControl tile = (TileTurbineControl) t;
		if(tile.drawCounter > 0){
			GL11.glColor4f(1, 1, 1, 1f);
			Multiblock mb = MB_Register.getMBbyID(MB_Register.ID_TURBINE);
			RenderUtil.renderMultiblock(x,y,z,tile,t,mb);
		}
	}
}