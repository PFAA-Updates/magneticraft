package com.cout970.magneticraft.util.multiblock.types;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import com.cout970.magneticraft.ManagerBlocks;
import com.cout970.magneticraft.api.util.MgDirection;
import com.cout970.magneticraft.api.util.VecInt;
import com.cout970.magneticraft.util.multiblock.MB_Register;
import com.cout970.magneticraft.util.multiblock.Mg_Component;
import com.cout970.magneticraft.util.multiblock.Multiblock;
import com.cout970.magneticraft.util.multiblock.SimpleComponent;

public class MultiblockSifter extends Multiblock{

	@Override
	public void init() {
		SimpleComponent a = new SimpleComponent(Blocks.air){
			public boolean isCorrect(World w, VecInt p, int x, int y, int z, Multiblock c, MgDirection e) {
				return true;
			}
		};
		SimpleComponent c = new SimpleComponent(ManagerBlocks.chassis);
		SimpleComponent i = new SimpleComponent(ManagerBlocks.multi_io);
		SimpleComponent e = new SimpleComponent(ManagerBlocks.multi_energy_low);
		SimpleComponent t = new SimpleComponent(ManagerBlocks.refinery_gap);
		SimpleComponent g = new SimpleComponent(ManagerBlocks.sifter);

		Mg_Component[][][] m = 
			{//     {{z2,z1,z0}x2,{z2,z1,z0}x1,{z2,z1,z0}x0}y0
				{ { e, c, a}, { c, c, c}, { a, c, i} },
				{ { c, g, a}, { t, t, t}, { a, t, t} },
				{ { a, a, a}, { a, t, t}, { a, t, t} }
			};

		VecInt p = new VecInt(-1,-1,0);
		x = m.length;
		y = m[0].length;
		z = m[0][0].length;
		matrix = m;
		tran = p;
	}

	@Override
	public int getID() {
		return MB_Register.ID_SIFTER;
	}

}
