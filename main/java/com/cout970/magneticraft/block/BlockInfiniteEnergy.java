package com.cout970.magneticraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.cout970.magneticraft.tileentity.TileInfiniteEnergy;

public class BlockInfiniteEnergy extends BlockMg{

	public BlockInfiniteEnergy() {
		super(Material.iron);
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileInfiniteEnergy();
	}

	@Override
	public String[] getTextures() {
		return new String[]{"infinite_energy"};
	}

	@Override
	public String getName() {
		return "infinite_energy";
	}

}
