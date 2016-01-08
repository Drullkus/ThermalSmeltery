package us.drullk.thermalsmeltery.common.plugins.tcon.engineering;

import cpw.mods.fml.common.registry.GameRegistry;
import mantle.blocks.abstracts.MultiServantLogic;
import net.minecraft.block.Block;
import us.drullk.thermalsmeltery.common.blocks.RFSmelteryBlock;
import us.drullk.thermalsmeltery.common.items.ItemBlockRFSmeltery;
import us.drullk.thermalsmeltery.common.lib.LibMisc;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;
import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import us.drullk.thermalsmeltery.common.tile.TileRFSmelteryInterfaceLogic;
import us.drullk.thermalsmeltery.common.tile.TileRFSmelteryLogic;
import us.drullk.thermalsmeltery.common.tile.TileRFSmelteryServant;

@ObjectHolder(LibMisc.MOD_ID)
@Pulse(id = "TSmelt RF Smeltery",
	description = "Adds the RF Smeltery",
	modsRequired = "TConstruct")
public class TConEngineering
{
	public static Block RFSmeltery;

	@Handler
	public void preInit(FMLInitializationEvent event)
	{
		System.out.println("Thermal Engineering Initiated!");

		TConEngineering.RFSmeltery = new RFSmelteryBlock().setBlockName("RFSmeltery");

		GameRegistry.registerBlock(TConEngineering.RFSmeltery, ItemBlockRFSmeltery.class, "RFSmeltery");

		GameRegistry.registerTileEntity(TileRFSmelteryLogic.class, "TSmelt.RFSmeltery");
		GameRegistry.registerTileEntity(TileRFSmelteryInterfaceLogic.class, "TSmelt.RFSmelteryInterface");
		GameRegistry.registerTileEntity(TileRFSmelteryServant.class, "TSmelt.Servants");
	}

	@Handler
	public void init(FMLInitializationEvent event)
	{

	}

	@Handler
	public void postInit(FMLPostInitializationEvent event)
	{

	}
}
