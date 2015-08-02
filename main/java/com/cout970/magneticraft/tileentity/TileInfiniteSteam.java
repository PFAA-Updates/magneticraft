package com.cout970.magneticraft.tileentity;

import com.cout970.magneticraft.api.util.MgDirection;
import com.cout970.magneticraft.api.util.MgUtils;
import com.cout970.magneticraft.update1_8.IFluidHandler1_8;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileInfiniteSteam extends TileBase implements IFluidHandler1_8{

	public void updateEntity(){
		super.updateEntity();
		if(worldObj.isRemote)return;
		for(MgDirection d : MgDirection.values()){
			TileEntity t = MgUtils.getTileEntity(this, d);
			if(t instanceof IFluidHandler){
				IFluidHandler f = (IFluidHandler) t;
				if(f.canFill(d.toForgeDir(), FluidRegistry.getFluid("steam"))){
					f.fill(d.toForgeDir().getOpposite(), FluidRegistry.getFluidStack("steam", 500) ,true);
				}
			}
		}
	}

	@Override
	public int fillMg(MgDirection from, FluidStack resource, boolean doFill) {
		return 0;
	}

	@Override
	public FluidStack drainMg_F(MgDirection from, FluidStack resource, boolean doDrain) {
		return null;
	}

	@Override
	public FluidStack drainMg(MgDirection from, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public boolean canFillMg(MgDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public boolean canDrainMg(MgDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfoMg(MgDirection from) {
		return new FluidTankInfo[0];
	}
	
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if(this instanceof IFluidHandler1_8)return((IFluidHandler1_8)this).fillMg(MgDirection.getDirection(from.ordinal()), resource, doFill);
		return 0;
	}

	public FluidStack drain(ForgeDirection from, FluidStack resource,
			boolean doDrain) {
		if(this instanceof IFluidHandler1_8)return ((IFluidHandler1_8)this).drainMg_F(MgDirection.getDirection(from.ordinal()), resource,doDrain);
		return null;
	}

	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if(this instanceof IFluidHandler1_8)return ((IFluidHandler1_8)this).drainMg(MgDirection.getDirection(from.ordinal()),maxDrain,doDrain);
		return null;
	}

	public boolean canFill(ForgeDirection from, Fluid fluid) {
		if(this instanceof IFluidHandler1_8)return ((IFluidHandler1_8)this).canFillMg(MgDirection.getDirection(from.ordinal()),fluid);
		return false;
	}

	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		if(this instanceof IFluidHandler1_8)return ((IFluidHandler1_8)this).canDrainMg(MgDirection.getDirection(from.ordinal()),fluid);
		return false;
	}

	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		if(this instanceof IFluidHandler1_8)return ((IFluidHandler1_8)this).getTankInfoMg(MgDirection.getDirection(from.ordinal()));
		return null;
	}
}