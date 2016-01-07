package us.drullk.thermalsmeltery.common.blocks;

import mantle.blocks.abstracts.InventoryBlock;
import mantle.blocks.abstracts.MultiServantLogic;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import us.drullk.thermalsmeltery.ThermalSmeltery;
import us.drullk.thermalsmeltery.common.tile.TileRFSmelteryInterfaceLogic;
import us.drullk.thermalsmeltery.common.tile.TileRFSmelteryLogic;
import us.drullk.thermalsmeltery.common.tile.TileRFSmelteryServant;

public class RFSmelteryBlock extends InventoryBlock implements IRFSmeltery
{
	public RFSmelteryBlock()
	{
		super(Material.iron);
		setHardness(3F);
		setResistance(20F);
		setStepSound(soundTypeMetal);
		this.setCreativeTab(ThermalSmeltery.itemTab);
		this.setBlockName("tsmelt.RFSmeltery");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		switch(metadata)
		{
			case 0:
				return new TileRFSmelteryLogic();
			case 1:
				return new TileRFSmelteryInterfaceLogic();
			default:
				return new TileRFSmelteryServant();
		}
	}

	@Override
	public Integer getGui(World world, int x, int y, int z, EntityPlayer entityplayer)
	{
		return -1;
	}

	@Override
	public Object getModInstance()
	{
		return ThermalSmeltery.instance;
	}

	@Override
	public String[] getTextureNames()
	{
		return new String[0]; // Mantle's custom handler. Screw that, yo.
	}

	@Override
	public String getTextureDomain(int textureNameIndex)
	{
		return null; // Mantle's custom handler. Screw that, yo.
	}

	@Override
	public int damageDropped (int meta)
	{
		return meta;
	}


}
