package us.drullk.thermalsmeltery.common.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mantle.blocks.abstracts.MultiItemBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import java.util.List;

public class ItemBlockRFSmeltery extends MultiItemBlock
{
	public static final String blockTypes[] = { "Controller", "Interface", "Block" };

	public ItemBlockRFSmeltery(Block b)
	{
		super(b, "RFSmeltery", blockTypes);
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
	{
		switch (stack.getItemDamage())
		{
			case 0:
				list.add(StatCollector.translateToLocal("rfsmeltery.controller.tooltip"));
				break;
			case 1:
				list.add(StatCollector.translateToLocal("rfsmeltery.interface.tooltip1"));
				list.add(StatCollector.translateToLocal("rfsmeltery.interface.tooltip2"));
				break;
			default:
				list.add(StatCollector.translateToLocal("rfsmeltery.block.tooltip1"));
				list.add(StatCollector.translateToLocal("rfsmeltery.block.tooltip2"));
				break;
		}
	}
}
