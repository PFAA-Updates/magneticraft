package com.cout970.magneticraft.util.multiblock;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.cout970.magneticraft.api.util.BlockPosition;
import com.cout970.magneticraft.api.util.MgDirection;
import com.cout970.magneticraft.api.util.VecInt;

public class MutableComponent {

	public List<Block> blocks = new ArrayList<Block>();
	
	public MutableComponent(Block b){
		blocks.add(b);
	}
	
	public MutableComponent(Block a, Block b) {
		blocks.add(a);
		blocks.add(b);
	}
	
	public MutableComponent(Block... a) {
		for(Block b : a)
		blocks.add(b);
	}

	public boolean isCorrect(World w, BlockPosition p, int x, int y, int z, Multiblock c, MgDirection e) {
		VecInt te = c.translate(x, y, z, e);
		Block t = w.getBlock(te.getX()+p.getX(), te.getY()+p.getY(), te.getZ()+p.getZ());
		if(!blocks.contains(t)){
//			w.setBlock(te.getX()+p.getX(), te.getY()+p.getY(), te.getZ()+p.getZ(), blocks.get(0));
			return false;
		}
		return true;
	}

	public void establish(World w, BlockPosition p, int x, int y, int z,Multiblock c, MgDirection e) {
		VecInt te = c.translate(x, y, z, e);
		Block t = w.getBlock(te.getX()+p.getX(), te.getY()+p.getY(), te.getZ()+p.getZ());
		if(t instanceof MB_Block){
			((MB_Block) t).mutates(w,new BlockPosition(te.getX()+p.getX(), te.getY()+p.getY(), te.getZ()+p.getZ()),c,e);
		}
		TileEntity tile = w.getTileEntity(te.getX()+p.getX(), te.getY()+p.getY(), te.getZ()+p.getZ());
		if(tile instanceof MB_Tile){
			((MB_Tile) tile).setControlPos(p);
			((MB_Tile) tile).setDirection(e);
			((MB_Tile) tile).setMultiblock(c);
			((MB_Tile) tile).onActivate(w,p,c,e);
		}
	}

	public void destroy(World w, BlockPosition p, int x, int y, int z, Multiblock c, MgDirection e) {
		VecInt te = c.translate(x, y, z, e);
		TileEntity tile = w.getTileEntity(te.getX()+p.getX(), te.getY()+p.getY(), te.getZ()+p.getZ());
		if(tile instanceof MB_Tile){
			((MB_Tile) tile).onDestroy(w,p,c,e);
			((MB_Tile) tile).setControlPos(null);
			((MB_Tile) tile).setMultiblock(null);
			((MB_Tile) tile).setDirection(null);
		}
		Block t = w.getBlock(te.getX()+p.getX(), te.getY()+p.getY(), te.getZ()+p.getZ());
		if(t instanceof MB_Block){
			((MB_Block) t).destroy(w,new BlockPosition(te.getX()+p.getX(), te.getY()+p.getY(), te.getZ()+p.getZ()),c,e);
		}
	}

	public String getErrorMesage() {
		return "with the block: "+blocks.get(0).getLocalizedName();
	}

}