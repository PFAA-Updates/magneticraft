package com.cout970.magneticraft.util;

import com.cout970.magneticraft.api.conveyor.IConveyorBelt;
import com.cout970.magneticraft.api.conveyor.prefab.ItemBox;
import com.cout970.magneticraft.api.util.MgDirection;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

public class MgBeltUtils {

    public static int dropItemStackIntoInventory(IInventory v, ItemStack stack, MgDirection dir, boolean simulated) {
        ItemStack it = stack.copy();

        if (v instanceof ISidedInventory) {
            ISidedInventory s = (ISidedInventory) v;
            if (s.getAccessibleSlotsFromSide(dir.ordinal()) == null) return it.stackSize;
            for (int i : s.getAccessibleSlotsFromSide(dir.ordinal())) {
                if ((s.getStackInSlot(i) != null) && s.getStackInSlot(i).getItem().equals(it.getItem()) && s.canInsertItem(i, it, dir.ordinal()) && s.isItemValidForSlot(i, stack)) {
                    int noAccepted = placeInSlot(v, i, it, simulated);
                    if (noAccepted == 0) return 0;
                    it.stackSize = noAccepted;
                }
            }

            for (int i : s.getAccessibleSlotsFromSide(dir.ordinal())) {
                if (s.canInsertItem(i, it, dir.ordinal())) {
                    int noAccepted = placeInSlot(v, i, it, simulated);
                    if (noAccepted == 0) return 0;
                    it.stackSize = noAccepted;
                }
            }
        } else {
            for (int i = 0; i < v.getSizeInventory(); i++) {
                if ((v.getStackInSlot(i) == null) || (!v.getStackInSlot(i).getItem().equals(it.getItem()))) {
                    continue;
                }
                int noAccepted = placeInSlot(v, i, it, simulated);
                if (noAccepted == 0) {
                    return 0;
                }
                it.stackSize = noAccepted;
            }

            for (int i = 0; i < v.getSizeInventory(); i++) {
                int noAccepted = placeInSlot(v, i, it, simulated);
                if (noAccepted == 0) {
                    return 0;
                }
                it.stackSize = noAccepted;
            }
        }
        return it.stackSize;
    }

    public static int placeInSlot(IInventory a, int slot, ItemStack b, boolean simulated) {
        if (!a.isItemValidForSlot(slot, b)) {
            return b.stackSize;
        }
        ItemStack c = a.getStackInSlot(slot);
        if (c == null) {
            int accepted = Math.min(a.getInventoryStackLimit(), b.stackSize);
            if (!simulated) {
                ItemStack d = b.copy();
                d.stackSize = accepted;
                a.setInventorySlotContents(slot, d);
                a.markDirty();
            }
            return b.stackSize - accepted;
        } else if (matchesAll(c, b)) {
            int space = Math.min(c.getMaxStackSize(), a.getInventoryStackLimit()) - c.stackSize;
            int accepted = Math.min(space, b.stackSize);
            if (!simulated) {
                ItemStack d = c.copy();
                d.stackSize += accepted;
                a.setInventorySlotContents(slot, d);
                a.markDirty();
            }
            return b.stackSize - accepted;
        }
        return b.stackSize;
    }

    private static boolean matchesAll(ItemStack a, ItemStack b) {
        return ItemStack.areItemStackTagsEqual(a, b) && a.isItemEqual(b);
    }

    public static int getSlotWithItemStack(IInventory t, MgDirection dir, boolean allowEmpty) {
        if (t instanceof ISidedInventory) {
            if (((ISidedInventory) t).getAccessibleSlotsFromSide(dir.ordinal()) == null) return -1;
            for (int i : ((ISidedInventory) t).getAccessibleSlotsFromSide(dir.ordinal())) {
                if ((t.getStackInSlot(i) != null) && (allowEmpty || (t.getStackInSlot(i).stackSize > 0)) && ((ISidedInventory) t).canExtractItem(i, t.getStackInSlot(i), dir.ordinal())) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < t.getSizeInventory(); i++) {
                if (t.getStackInSlot(i) != null) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static boolean canInjectInBelt(IConveyorBelt t, ItemBox box, MgDirection dir) {
        boolean var = dir.isPerpendicular(t.getDir()) ? (dir == t.getDir().step(MgDirection.UP) ? !box.isOnLeft() : box.isOnLeft()) : dir != t.getDir();
        return t.addItem(dir, var ? 0 : 2, box, true);
    }
}
