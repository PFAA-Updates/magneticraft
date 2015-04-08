package com.cout970.magneticraft.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;

import com.cout970.magneticraft.ManagerBlocks;
import com.cout970.magneticraft.api.electricity.Conductor;
import com.cout970.magneticraft.api.electricity.ElectricConstants;
import com.cout970.magneticraft.api.electricity.IElectricConductor;
import com.cout970.magneticraft.api.util.MgDirection;
import com.cout970.magneticraft.util.tile.TileConductorLow;

public class TileAirlock extends TileConductorLow{

	@Override
	public IElectricConductor initConductor() {
		return new Conductor(this);
	}

	public void updateEntity(){
		super.updateEntity();
		int radsquared = 49;
		if(worldObj.isRemote)return;
		if(cond.getVoltage() > ElectricConstants.MACHINE_WORK && worldObj.getWorldTime() % 200 == 0){
			for(int i=-8;i<9;i++)
				for(int j=-8;j<9;j++)
					for(int k=-8;k<9;k++)
						if(i*i+j*j+k*k < radsquared){
							setAir(xCoord+i, yCoord+j, zCoord+k);
						}
			for(int i=-8;i<9;i++)
				for(int j=-8;j<9;j++)
					for(int k=-8;k<9;k++)
						if(i*i+j*j+k*k < radsquared && Block.isEqualTo(worldObj.getBlock(xCoord+i, yCoord+j, zCoord+k),ManagerBlocks.air_bubble)){
							boolean water = false;
							for(MgDirection dir : MgDirection.values()){
								Block b = worldObj.getBlock(xCoord+i+dir.getOffsetX(), yCoord+j+dir.getOffsetY(), zCoord+k+dir.getOffsetZ());
								if(b.getMaterial() == Material.water) water = true;
							}
							if(!water)worldObj.setBlockToAir(xCoord+i, yCoord+j, zCoord+k);
						}
		}
	}

	public void setAir(int x,int y,int z) {
		if(Block.isEqualTo(worldObj.getBlock(x, y, z),Blocks.water) || Block.isEqualTo(worldObj.getBlock(x, y, z),Blocks.flowing_water)){
			worldObj.setBlock(x, y, z, ManagerBlocks.air_bubble);
			cond.drainPower(1000);
		}
	}
}