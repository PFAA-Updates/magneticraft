package com.cout970.magneticraft.tabs;

import com.cout970.magneticraft.ManagerItems;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CreativeTabSteam extends CreativeTabs{

	public CreativeTabSteam(String s) {
		super(s);
	}

	@Override
	public Item getTabIconItem() {
		return ManagerItems.wrench;
	}

	@SideOnly(Side.CLIENT)
	public String getTranslatedTabLabel()
	{
		return "Magneticraft Steam Age";
	}
}