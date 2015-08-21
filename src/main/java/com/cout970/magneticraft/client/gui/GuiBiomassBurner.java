package com.cout970.magneticraft.client.gui;

import java.util.Locale;

import com.cout970.magneticraft.Magneticraft;
import com.cout970.magneticraft.client.gui.component.CompBackground;
import com.cout970.magneticraft.client.gui.component.CompBurningTime;
import com.cout970.magneticraft.client.gui.component.CompButtonRedstoneControl;
import com.cout970.magneticraft.client.gui.component.CompHeatBar;
import com.cout970.magneticraft.client.gui.component.GuiPoint;
import com.cout970.magneticraft.tileentity.TileBiomassBurner;

import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiBiomassBurner extends GuiBasic{

	public GuiBiomassBurner(Container c, TileEntity tile) {
		super(c, tile);
	}

	@Override
	public void initComponenets() {
		comp.add(new CompBackground(new ResourceLocation(Magneticraft.NAME.toLowerCase(Locale.US)+":textures/gui/biomass_burner.png")));
		comp.add(new CompBurningTime(new ResourceLocation(Magneticraft.NAME.toLowerCase(Locale.US)+":textures/gui/fire.png"),new GuiPoint(80, 28), ((TileBiomassBurner)tile).getBurningTimeBar()));
		comp.add(new CompHeatBar(new ResourceLocation(Magneticraft.NAME.toLowerCase(Locale.US)+":textures/gui/heatbar.png"), new GuiPoint(107, 20), ((TileBiomassBurner)tile).heat));
		comp.add(new CompButtonRedstoneControl(new GuiPoint(150, 8)));
	}

}
