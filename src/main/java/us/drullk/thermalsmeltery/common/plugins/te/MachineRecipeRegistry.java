package us.drullk.thermalsmeltery.common.plugins.te;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.oredict.OreDictionary;
import tconstruct.library.crafting.CastingRecipe;
import tconstruct.library.crafting.LiquidCasting;
import tconstruct.plugins.gears.TinkerGears;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.weaponry.TinkerWeaponry;

import java.util.*;

public class MachineRecipeRegistry
{
    private static List<StampingRecipe> recipes = new ArrayList<StampingRecipe>();
    private static List<ItemStack> validIngots = new ArrayList<ItemStack>();
    private static Set<Integer> incompatibleCasts = new HashSet<Integer>(Arrays.asList(0, 23, 24, 26, 27)); //Ingot,Gem,Nugget,and blanks
    private static Map<Fluid, CastingRecipe> ingotRecipes = new HashMap<Fluid, CastingRecipe>();
    private static Map<Fluid, CastingRecipe> blockRecipes = new HashMap<Fluid, CastingRecipe>();

    public static void registerStampingRecipe(LiquidCasting tableCasting, CastingRecipe recipe)
    {
        if (isValidCast(recipe.cast))
        {
            StampingRecipe stampingRecipe = new StampingRecipe(tableCasting, recipe);
            if (stampingRecipe.metal != null)
            {
                recipes.add(stampingRecipe);
                if (!isValidMetal(stampingRecipe.metal))
                {
                    validIngots.add(stampingRecipe.metal);
                }
            }
        }
    }

    public static void registerIngotRecipe(CastingRecipe recipe)
    {
        if (recipe.cast != null && recipe.cast.isItemEqual(new ItemStack(TinkerSmeltery.metalPattern, 1, 0)))
        {
            ingotRecipes.put(recipe.castingMetal.getFluid(), recipe);
        }
    }

    public static void registerBlockRecipe(CastingRecipe recipe)
    {
        if (recipe.cast == null)
        {
            blockRecipes.put(recipe.castingMetal.getFluid(), recipe);
        }
    }

    public static CastingRecipe getExtruderRecipe(Fluid fluid, boolean block)
    {
        return block ? blockRecipes.get(fluid) : ingotRecipes.get(fluid);
    }

    public static boolean isValidFluid(Fluid fluid)
    {
        return blockRecipes.containsKey(fluid) || ingotRecipes.containsKey(fluid);
    }

    public static StampingRecipe getStampingRecipe(ItemStack ingot, ItemStack cast)
    {
        for (StampingRecipe recipe : recipes)
        {
            if (recipe.matches(ingot, cast)) return recipe;
        }
        return null;
    }

    public static boolean isValidCast(ItemStack stack)
    {
        if(stack==null)
			return false;
		if(stack.getItem() == TinkerSmeltery.metalPattern)
		{
			if(!incompatibleCasts.contains(stack.getItemDamage()))
				return true;
		}
		if(stack.getItem() == TinkerWeaponry.metalPattern)
			if(!(stack.getItemDamage() == 3)) // Damned Bow Limbs
				return true;
		if(stack.getItem() == TinkerGears.gearCast)
			return true;

        return false;
    }

    public static boolean isValidMetal(ItemStack stack)
    {
        if (stack == null) return false;
        for (ItemStack ingot : validIngots)
        {
            if (isOreDictMatch(stack, ingot)) return true;
        }
        return false;
    }

    public static boolean isOreDictMatch(ItemStack metal, ItemStack metal1)
    {
        int[] ids1 = OreDictionary.getOreIDs(metal);
        int[] ids2 = OreDictionary.getOreIDs(metal1);
        for (int id1 : ids1)
        {
            for (int id2 : ids2)
            {
                if (id1 == id2) return true;
            }
        }
        return false;
    }


}
