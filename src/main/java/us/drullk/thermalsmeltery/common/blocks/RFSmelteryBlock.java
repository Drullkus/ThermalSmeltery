package us.drullk.thermalsmeltery.common.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mantle.blocks.abstracts.InventoryBlock;
import mantle.blocks.iface.IFacingLogic;
import mantle.blocks.iface.IServantLogic;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import us.drullk.thermalsmeltery.ThermalSmeltery;
import us.drullk.thermalsmeltery.client.RFSmelteryRenderer;
import us.drullk.thermalsmeltery.common.lib.LibMisc;
import us.drullk.thermalsmeltery.common.tile.TileRFSmelteryInterfaceLogic;
import us.drullk.thermalsmeltery.common.tile.TileRFSmelteryLogic;
import us.drullk.thermalsmeltery.common.tile.TileRFSmelteryServant;

import java.util.List;

public class RFSmelteryBlock extends InventoryBlock implements IRFSmeltery
{
	String[] textureNames = {"smelteryControllerOff", "smelteryControllerOn", "smelteryHatch", "smeltery"};

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
	public int getRenderType()
	{
		return RFSmelteryRenderer.RFSmelteryModel;
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
	public void getSubBlocks(Item id, CreativeTabs tab, List list)
	{
		for(int c = 0; c < 12; c++)
		{
			list.add(new ItemStack(id, 1, c));
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block blockID, int meta)
	{
		TileEntity logic = world.getTileEntity(x, y, z);
		if(logic instanceof IServantLogic)
		{
			((IServantLogic) logic).notifyMasterOfChange();
		}
		super.breakBlock(world, x, y, z, blockID, meta);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		if(meta == 0)
		{
			return icons[0];
		}
		else if(meta == 1)
		{
			return icons[2];
		}
		else
		{
			return icons[3];
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
	{
		TileEntity logic = world.getTileEntity(x, y, z);

		short direction = (logic instanceof IFacingLogic) ? ((IFacingLogic) logic).getRenderDirection() : 0;

		int meta = world.getBlockMetadata(x, y, z);

		if(meta == 0) // Smeltery
		{
			if(side == direction)
			{
				if(isActive(world, x, y, z))
				{
					return icons[1];
				}
				else
				{
					return icons[0];
				}
			}
			else
			{
				return icons[3];
			}
		}
		else if(meta == 1)
		{
			return icons[2];
		}

		return icons[3];
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
		for(int i = 0; i < textureNames.length; i++)
		{
			textureNames[i] = "smeltery/" + textureNames[i];
		}

		return textureNames;
	}

	@Override
	public String getTextureDomain(int textureNameIndex)
	{
		return LibMisc.MOD_ID;
	}

	@Override
	public int damageDropped(int meta)
	{
		return meta;
	}

	public boolean hasComparatorInputOverride()
	{
		return true;
	}

	public int getComparatorInputOverride(World world, int x, int y, int z, int comparatorSide)
	{
		int meta = world.getBlockMetadata(x, y, z);

		if(meta == 0)
		{
			return Container.calcRedstoneFromInventory(((TileRFSmelteryLogic) world.getTileEntity(x, y, z)));
		}
		return 0;
	}
}
