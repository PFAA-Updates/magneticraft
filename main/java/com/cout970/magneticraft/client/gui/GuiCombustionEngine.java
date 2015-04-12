package com.cout970.magneticraft.client.gui;

import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import com.cout970.magneticraft.Magneticraft;
import com.cout970.magneticraft.client.gui.GuiTurbine.CompEnergyBarMediumVoltage_Turbine;
import com.cout970.magneticraft.client.gui.GuiTurbine.CompFluidRender_Turbine;
import com.cout970.magneticraft.client.gui.component.CompBackground;
import com.cout970.magneticraft.client.gui.component.CompButtonRedstoneControl;
import com.cout970.magneticraft.client.gui.component.CompEnergyBar;
import com.cout970.magneticraft.client.gui.component.CompFluidRender;
import com.cout970.magneticraft.client.gui.component.CompGenericBar;
import com.cout970.magneticraft.client.gui.component.CompHeatBar;
import com.cout970.magneticraft.client.gui.component.CompStorageBar;
import com.cout970.magneticraft.client.gui.component.GuiPoint;
import com.cout970.magneticraft.tileentity.TileCombustionEngine;
import com.cout970.magneticraft.tileentity.TileTurbineControl;

public class GuiCombustionEngine extends GuiBasic{

	public GuiCombustionEngine(Container c, TileEntity tile) {
		super(c, tile);
	}

	@Override
	public void initComponenets() {
		comp.add(new CompBackground(new ResourceLocation(Magneticraft.NAME.toLowerCase()+":textures/gui/combustion_engine.png")));
		comp.add(new CompFluidRender(((TileCombustionEngine)tile).getTank(), new GuiPoint(47,25), new GuiPoint(65, 64),new ResourceLocation(Magneticraft.NAME.toLowerCase()+":textures/gui/tank.png")));
		comp.add(new CompEnergyBar(new ResourceLocation(Magneticraft.NAME.toLowerCase()+":textures/gui/energybar.png"),new GuiPoint(23,16)));
		comp.add(new CompStorageBar(new ResourceLocation(Magneticraft.NAME.toLowerCase()+":textures/gui/energybar.png"),new GuiPoint(31,16)));
		comp.add(new CompHeatBar(new ResourceLocation(Magneticraft.NAME.toLowerCase()+":textures/gui/heatbar.png"), new GuiPoint(69, 20)));
		comp.add(new CompButtonRedstoneControl(new GuiPoint(150, 8)));
		comp.add(new CompGenericBar(new ResourceLocation(Magneticraft.NAME.toLowerCase()+":textures/gui/efficiencybar.png"),new GuiPoint(78, 20)));
	}
}