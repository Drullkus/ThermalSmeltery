package com.drullkus.thermalsmeltery.common.plugins.tcon.tools;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.tools.ToolCore;
import tconstruct.modifiers.tools.ModBoolean;

public class ModVoidTouch extends ModBoolean
{

	public ModVoidTouch(ItemStack[] items, int effect, String tag, String c, String tip)
	{
		super(items, effect, tag, c, tip);
	}

	@Override
	protected boolean canModify(ItemStack tool, ItemStack[] input)
	{
		//POTENTAL API CHANGE: ItemModifier.addIncompatibilitiy(<modifierKey>)
		//If Boni accepts PR, this change will then be implented

		if (tool.getItem() instanceof ToolCore)
		{
			ToolCore toolitem = (ToolCore) tool.getItem();
			if (!this.validType(toolitem))
			{
				return false;
			}

			NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
			if (tags.getBoolean("Silk Touch"))
			{
				return false;
			}
			if (tags.getBoolean("Lava"))
			{
				return false;
			}
			if (tags.getBoolean("Core Touch"))
			{
				return false;
			}
			return tags.getInteger("Modifiers") > 0 && !tags.getBoolean(this.key); //Will fail if the modifier is false or the tag doesn't exist
		}
		return false;
	}

	public boolean validType(ToolCore tool)
	{
		if (tool.getToolName().equals("Mattock") ||
				tool.getToolName().equals("Hatchet") ||
				tool.getToolName().equals("Shovel") ||
				tool.getToolName().equals("Excavator") ||
				tool.getToolName().equals("Battle Axe") ||
				tool.getToolName().equals("Lumber Axe") ||
				tool.getToolName().equals("Pickaxe"))
		{
			return true;
		}

		return false;
	}
}
