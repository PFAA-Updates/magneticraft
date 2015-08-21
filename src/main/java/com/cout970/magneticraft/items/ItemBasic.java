package com.cout970.magneticraft.items;

import java.util.Locale;

import com.cout970.magneticraft.tabs.CreativeTabsMg;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemBasic extends Item{
	
	public static final String Base = "magneticraft:";
	
	public ItemBasic(String unlocalizedname, String texture){
		super();
		setUnlocalizedName(unlocalizedname);
		setCreativeTab(CreativeTabsMg.MainTab);
		setTextureName(Base+texture);
	}
	
	public ItemBasic(String unlocalizedname){
		this(unlocalizedname, unlocalizedname);
	}

	public String getUnlocalizedName(ItemStack i){
		return getUnlocalizedName();
	}
	
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister IR)
    {
        this.itemIcon = IR.registerIcon(this.getIconString().toLowerCase(Locale.US));
    }
}
