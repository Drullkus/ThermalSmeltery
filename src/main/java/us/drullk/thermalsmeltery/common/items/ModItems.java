package us.drullk.thermalsmeltery.common.items;

import us.drullk.thermalsmeltery.ThermalSmeltery;
import us.drullk.thermalsmeltery.common.items.misc.ItemMod;
import us.drullk.thermalsmeltery.common.lib.LibItemNames;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ModItems {
	
	public static Item potatoesMashed;
	public static Item Tool_Mod_Void;
	
	public static void init() {
		
		Tool_Mod_Void = new ItemMod().setUnlocalizedName(LibItemNames.TOOLMOD_VOID);
		
		Item[] item = { Tool_Mod_Void };
        String[] itemStrings = { LibItemNames.TOOLMOD_VOID_NAME };
	    
	    for (int i = 0; i < item.length; i++)
        {
            GameRegistry.registerItem(item[i], itemStrings[i]);
        }
		
		ThermalSmeltery.itemTab.init(new ItemStack(ModItems.Tool_Mod_Void));
		
    }
	
}
