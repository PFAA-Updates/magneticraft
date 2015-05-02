package com.cout970.magneticraft.handlers;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.cout970.magneticraft.ManagerBlocks;
import com.cout970.magneticraft.ManagerFluids;
import com.cout970.magneticraft.ManagerItems;
import com.cout970.magneticraft.items.ItemBucket;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class HandlerBuckets {

	public static HandlerBuckets INSTANCE;

	public FluidStack getFluid(ItemStack item) {
		Item i = item.getItem();
		if(i instanceof ItemBucket){
			if(((ItemBucket) i).fluid == "oil") return FluidRegistry.getFluidStack("oil",1000);
			if(((ItemBucket) i).fluid == "lightoil") return FluidRegistry.getFluidStack("lightoil",1000);
			if(((ItemBucket) i).fluid == "heavyoil") return FluidRegistry.getFluidStack("heavyoil",1000);	
		}
		return null;
	}

    public Map<Block, Item> buckets = new HashMap<Block, Item>();

    public HandlerBuckets(){
    	buckets.put(ManagerFluids.oilBlock, ManagerItems.bucket_oil);
    	buckets.put(ManagerFluids.lightOilBlock, ManagerItems.bucket_light_oil);
    	buckets.put(ManagerFluids.heavyOilBlock, ManagerItems.bucket_heavy_oil);
    	
    	FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluidStack("oil", FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(ManagerItems.bucket_oil), new ItemStack(Items.bucket));
    	FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluidStack("lightoil", FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(ManagerItems.bucket_light_oil), new ItemStack(Items.bucket));
    	FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluidStack("heavyoil", FluidContainerRegistry.BUCKET_VOLUME), new ItemStack(ManagerItems.bucket_heavy_oil), new ItemStack(Items.bucket));
    }
    
    @SubscribeEvent
    public void onBucketFill(FillBucketEvent event) {

            ItemStack result = fillCustomBucket(event.world, event.target);

            if (result == null) return;

            event.result = result;
            event.setResult(Result.ALLOW);
    }

    private ItemStack fillCustomBucket(World world, MovingObjectPosition pos) {

    	Block block = world.getBlock(pos.blockX, pos.blockY, pos.blockZ);

    	Item bucket = buckets.get(block);
    	if (bucket != null && world.getBlockMetadata(pos.blockX, pos.blockY, pos.blockZ) == 0) {
    		world.setBlockToAir(pos.blockX, pos.blockY, pos.blockZ);
    		return new ItemStack(bucket);
    	} else
    		return null;
    }
}