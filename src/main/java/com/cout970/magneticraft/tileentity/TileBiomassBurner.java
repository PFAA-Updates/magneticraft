package com.cout970.magneticraft.tileentity;

import com.cout970.magneticraft.api.access.RecipeBiomassBurner;
import com.cout970.magneticraft.api.heat.IHeatConductor;
import com.cout970.magneticraft.api.heat.prefab.HeatConductor;
import com.cout970.magneticraft.api.util.EnergyConversor;
import com.cout970.magneticraft.client.gui.component.IBarProvider;
import com.cout970.magneticraft.client.gui.component.IGuiSync;
import com.cout970.magneticraft.util.IInventoryManaged;
import com.cout970.magneticraft.util.InventoryComponent;
import com.cout970.magneticraft.util.tile.TileHeatConductor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileBiomassBurner extends TileHeatConductor implements IInventoryManaged, IGuiSync {

    private InventoryComponent inv = new InventoryComponent(this, 1, "Biomass Burner");
    private int progress;
    private boolean updated;
    private int maxProgres;

    @Override
    public IHeatConductor initHeatCond() {
        return new HeatConductor(this, 1400.0D, 750.0D);
    }

    public InventoryComponent getInv() {
        return inv;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (worldObj.isRemote) return;
        if (!updated) {
            if (progress > 0)
                worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 1, 2);
            else
                worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 0, 2);
            updated = true;
        }
        if (progress > 0) {
            //fuel to heat
            if (heat.getTemperature() < 1200 && isControled()) {
                int i = 4;//burning speed
                if (progress - i < 0) {
                    heat.applyCalories(EnergyConversor.FUELtoCALORIES(progress));
                    progress = 0;
                    updated = false;
                } else {
                    progress -= i;
                    heat.applyCalories(EnergyConversor.FUELtoCALORIES(i));
                }
            }
        }

        if (progress <= 0) {
            if (getInv().getStackInSlot(0) != null && isControled()) {
                int fuel = getItemBurnTime(getInv().getStackInSlot(0));
                if (fuel > 0 && heat.getTemperature() < heat.getMaxTemp()) {
                    progress = fuel;
                    maxProgres = fuel;
                    if (getInv().getStackInSlot(0) != null) {
                        getInv().getStackInSlot(0).stackSize--;
                        if (getInv().getStackInSlot(0).stackSize <= 0) {
                            getInv().setInventorySlotContents(0, getInv().getStackInSlot(0).getItem().getContainerItem(getInv().getStackInSlot(0)));
                        }
                    }
                    markDirty();
                }
            }
            updated = false;
        }
    }

    private int getItemBurnTime(ItemStack stackInSlot) {
        RecipeBiomassBurner r = RecipeBiomassBurner.getRecipe(stackInSlot);
        if (r != null) return r.getBurnTime();
        return 0;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        progress = nbt.getInteger("Progres");
        maxProgres = nbt.getInteger("maxProgres");
        getInv().readFromNBT(nbt);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("Progres", progress);
        nbt.setInteger("maxProgres", maxProgres);
        getInv().writeToNBT(nbt);
    }

    @Override
    public void sendGUINetworkData(Container cont, ICrafting craft) {
        craft.sendProgressBarUpdate(cont, 0, progress);
        craft.sendProgressBarUpdate(cont, 1, maxProgres);
        craft.sendProgressBarUpdate(cont, 2, (int) heat.getTemperature());
    }

    @Override
    public void getGUINetworkData(int id, int value) {
        if (id == 0) progress = value;
        if (id == 1) maxProgres = value;
        if (id == 2) heat.setTemperature(value);
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

    public void openInventory() {
    }

    public void closeInventory() {
    }

    public boolean isItemValidForSlot(int a, ItemStack b) {
        return getInv().isItemValidForSlot(a, b);
    }

    public IBarProvider getBurningTimeBar() {
        return new IBarProvider() {

            @Override
            public String getMessage() {
                return null;
            }

            @Override
            public float getMaxLevel() {
                return maxProgres;
            }

            @Override
            public float getLevel() {
                return progress;
            }
        };
    }
}
