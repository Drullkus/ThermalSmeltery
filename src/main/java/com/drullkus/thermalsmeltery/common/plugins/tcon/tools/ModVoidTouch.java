package com.drullkus.thermalsmeltery.common.plugins.tcon.tools;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.tools.ToolCore;
import tconstruct.modifiers.tools.ModBoolean;

public class ModVoidTouch extends ModBoolean {

    public ModVoidTouch(ItemStack[] items, int effect, String tag, String c, String tip)
    {
        super(items, effect, tag, c, tip);
    }

    @Override
    protected boolean canModify(ItemStack tool, ItemStack[] input)
    {
        //POTENTAL API CHANGE: ItemModifier.addIncompatibilitiy(<modifierKey>)
        //If Boni accepts PR, this change will then be implented

        ToolCore toolitem = (ToolCore) tool.getItem();
        if (!validType(toolitem)) return false;

        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        if (tags.getBoolean("Silk Touch"))
            return false;
        if (tags.getBoolean("Lava"))
            return false;
        if (tags.getBoolean("Core Touch"))
            return false;
        return tags.getInteger("Modifiers") > 0 && !tags.getBoolean(key); //Will fail if the modifier is false or the tag doesn't exist
    }

    public boolean validType (ToolCore tool)
    {
        if(tool.getToolName().equals("Mattock") ||
                tool.getToolName().equals("Hatchet") ||
                tool.getToolName().equals("Broadsword") ||
                tool.getToolName().equals("Longsword") ||
                tool.getToolName().equals("Rapier") ||
                tool.getToolName().equals("Cutlass") ||
                tool.getToolName().equals("Cleaver") ||
                tool.getToolName().equals("Lumber Axe") ||
                tool.getToolName().equals("Scythe") ||
                tool.getToolName().equals("Pickaxe") )
        {
            return true;
        }

        return false;
    }
}
