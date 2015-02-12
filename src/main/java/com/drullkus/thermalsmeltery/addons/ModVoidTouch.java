package com.drullkus.thermalsmeltery.addons;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.modifiers.tools.ModBoolean;

public class ModVoidTouch extends ModBoolean {

    static String name = "Crooked";
    static String color = "\u00a77";
    static String tooltip = "Crooked";

    public ModVoidTouch(ItemStack[] items, int effect)
    {
        super(items, effect, name, color, tooltip);
    }

    @Override
    protected boolean canModify(ItemStack tool, ItemStack[] input)
    {
        //POTENTAL API CHANGE: ItemModifier.addIncompatibilitiy(<modifierKey>)
        //If Boni accepts PR, this change will then be implented

        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        if (tags.getBoolean("Silk Touch"))
            return false;
        if (tags.getBoolean("Lava"))
            return false;
        if (tags.getBoolean("Core Touch"))
            return false;
        return tags.getInteger("Modifiers") > 0 && !tags.getBoolean(key); //Will fail if the modifier is false or the tag doesn't exist
    }
}
