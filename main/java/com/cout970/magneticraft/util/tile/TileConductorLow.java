package com.cout970.magneticraft.util.tile;

import net.minecraft.nbt.NBTTagCompound;

import com.cout970.magneticraft.api.electricity.CableCompound;
import com.cout970.magneticraft.api.electricity.IElectricConductor;
import com.cout970.magneticraft.api.electricity.IElectricTile;
import com.cout970.magneticraft.api.util.VecInt;
import com.cout970.magneticraft.tileentity.TileBase;

public abstract class TileConductorLow extends TileBase implements IElectricTile{

	public IElectricConductor cond = initConductor();
	
	public abstract IElectricConductor initConductor();
	
	@Override
	public CableCompound getConds(VecInt dir, int tier) {
		if(tier != 0 && tier !=-1)return null;
		return new CableCompound(cond);
	}
	
	@Override
	public void onNeigChange(){
		super.onNeigChange();
		cond.disconect();
	}
	
	@Override
	public void updateEntity(){
		super.updateEntity();
		if(worldObj.isRemote)return;
		cond.recache();
		cond.iterate();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		cond.load(nbt);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		cond.save(nbt);
	}

}