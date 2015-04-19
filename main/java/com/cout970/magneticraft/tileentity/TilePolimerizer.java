package com.cout970.magneticraft.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.cout970.magneticraft.ManagerItems;
import com.cout970.magneticraft.api.heat.IHeatConductor;
import com.cout970.magneticraft.api.util.BlockPosition;
import com.cout970.magneticraft.api.util.MgDirection;
import com.cout970.magneticraft.api.util.MgUtils;
import com.cout970.magneticraft.api.util.VecInt;
import com.cout970.magneticraft.client.gui.component.IBurningTime;
import com.cout970.magneticraft.client.gui.component.IGuiSync;
import com.cout970.magneticraft.util.IInventoryManaged;
import com.cout970.magneticraft.util.InventoryComponent;
import com.cout970.magneticraft.util.InventoryUtils;
import com.cout970.magneticraft.util.fluid.TankMg;
import com.cout970.magneticraft.util.multiblock.Multiblock;

public class TilePolimerizer extends TileMB_Base implements IInventoryManaged, ISidedInventory, IGuiSync, IBurningTime{

	public boolean active;
	public int Progres;
	public int maxProgres = 200;
	public InventoryComponent inv = new InventoryComponent(this, 2, "Polimerizer");
	public TankMg input;
	public IHeatConductor heater;
	public InventoryComponent in,out;
	
	
	public InventoryComponent getInv(){
		return inv;
	}
	
	public void updateEntity(){
		super.updateEntity();
		
		if(!active)return;		
		if(input == null || in == null || out == null || heater == null || worldObj.getWorldTime() % 20 == 0){
			searchTanks();
			return;
		}
		if(worldObj.isRemote)return;
		if(canCraft()){
			if(Progres >= maxProgres){
				craft();
				Progres = 0;
			}else{
				Progres++;
			}
			heater.drainCalories(100);
		}
		distributeItems();
	}
	
	private void distributeItems() {
		if (in != null) {
			if(((TileBase)in.tile).isControled()){
				if(getInv().getStackInSlot(0) != null){
					int s = InventoryUtils.findCombination(in, getInv().getStackInSlot(0));
					if(s != -1){
						InventoryUtils.traspass(in,this,s,0);
					}
				}else{
					setInventorySlotContents(0, InventoryUtils.getItemStack(in));
				}
			}
		}
		if (out != null) {
			if(((TileBase)out.tile).isControled()){
				if(getInv().getStackInSlot(1) != null){
					int s = InventoryUtils.getSlotForStack(out,getInv().getStackInSlot(1));
					if(s != -1){
						InventoryUtils.traspass(this,out,1,s);
					}
				}
			}
		}
	}
	
	private void craft() {
		decrStackSize(0, 1);
		if(getInv().getStackInSlot(1) == null){
			getInv().setInventorySlotContents(1, new ItemStack(ManagerItems.plastic));
		}else{
			ItemStack i = getInv().getStackInSlot(1);
			i.stackSize++;
			getInv().setInventorySlotContents(1, i);
		}
		heater.drainCalories(500);
		input.drain(500, true);
	}

	private boolean canCraft() {
		if(input.getFluidAmount() >= 500 && isControled() && heater.getTemperature() > 250){
			ItemStack stack = getInv().getStackInSlot(0);
			if(stack != null && MgUtils.areEcuals(stack, new ItemStack(ManagerItems.dustSulfur), true)){
				ItemStack output = getInv().getStackInSlot(1);
				if(output == null || (MgUtils.areEcuals(output, new ItemStack(ManagerItems.plastic), true)) && output.stackSize+1 <= getInventoryStackLimit()){
					return true;
				}
			}
		}
		return false;
	}

	private void searchTanks() {
		MgDirection d = MgDirection.getDirection(getBlockMetadata()%6).opposite();
		VecInt vec = d.getVecInt();
		TileEntity tile = MgUtils.getTileEntity(this, vec.copy().multiply(4));
		
		if(tile instanceof TileMgTank){
			input = ((TileMgTank) tile).getTank();
		}
		tile = MgUtils.getTileEntity(this, vec.copy().add(d.step(MgDirection.DOWN).getVecInt().getOpposite()));
		if(tile instanceof IInventoryManaged){
			in = ((IInventoryManaged) tile).getInv();
		}
		tile = MgUtils.getTileEntity(this, vec.copy().add(d.step(MgDirection.UP).getVecInt().getOpposite()));
		if(tile instanceof IInventoryManaged){
			out = ((IInventoryManaged) tile).getInv();
		}
		tile = MgUtils.getTileEntity(this, vec.copy().multiply(3));
		if(tile instanceof TileHeater){
			heater = ((TileHeater) tile).getHeatCond(vec.getOpposite());
		}
	}

	@Override
	public void onDestroy(World w, BlockPosition p, Multiblock c, MgDirection e) {
		active = false;
		sendUpdateToClient();
	}

	@Override
	public void onActivate(World w, BlockPosition p, Multiblock c, MgDirection e) {
		active = true;
	}
	
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		active = nbt.getBoolean("A");
		getInv().readFromNBT(nbt);
	}

	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setBoolean("A", active);
		getInv().writeToNBT(nbt);
	}
	
	@Override
	public int getProgres() {
		return Progres;
	}

	@Override
	public int getMaxProgres() {
		return maxProgres;
	}

	@Override
	public void sendGUINetworkData(Container cont, ICrafting craft) {
		if(input == null || heater == null)return;
		craft.sendProgressBarUpdate(cont, 0, (int) heater.getTemperature());
		if(input.getFluidAmount() > 0){
			craft.sendProgressBarUpdate(cont, 1, input.getFluid().getFluidID());
			craft.sendProgressBarUpdate(cont, 2, input.getFluidAmount());
		}else craft.sendProgressBarUpdate(cont, 1, -1);
		
		craft.sendProgressBarUpdate(cont, 3, Progres);
		((TileHeater)heater.getParent()).sendGUINetworkData(cont, craft);
	}

	@Override
	public void getGUINetworkData(int id, int value) {
		if(input == null || heater == null)return;
		if(id == 0) heater.setTemperature(value);
		if(id == 1)
			if(value == -1) input.setFluid(null);
			else input.setFluid(new FluidStack(FluidRegistry.getFluid(value),1));
		if(id == 2) input.getFluid().amount = value;
		if(id == 3)Progres = value;
		((TileHeater)heater.getParent()).getGUINetworkData(id, value);
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
		return new int[] {};
	}

	@Override
	public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_,
			int p_102007_3_) {
		return false;
	}

	@Override
	public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_,
			int p_102008_3_) {
		return false;
	}
	
	public int getSizeInventory() {
		return getInv().getSizeInventory();
	}

	public ItemStack getStackInSlot(int s) {
		return getInv().getStackInSlot(s);
	}

	public ItemStack decrStackSize(int a, int b) {
		return getInv().decrStackSize(a, b);
	}

	public ItemStack getStackInSlotOnClosing(int a) {
		return getInv().getStackInSlotOnClosing(a);
	}

	public void setInventorySlotContents(int a, ItemStack b) {
		getInv().setInventorySlotContents(a, b);
	}

	public String getInventoryName() {
		return getInv().getInventoryName();
	}

	public boolean hasCustomInventoryName() {
		return getInv().hasCustomInventoryName();
	}

	public int getInventoryStackLimit() {
		return getInv().getInventoryStackLimit();
	}

	public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
		return true;
	}

	public void openInventory() {}

	public void closeInventory() {}

	public boolean isItemValidForSlot(int a, ItemStack b) {
		return getInv().isItemValidForSlot(a, b);
	}
}
