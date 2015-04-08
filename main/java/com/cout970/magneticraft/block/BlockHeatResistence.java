package com.cout970.magneticraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.cout970.magneticraft.tileentity.TileHeatResistance;

public class BlockHeatResistence extends BlockMg{

	public BlockHeatResistence() {
		super(Material.iron);
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileHeatResistance();
	}

	@Override
	public String[] getTextures() {
		return new String[]{"heat_resistence"};
	}

	@Override
	public String getName() {
		return "heat_resitence";
	}

}