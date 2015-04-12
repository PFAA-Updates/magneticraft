package com.cout970.magneticraft.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;

import com.cout970.magneticraft.api.electricity.BatteryConductor;
import com.cout970.magneticraft.api.electricity.ConnectionClass;
import com.cout970.magneticraft.api.electricity.ElectricConstants;
import com.cout970.magneticraft.api.electricity.IElectricConductor;
import com.cout970.magneticraft.api.electricity.item.IBatteryItem;
import com.cout970.magneticraft.api.util.MgDirection;
import com.cout970.magneticraft.api.util.VecInt;
import com.cout970.magneticraft.util.tile.TileConductorLow;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileTeslaCoil extends TileConductorLow{

	private List<EntityPlayer> nearPlayers = new ArrayList<EntityPlayer>();

	@Override
	public IElectricConductor initConductor() {
		return new BatteryConductor(this,ElectricConstants.RESISTANCE_COPPER_2X2,16000,ElectricConstants.MACHINE_DISCHARGE,ElectricConstants.MACHINE_CHARGE){			
			@Override
			public boolean isAbleToConnect(IElectricConductor e, VecInt v) {
				return e.getConnectionClass(v.getOpposite()) == ConnectionClass.FULL_BLOCK || e.getConnectionClass(v.getOpposite()) == ConnectionClass.SLAB_BOTTOM || VecInt.getConnexion(MgDirection.DOWN).equals(v);
			}
			
			@Override
			public ConnectionClass getConnectionClass(VecInt v) {
				if(v.toMgDirection() == MgDirection.DOWN)return ConnectionClass.FULL_BLOCK;
				return ConnectionClass.SLAB_BOTTOM;
			}
		};
	}

	public void updateEntity(){
		super.updateEntity();
		if(worldObj.isRemote)return;
		if(cond.getVoltage() > ElectricConstants.MACHINE_WORK){
			if(worldObj.getWorldTime() % 20 == 0){
				nearPlayers.clear();
				List e = worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(xCoord-10, yCoord-10, zCoord-10, xCoord+10, yCoord+10, zCoord+10));
				for(Object o : e){
					if(o instanceof EntityPlayer){
						nearPlayers.add((EntityPlayer) o);
					}
				}
			}

			for(EntityPlayer p : nearPlayers){
				for(int i = 0; i < p.inventory.getSizeInventory();i++){
					ItemStack stack = p.inventory.getStackInSlot(i);
					if(stack != null && stack.getItem() instanceof IBatteryItem){
						IBatteryItem batteryItem = (IBatteryItem) stack.getItem();
						int space = batteryItem.getMaxCharge() - batteryItem.getCharge(stack);
						if(space > 0){
							int change = (int)Math.min(space, (cond.getVoltage()-ElectricConstants.MACHINE_WORK)*2);
							if(change > 0){
								batteryItem.charge(stack, change);
								cond.drainPower(change*100);
							}
						}
					}
				}
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord+1, yCoord+3, zCoord+1);
    }
}
