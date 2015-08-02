package com.cout970.magneticraft.client.gui;

import java.util.ArrayList;
import java.util.List;

import com.cout970.magneticraft.Magneticraft;
import com.cout970.magneticraft.api.heat.IHeatConductor;
import com.cout970.magneticraft.client.gui.component.CompBackground;
import com.cout970.magneticraft.client.gui.component.CompFluidRender;
import com.cout970.magneticraft.client.gui.component.CompProgresBar;
import com.cout970.magneticraft.client.gui.component.GuiPoint;
import com.cout970.magneticraft.client.gui.component.IGuiComp;
import com.cout970.magneticraft.tileentity.TilePolymerizer;
import com.cout970.magneticraft.util.RenderUtil;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiPolimerizer extends GuiBasic{

	public GuiPolimerizer(Container c, TileEntity tile) {
		super(c, tile);
	}
	
	@Override
	public void initComponenets() {
		comp.add(new CompBackground(new ResourceLocation(Magneticraft.NAME.toLowerCase()+":textures/gui/polimerizer.png")));
		comp.add(new CompHeaterBar(new ResourceLocation(Magneticraft.NAME.toLowerCase()+":textures/gui/heatbar.png"), new GuiPoint(20, 20)));
		comp.add(new CompFluidRender(((TilePolymerizer)tile).input, new GuiPoint(41,25), new GuiPoint(59, 64),new ResourceLocation(Magneticraft.NAME.toLowerCase()+":textures/gui/tank.png")));
		comp.add(new CompProgresBar(new ResourceLocation(Magneticraft.NAME.toLowerCase()+":textures/gui/progresbar1.png"), new GuiPoint(93, 35), ((TilePolymerizer)tile).getProgresBar()));
	}
	
	public class CompHeaterBar implements IGuiComp{

		public ResourceLocation texture;
		public GuiPoint pos;
		
		public CompHeaterBar(ResourceLocation tex, GuiPoint p){
			texture = tex;
			pos = p;
		}

		@Override
		public void render(int mx, int my, TileEntity tile, GuiBasic gui) {
			if(tile instanceof TilePolymerizer){
				TilePolymerizer ref = (TilePolymerizer) tile;
				IHeatConductor c = ref.heater;
				if(c == null)return;
				int scale = 0;
				if(c.getMaxTemp() > 0)
					scale = (int) (c.getTemperature() * 44 / c.getMaxTemp());
				if(scale > 44)scale = 44;
				gui.mc.renderEngine.bindTexture(texture);
				RenderUtil.drawTexturedModalRectScaled(gui.xStart+pos.x, gui.yStart+pos.y+(44-scale), 0, 44-scale, 6, scale, 12, 45);
			}
		}

		@Override
		public void onClick(int mx, int my, int buttom, GuiBasic gui) {}

		@Override
		public boolean onKey(int n, char key, GuiBasic gui) {return false;}

		@Override
		public void renderTop(int mx, int my, TileEntity tile, GuiBasic gui) {
			if(tile instanceof TilePolymerizer){
				TilePolymerizer ref = (TilePolymerizer) tile;
				IHeatConductor c = ref.heater;
				if(c == null)return;
				if(gui.isIn(mx, my, gui.xStart+pos.x, gui.yStart+pos.y, 6, 44)){
					List<String> data = new ArrayList<String>();
					data.add((int)c.getTemperature()+"C");
					gui.drawHoveringText2(data, mx-gui.xStart, my-gui.yStart);
					RenderHelper.enableGUIStandardItemLighting();
				}
			}
		}

	}

}
