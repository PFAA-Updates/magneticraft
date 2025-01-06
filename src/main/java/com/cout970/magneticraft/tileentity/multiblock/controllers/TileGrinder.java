package com.cout970.magneticraft.tileentity.multiblock.controllers;

import com.cout970.magneticraft.api.access.RecipeGrinder;
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
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

import java.util.List;

public class TileGrinder extends TileMB_Base implements IInventoryManaged, ISidedInventory, IGuiSync {

    public MachineElectricConductor cond = new MachineElectricConductor(this);
    private InventoryComponent inv = new InventoryComponent(this, 4, "Grinder");
    private InventoryComponent in;
    private InventoryComponent out;

    public static final int maxProgress = 100;
    private float progress;

    private double[] flow = new double[2];
    public int drawCounter;
    public float rotation;
    private long time;
    private boolean working;
    private boolean active_w;
    private AverageBar energy = new AverageBar(20);

    public void updateEntity() {
        super.updateEntity();
        if (drawCounter > 0) drawCounter--;
        if (!isActive()) return;
        if (worldObj.isRemote) return;
        if (worldObj.getTotalWorldTime() % 20 == 0) {
            if (working && !isWorking()) {
                setActive(true);
            } else if (!working && isWorking()) {
                setActive(false);
            }
        }
        energy.tick();
        if (worldObj.getTotalWorldTime() % 10 == 0) {
            catchDrops();
        }
        updateConductor();
        if (cond.getVoltage() >= ElectricConstants.MACHINE_WORK) {
            if (canCraft()) {
                double speed = TileConductorLow.getEfficiency(cond.getVoltage(), ElectricConstants.MACHINE_WORK, ElectricConstants.BATTERY_CHARGE);
                if (speed > 0) {
                    speed *= .5;
                    progress += speed;
                    energy.addValue((float) EnergyConverter.RFtoW(speed * 200));
                    cond.drainPower(EnergyConverter.RFtoW(speed * 200));
                    if (progress >= maxProgress) {
                        craft();
                        markDirty();
                        progress %= maxProgress;
                    }
                }
            } else {
                progress = 0;
            }
        }
        working = energy.getAverage() > 0;
        distributeItems();
    }

    @SuppressWarnings("rawtypes")
    private void catchDrops() {
        if (in == null) return;
        MgDirection dir = getDirection().opposite();
        VecInt vec1, vec2;
        vec1 = new VecInt(this).add(0, 4, 0).add(dir.toVecInt());
        vec2 = vec1.copy();
        vec1.add(-1, 0, -1);
        vec2.add(2, 1, 2);
        List l = worldObj.getEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getBoundingBox(vec1.getX(), vec1.getY(), vec1.getZ(), vec2.getX(), vec2.getY(), vec2.getZ()));
        if (l.isEmpty()) return;
        for (Object aL : l) {
            EntityItem e = (EntityItem) aL;
            ItemStack item = e.getEntityItem();
            if (InventoryUtils.dropIntoInventory(item, in)) {
                worldObj.removeEntity(e);
                e.setDead();
            } else {
                break;
            }
        }
    }

    private void setActive(boolean b) {
        active_w = b;
        sendUpdateToClient();
    }

    public boolean isWorking() {
        return active_w;
    }

    public boolean isActive() {
        return getBlockMetadata() > 5;
    }

    public InventoryComponent getInv() {
        return inv;
    }

    @Override
    public MgDirection getDirection() {
        return MgDirection.getDirection(getBlockMetadata());
    }

    public void updateConductor() {
        cond.recache();
        cond.iterate();
        MgDirection d = MgDirection.getDirection(getBlockMetadata()).opposite();
        VecInt vec = d.toVecInt().add(d.step(MgDirection.UP).toVecInt());
        TileEntity c = MgUtils.getTileEntity(this, vec);
        if (c instanceof IElectricTile) {
            valance((IElectricTile) c, 0);
        }
        vec = d.toVecInt().add(d.step(MgDirection.DOWN).toVecInt());
        c = MgUtils.getTileEntity(this, vec);
        if (c instanceof IElectricTile) {
            valance((IElectricTile) c, 1);
        }
    }

    public void valance(IElectricTile c, int i) {
        IElectricConductor[] comp = c.getConds(VecInt.NULL_VECTOR, 0);
        IElectricConductor cond2 = comp[0];
        ElectricConductor.valance(cond, cond2, flow, i);
    }

    private void distributeItems() {
        if (in == null) {
            MgDirection d = MgDirection.getDirection(getBlockMetadata()).opposite();
            VecInt v = d.toVecInt().add(new VecInt(0, 3, 0));
            TileEntity c = MgUtils.getTileEntity(this, v);
            if (c instanceof IInventoryManaged) {
                in = ((IInventoryManaged) c).getInv();
            }
        }
        if (out == null) {
            MgDirection d = MgDirection.getDirection(getBlockMetadata()).opposite();
            VecInt v = d.toVecInt().multiply(2);
            TileEntity c = MgUtils.getTileEntity(this, v);
            if (c instanceof IInventoryManaged) {
                out = ((IInventoryManaged) c).getInv();
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

    private void craft() {
        ItemStack a = getInv().getStackInSlot(0);
        RecipeGrinder r = RecipeGrinder.getRecipe(a);

        assert r != null;
        getInv().setInventorySlotContents(1,
                InventoryUtils.addition(r.getOutput(), getInv().getStackInSlot(1)));

        int intents = ((int) r.getProb2()) + 1;
        for (int i = 0; i < intents; i++) {
            if (worldObj.rand.nextFloat() <= r.getProb2() - i) {
                getInv().setInventorySlotContents(2, InventoryUtils.addition(r.getOutput2(), getInv().getStackInSlot(2)));
            }
        }

        intents = ((int) (r.getProb3() / 100)) + 1;
        for (int i = 0; i < intents; i++) {
            if (worldObj.rand.nextFloat() <= r.getProb3() - i) {
                getInv().setInventorySlotContents(3, InventoryUtils.addition(r.getOutput3(), getInv().getStackInSlot(3)));
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
        RecipeGrinder r = RecipeGrinder.getRecipe(a);
        if (r == null)
            return false;
        if (getInv().getStackInSlot(1) != null)
            if (!InventoryUtils.canCombine(r.getOutput(), getInv().getStackInSlot(1), getInv().getInventoryStackLimit()))
                return false;
        if (getInv().getStackInSlot(2) != null)
            if (!InventoryUtils.canCombine(r.getOutput2(), getInv().getStackInSlot(2), getInv().getInventoryStackLimit()))
                return false;
        if (getInv().getStackInSlot(3) != null)
            if (!InventoryUtils.canCombine(r.getOutput3(), getInv().getStackInSlot(3), getInv().getInventoryStackLimit()))
                return false;
        return true;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        active_w = nbt.getBoolean("Act");
        cond.load(nbt);
        getInv().readFromNBT(nbt);
    }

    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("Act", active_w);
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
        VecInt v2 = VecIntUtil.getRotatedOffset(getDirection().opposite(), 1, 3, 2);
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
                return maxProgress;
            }

            @Override
            public float getLevel() {
                return progress;
            }
        };
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
