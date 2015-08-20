package com.cout970.magneticraft;

import java.io.File;

import com.cout970.magneticraft.world.OreGenConfig;
import com.cout970.magneticraft.world.WorldGenManagerMg;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;

public class ManagerConfig {

	public static Configuration config;
	public static boolean ChunkLoading;

	public static void init(File file) {
		if(config == null){
			config = new Configuration(file);
			LoadConfigs();
		}
	}

	@SubscribeEvent
	public void onconfigurationChangeEvent(ConfigChangedEvent.OnConfigChangedEvent event){
		if(event.modID.equalsIgnoreCase(Magneticraft.ID)){
			LoadConfigs();
		}
	}

	public static void LoadConfigs() {
		config.load();

		WorldGenManagerMg.GenCopper = getOreConfig(config, 		"copper", 	10, 8, 80, 30);
		WorldGenManagerMg.GenTungsten = getOreConfig(config, 	"tungsten", 2, 	6, 10, 0);
		WorldGenManagerMg.GenSulfur = getOreConfig(config, 		"sulfur", 	3, 	8, 12, 0);
		WorldGenManagerMg.GenUranium = getOreConfig(config, 	"uranium", 	3, 	3, 80, 0);
		WorldGenManagerMg.GenThorium = getOreConfig(config, 	"thorium", 	5, 	6, 20, 0);
		WorldGenManagerMg.GenSalt = getOreConfig(config, 		"salt", 	6, 	8, 80, 0);
		WorldGenManagerMg.GenZinc = getOreConfig(config, 		"zinc", 	4, 	6, 80, 0);
		WorldGenManagerMg.GenOil = config.getBoolean("Oil Generation", Configuration.CATEGORY_GENERAL, true, "Should spawn oil in the world?");
		WorldGenManagerMg.GenOilProbability = config.getInt("Oil Generation Amount", Configuration.CATEGORY_GENERAL, 2000,200,50000, "How rare should oil be? Higher value means less oil.");
		WorldGenManagerMg.GenOilMaxHeight = config.getInt("Oil Generation Max Height", Configuration.CATEGORY_GENERAL, 30,0,256, "Max Height for a oil deposit");
		WorldGenManagerMg.GenOilMinHeight = config.getInt("Oil Generation Min Height", Configuration.CATEGORY_GENERAL, 10,0,256, "Min Height for a oil deposit");
		WorldGenManagerMg.GenOilMaxAmount = config.getInt("Oil Generation Max oil deposits", Configuration.CATEGORY_GENERAL, 9,1,16, "Max number of oil deposits nearby");
		ChunkLoading = config.getBoolean("Chunk Loading", Configuration.CATEGORY_GENERAL, true, "Should the miner load chunks?");
		if (config.hasChanged()) {
			config.save();
		}
	}

	private static OreGenConfig getOreConfig(Configuration conf, String name, int chunk,int vein, int max, int min) {
		boolean active = conf.getBoolean(name+"_gen_active", Configuration.CATEGORY_GENERAL, true, "Generation of " + name);
		int amount_per_chunk = conf.getInt(name+"_amount_chunk", Configuration.CATEGORY_GENERAL, chunk, 0, 20, "Number of veins of "+name+" per chunk");
		int amount_per_vein = conf.getInt(name+"_amount_vein", Configuration.CATEGORY_GENERAL, vein, 0, 20, "Max amount of blocks of "+name+" in a vein");
		int max_height = conf.getInt(name+"_max_height", Configuration.CATEGORY_GENERAL, max, 0, 256, "Max height for generation of "+name);
		int min_height = conf.getInt(name+"_min_height", Configuration.CATEGORY_GENERAL, min, 0, 256, "Min height for generation of "+name);
		return new OreGenConfig(active, amount_per_chunk, amount_per_vein, max_height, min_height);
	}
}
