package com.cout970.magneticraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.cout970.magneticraft.Magneticraft;
import com.cout970.magneticraft.tileentity.TileMonitor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockMonitor extends BlockMg{

	public BlockMonitor() {
		super(Material.iron);
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileMonitor();
	}
	
	public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer p, int side, float p_149727_7_, float p_149727_8_, float p_149727_9_){
		if(p.isSneaking())return false;
		p.openGui(Magneticraft.Instance, 0, w, x, y, z);
		return true;
	}

	@Override
	public String[] getTextures() {
		return new String[]{"monitor","monitor_head"};
	}

	@Override
	public String getName() {
		return "monitor";
	}
	
	@SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
		if(meta == 0) return side == 3 ? icons[1] : icons[0];
        return side == meta ? icons[1] : icons[0];
    }
	
	public void onBlockPlacedBy(World w, int x, int y, int z, EntityLivingBase p, ItemStack i){
		int l = MathHelper.floor_double((double)(p.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		if (l == 0){
			w.setBlockMetadataWithNotify(x, y, z, 2, 2);
		}if (l == 1){
			w.setBlockMetadataWithNotify(x, y, z, 5, 2);
		}if (l == 2){
			w.setBlockMetadataWithNotify(x, y, z, 3, 2);
		}if (l == 3){
			w.setBlockMetadataWithNotify(x, y, z, 4, 2);
		}
	}
}