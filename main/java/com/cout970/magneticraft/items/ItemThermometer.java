package com.cout970.magneticraft.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import com.cout970.magneticraft.api.heat.IHeatConductor;
import com.cout970.magneticraft.api.util.MgUtils;
import com.cout970.magneticraft.api.util.VecInt;

public class ItemThermometer extends ItemBasic{

	public ItemThermometer(String unlocalizedname) {
		super(unlocalizedname);
	}
	
	public boolean onItemUse(ItemStack item, EntityPlayer p, World w, int x, int y, int z, int side, float p_149727_7_, float p_149727_8_, float p_149727_9_)
	{
		if(w.isRemote)return false;
		TileEntity t = w.getTileEntity(x, y, z);
		IHeatConductor heat = MgUtils.getHeatCond(t, VecInt.NULL_VECTOR);
		if(heat != null){
			p.addChatMessage(new ChatComponentText("Temperature: "+MgUtils.format(heat.getTemperature())));
		}
//		p.addChatMessage(new ChatComponentText("Metadata: "+w.getBlockMetadata(x, y, z)));
		return false;
	}

}