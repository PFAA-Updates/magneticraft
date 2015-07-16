package com.cout970.magneticraft.tileentity;

import net.minecraft.tileentity.TileEntity;

import com.cout970.magneticraft.api.electricity.ConnectionClass;
import com.cout970.magneticraft.api.electricity.ElectricConductor;
import com.cout970.magneticraft.api.electricity.IElectricConductor;
import com.cout970.magneticraft.api.electricity.wires.IElectricPole;
import com.cout970.magneticraft.api.electricity.wires.ITileElectricPole;
import com.cout970.magneticraft.api.util.MgDirection;
import com.cout970.magneticraft.api.util.VecInt;
import com.cout970.magneticraft.util.tile.TileConductorLow;

public class TileElectricPoleCableWireDown extends TileConductorLow implements ITileElectricPole{

	public int mask = -1;
	
	@Override
	public IElectricConductor initConductor() {
		return new ElectricConductor(this){
			@Override
			public VecInt[] getValidConnections() {
				return new VecInt[]{MgDirection.EAST.toVecInt(), MgDirection.WEST.toVecInt()};
			}
			
			@Override
			public boolean isAbleToConnect(IElectricConductor c, VecInt d) {
				if(d.toMgDirection() != MgDirection.EAST && d.toMgDirection() != MgDirection.WEST)return false;
				return c.getConnectionClass(d.getOpposite()) == ConnectionClass.CABLE_LOW 
						|| c.getConnectionClass(d.getOpposite()) == ConnectionClass.SLAB_BOTTOM
						|| c.getConnectionClass(d.getOpposite()) == ConnectionClass.FULL_BLOCK;
			}
		};
	}

	@Override
	public IElectricPole getPoleConnection() {
		return null;
	}

	@Override
	public ITileElectricPole getMainTile() {
		TileEntity pole = new VecInt(this).add(0, 4, 0).getTileEntity(getWorldObj());
		if(pole instanceof ITileElectricPole)return (ITileElectricPole) pole;
		return null;
	}
}
