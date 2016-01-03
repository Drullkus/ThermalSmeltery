package us.drullk.thermalsmeltery.common.core.handler;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ModCreativeTab extends CreativeTabs
{
    ItemStack display;

    public ModCreativeTab(String label)
    {
        super(label);
    }

    public void init (ItemStack stack)
    {
        display = stack;
    }

    public ItemStack getIconItemStack ()
    {
        return display;
    }

    public Item getTabIconItem ()
    {
        return display.getItem();
    }
}
