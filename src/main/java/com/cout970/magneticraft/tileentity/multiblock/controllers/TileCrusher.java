package com.cout970.magneticraft.tileentity.multiblock.controllers;

import com.cout970.magneticraft.api.access.RecipeCrusher;
import com.cout970.magneticraft.api.electricity.ElectricConstants;
import com.cout970.magneticraft.api.electricity.IElectricConductor;
import com.cout970.magneticraft.api.electricity.IElectricTile;
import com.cout970.magneticraft.api.electricity.prefab.ElectricConductor;
import com.cout970.magneticraft.api.util.*;
import com.cout970.magneticraft.client.gui.component.IBarProvider;
import com.cout970.magneticraft.client.gui.component.IGuiSync;
import com.cout970.magneticraft.tileentity.TileBase;
import com.cout970.magneticraft.tileentity.multiblock.TileMB_Base;
import com.cout970.magneticraft.util.IInventoryManaged;
import com.cout970.magneticraft.util.InventoryComponent;
import com.cout970.magneticraft.util.InventoryUtils;
import com.cout970.magneticraft.util.tile.AverageBar;
import com.cout970.magneticraft.util.tile.MachineElectricConductor;
import com.cout970.magneticraft.util.tile.TileConductorLow;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileCrusher extends TileMB_Base implements IGuiSync, IInventoryManaged, ISidedInventory {

    public float animation;
    public float progress = 0;
    public MachineElectricConductor cond = new MachineElectricConductor(this);
    public int drawCounter;
    public boolean working;
    private float MAX_PROGRESS = 100;
    private double[] flow = new double[1];
    private InventoryComponent inv = new InventoryComponent(this, 4, "Crusher");
    private InventoryComponent in;
    private InventoryComponent out;
    private long time;
    private AverageBar energy = new AverageBar(20);

    public InventoryComponent getInv() {
        return inv;
    }

    public void updateEntity() {
        super.updateEntity();

        if (drawCounter > 0) drawCounter--;
        if (!isActive()) return;
        if (worldObj.isRemote) return;
        if (worldObj.getTotalWorldTime() % 20 == 0) sendUpdateToClient();
        updateConductor();
        energy.tick();
        if (cond.getVoltage() >= ElectricConstants.MACHINE_WORK) {
            if (canCraft()) {
                double speed = TileConductorLow.getEfficiency(cond.getVoltage(), ElectricConstants.MACHINE_WORK, ElectricConstants.BATTERY_CHARGE);
                if (speed > 0) {
                    speed *= .5;
                    progress += speed;
                    energy.addValue((float) EnergyConverter.RFtoW(speed * 200));
                    cond.drainPower(EnergyConverter.RFtoW(speed * 800));
                    if (progress >= MAX_PROGRESS) {
                        craft();
                        markDirty();
                        progress %= MAX_PROGRESS;
                    }
                }
            } else {
                progress = 0;
            }
        }
        working = energy.getAverage() > 0;
        distributeItems();
    }

    private void distributeItems() {
        if (in == null) {
            if (getBlockMetadata() % 8 >= 4) {
                MgDirection d = getDirection().opposite();
                VecInt v = d.toVecInt().multiply(2).add(d.step(MgDirection.UP).toVecInt().getOpposite());
                TileEntity c = MgUtils.getTileEntity(this, v);
                if (c instanceof IInventoryManaged) {
                    in = ((IInventoryManaged) c).getInv();
                }
            } else {
                MgDirection d = getDirection().opposite();
                VecInt v = d.toVecInt().multiply(2).add(d.step(MgDirection.DOWN).toVecInt().getOpposite());
                TileEntity c = MgUtils.getTileEntity(this, v);
                if (c instanceof IInventoryManaged) {
                    in = ((IInventoryManaged) c).getInv();
                }
            }
        }
        if (out == null) {
            if (getBlockMetadata() % 8 < 4) {
                MgDirection d = getDirection().opposite();
                VecInt v = d.toVecInt().multiply(2).add(d.step(MgDirection.UP).toVecInt().multiply(3).getOpposite());
                TileEntity c = MgUtils.getTileEntity(this, v);
                if (c instanceof IInventoryManaged) {
                    out = ((IInventoryManaged) c).getInv();
                }
            } else {
                MgDirection d = getDirection().opposite();
                VecInt v = d.toVecInt().multiply(2).add(d.step(MgDirection.DOWN).toVecInt().multiply(3).getOpposite());
                TileEntity c = MgUtils.getTileEntity(this, v);
                if (c instanceof IInventoryManaged) {
                    out = ((IInventoryManaged) c).getInv();
                }
            }
        }

        if (in != null && out != null) {
            if (((TileBase) in.tile).isControlled()) {
                if (getInv().getStackInSlot(0) != null) {
                    int s = InventoryUtils.findCombination(in, getInv().getStackInSlot(0));
                    if (s != -1) {
                        InventoryUtils.traspass(in, this, s, 0);
                    }
                } else {
                    setInventorySlotContents(0, InventoryUtils.getItemStack(in));
                }
            }
            if (((TileBase) out.tile).isControlled()) {
                for (int i = 0; i < 3; i++) {
                    if (getInv().getStackInSlot(i + 1) != null) {
                        int s = InventoryUtils.getSlotForStack(out, getInv().getStackInSlot(i + 1));
                        if (s != -1) {
                            InventoryUtils.traspass(this, out, i + 1, s);
                        }
                    }
                }
            }
        }
    }

    @Override
    public MgDirection getDirection() {
        int meta = getBlockMetadata();
        return MgDirection.getDirection(meta % 4 + 2);
    }

    @SuppressWarnings("ConstantConditions")
    private void craft() {
        ItemStack a = getInv().getStackInSlot(0);
        RecipeCrusher r = RecipeCrusher.getRecipe(a);

        getInv().setInventorySlotContents(1,
                InventoryUtils.addition(r.getOutput(), getInv().getStackInSlot(1)));

        int intents = ((int) (r.getProb2())) + 1;
        for (int i = 0; i < intents; i++) {
            if (worldObj.rand.nextFloat() <= r.getProb2() - i) {
                getInv().setInventorySlotContents(
                        2,
                        InventoryUtils.addition(r.getOutput2(), getInv()
                                .getStackInSlot(2)));
            }
        }

        intents = ((int) (r.getProb3())) + 1;
        for (int i = 0; i < intents; i++) {
            if (worldObj.rand.nextFloat() <= r.getProb3() - i) {
                getInv().setInventorySlotContents(
                        3,
                        InventoryUtils.addition(r.getOutput3(), getInv()
                                .getStackInSlot(3)));
            }
        }

        a.stackSize--;
        if (a.stackSize <= 0) {
            getInv().setInventorySlotContents(0, null);
        }
    }

    private boolean canCraft() {
        ItemStack a = getInv().getStackInSlot(0);
        if (a == null)
            return false;
        RecipeCrusher r = RecipeCrusher.getRecipe(a);
        if (r == null)
            return false;
        if (getInv().getStackInSlot(1) != null)
            if (!InventoryUtils.canCombine(r.getOutput(),
                    getInv().getStackInSlot(1), getInv()
                            .getInventoryStackLimit()))
                return false;
        if (getInv().getStackInSlot(2) != null)
            if (!InventoryUtils.canCombine(r.getOutput2(), getInv()
                    .getStackInSlot(2), getInv().getInventoryStackLimit()))
                return false;
        if (getInv().getStackInSlot(3) != null)
            if (!InventoryUtils.canCombine(r.getOutput3(), getInv()
                    .getStackInSlot(3), getInv().getInventoryStackLimit()))
                return false;
        return true;
    }

    public void updateConductor() {
        cond.recache();
        cond.iterate();
        MgDirection d = getDirection().opposite();
        TileEntity c = MgUtils.getTileEntity(this, d.toVecInt().multiply(3));
        if (c instanceof IElectricTile) {
            IElectricConductor[] comp = ((IElectricTile) c).getConds(VecInt.NULL_VECTOR, 0);
            IElectricConductor cond2 = comp[0];
            ElectricConductor.valance(cond, cond2, flow, 0);
        }
    }

    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        working = nbt.getBoolean("W");
        flow[0] = nbt.getDouble("F");
        cond.load(nbt);
        getInv().readFromNBT(nbt);
    }

    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("W", working);
        nbt.setDouble("F", flow[0]);
        cond.save(nbt);
        getInv().writeToNBT(nbt);
    }

    @Override
    public void sendGUINetworkData(Container cont, ICrafting craft) {
        craft.sendProgressBarUpdate(cont, 0, (int) cond.getVoltage());
        craft.sendProgressBarUpdate(cont, 1, (int) energy.getAverage()*16);
        craft.sendProgressBarUpdate(cont, 3, (int) progress);
    }

    @Override
    public void getGUINetworkData(int id, int value) {
        switch (id) {
            case 0:
                cond.setVoltage(value);
                break;
            case 1:
                energy.setStorage(value/16f);
                break;
            case 3:
                progress = value;
                break;
        }
    }

    @Override
    public void onNeigChange() {
        super.onNeigChange();
        cond.disconnect();
        in = null;
        out = null;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
        return new int[]{};
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

    public void openInventory() {
    }

    public void closeInventory() {
    }

    public boolean isItemValidForSlot(int a, ItemStack b) {
        return getInv().isItemValidForSlot(a, b);
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        VecInt v1 = VecIntUtil.getRotatedOffset(getDirection().opposite(), -1, -1, 0);
        VecInt v2 = VecIntUtil.getRotatedOffset(getDirection().opposite(), 3, 1, 3);
        VecInt block = new VecInt(xCoord, yCoord, zCoord);

        return VecIntUtil.getAABBFromVectors(v1.add(block), v2.add(block));
    }

    public float getDelta() {
        long aux = time;
        time = System.nanoTime();
        return time - aux;
    }

    public IBarProvider getProgressBar() {
        return new IBarProvider() {

            @Override
            public String getMessage() {
                return null;
            }

            @Override
            public float getMaxLevel() {
                return MAX_PROGRESS;
            }

            @Override
            public float getLevel() {
                return progress;
            }
        };
    }

    public boolean isActive() {
        return getBlockMetadata() >= 8;
    }

    public IBarProvider getConsumptionBar() {
        return new IBarProvider() {
            @Override
            public String getMessage() {
                return String.format("Consumption %.3f kW", energy.getStorage()/1000);
            }

            @Override
            public float getLevel() {
                return energy.getStorage();
            }

            @Override
            public float getMaxLevel() {
                return (float) EnergyConverter.RFtoW(100);
            }
        };
    }
}
