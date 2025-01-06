package com.cout970.magneticraft;

import com.cout970.magneticraft.compat.ManagerIntegration;
import com.cout970.magneticraft.compat.minetweaker.MgMinetweaker;
import com.cout970.magneticraft.handlers.GuiHandler;
import com.cout970.magneticraft.handlers.HandlerBuckets;
import com.cout970.magneticraft.handlers.SolidFuelHandler;
import com.cout970.magneticraft.proxy.IProxy;
import com.cout970.magneticraft.tileentity.TileMiner;
import com.cout970.magneticraft.util.Log;
import com.cout970.magneticraft.util.URLConnectionReader;
import com.cout970.magneticraft.util.energy.EnergyInterfaceFactory;
import com.cout970.magneticraft.util.multiblock.MB_Register;
import com.cout970.magneticraft.world.WorldGenManagerMg;
import com.google.common.collect.Lists;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModAPIManager;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.util.List;


@Mod(modid = Magneticraft.ID, name = Magneticraft.NAME, version = Magneticraft.VERSION, guiFactory = Magneticraft.GUI_FACTORY, dependencies = "required-after:ForgeMultipart;" +
        "after:BuildCraft|Core;after:CoFHCore;after:IC2;after:Railcraft;after:ImmersiveEngineering")
public class Magneticraft {

    public final static String ID = "Magneticraft";
    public final static String NAME = "Magneticraft";
    public final static String VERSION = "@VERSION@";
    public final static String MIN_JAVA = "Java 8";
    public final static String ENERGY_STORED_NAME = "J";
    public final static String GUI_FACTORY = "com.cout970.magneticraft.handlers.MgGuiFactory";
    public static final boolean DEBUG = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
    public static String DEV_HOME = null;

    @Instance(NAME)
    public static Magneticraft INSTANCE;

    @SidedProxy(clientSide = "com.cout970.magneticraft.proxy.ClientProxy",
            serverSide = "com.cout970.magneticraft.proxy.ServerProxy")
    public static IProxy proxy;

    public static ManagerMultiPart registry;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Log.init(event.getModLog());
        Log.info("Starting preInit");

        ManagerIntegration.searchCompatibilities();

        ManagerConfig.init(event.getSuggestedConfigurationFile());

        ManagerBlocks.initBlocks();
        ManagerBlocks.registerBlocks();
        ManagerBlocks.registerTileEntities();

        ManagerItems.initItems();
        ManagerItems.registerItems();

        ManagerFluids.initFluids();

        proxy.init();

        if (DEBUG) {
            //BEGIN FINDING OF SOURCE DIR
            File temp = event.getModConfigurationDirectory();
            while (temp != null && temp.isDirectory()) {
                if (new File(temp, "build.gradle").exists()) {
                    DEV_HOME = temp.getAbsolutePath();
                    Log.info("Found source code directory at " + DEV_HOME);
                    break;
                }
                temp = temp.getParentFile();
            }
            if (DEV_HOME == null) {
                throw new RuntimeException("Could not find source code directory!");
            }
            //END FINDING OF SOURCE DIR
            LangHelper.registerNames();
            LangHelper.setupLangFile();
        }

        ManagerOreDict.registerOreDict();
        Log.info("preInit Done");
    }

    @EventHandler
    public void load(FMLInitializationEvent event) {
        Log.info("Starting Init");

        NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new GuiHandler());
        MB_Register.init();
        EnergyInterfaceFactory.init();
        registry = new ManagerMultiPart();
        registry.init();
        GameRegistry.registerFuelHandler(new SolidFuelHandler());
        GameRegistry.registerWorldGenerator(new WorldGenManagerMg(), 11);
        ManagerCraft.init();
        ManagerRecipe.registerFurnaceRecipes();
        ManagerRecipe.registerThermopileRecipes();
        ManagerRecipe.registerBiomassBurnerRecipes();
        ManagerNetwork.registerMessages();
        ManagerFluids.registerFuels();
        HandlerBuckets.INSTANCE = new HandlerBuckets();
        MinecraftForge.EVENT_BUS.register(HandlerBuckets.INSTANCE);
        checkVersion();

        Log.info("Init Done");
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        Log.info("Starting postInit");

        if (Loader.isModLoaded("MineTweaker3")) {
            Log.info("Setting up MineTweaker3 compatibility");
            MgMinetweaker.init();
        }

        /*
    PFAA uses a dedicated petrochem line for oil processing. Disable the addition of MgC fuels to BC, IE, and RC so
    we have to refine our oil.

        if (ManagerIntegration.BUILDCRAFT) {
            ManagerFluids.registerBCFuels();
        }

        if (ManagerIntegration.RAILCRAFT) {
            ManagerFluids.registerRCFuels();
        }

        if (ManagerIntegration.IE) {
            ManagerFluids.registerIEFuels();
        }

         */

        ForgeChunkManager.setForcedChunkLoadingCallback(INSTANCE, new MinerChunkCallBack());
//		if(DEBUG)printOreDict();
        Log.info("postInit Done");
    }

//	private void printOreDict() {
//		for(String i : OreDictionary.getOreNames()){
//			Log.debug(i+" "+ManagerOreDict.getOre(i));
//		}
//	}

    public void checkVersion() {
        try {
            String version = URLConnectionReader.getText("https://raw.githubusercontent.com/cout970/Versions/master/MgVersion.txt");
            Log.info("Last Version: " + version);
            Log.info("Current Version: " + VERSION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void deleteOldBlocks(FMLMissingMappingsEvent event){
        event.get().stream().forEach(FMLMissingMappingsEvent.MissingMapping::ignore);
    }

    public class MinerChunkCallBack implements ForgeChunkManager.OrderedLoadingCallback {

        @Override
        public void ticketsLoaded(List<Ticket> tickets, World world) {
            for (Ticket ticket : tickets) {
                int x = ticket.getModData().getInteger("quarryX");
                int y = ticket.getModData().getInteger("quarryY");
                int z = ticket.getModData().getInteger("quarryZ");
                TileEntity tq = world.getTileEntity(x, y, z);
                if (tq instanceof TileMiner) {
                    ((TileMiner) tq).forceChunkLoading(ticket);
                }
            }
        }

        @Override
        public List<Ticket> ticketsLoaded(List<Ticket> tickets, World world, int maxTicketCount) {
            List<Ticket> validTickets = Lists.newArrayList();
            if (ManagerConfig.MINER_CHUNKLOADING) {
                for (Ticket ticket : tickets) {
                    int x = ticket.getModData().getInteger("quarryX");
                    int y = ticket.getModData().getInteger("quarryY");
                    int z = ticket.getModData().getInteger("quarryZ");

                    Block block = world.getBlock(x, y, z);
                    if (block == ManagerBlocks.miner) {
                        validTickets.add(ticket);
                    }
                }
            }
            return validTickets;
        }
    }
}
