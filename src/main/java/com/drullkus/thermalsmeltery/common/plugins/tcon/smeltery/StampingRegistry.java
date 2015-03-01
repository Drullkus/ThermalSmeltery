package com.drullkus.thermalsmeltery.common.plugins.tcon.smeltery;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import tconstruct.library.crafting.CastingRecipe;
import tconstruct.smeltery.TinkerSmeltery;

import java.util.*;

public class StampingRegistry
{
    private static List<StampingRecipe> recipes = new ArrayList<StampingRecipe>();
    private static List<ItemStack> validIngots = new ArrayList<ItemStack>();
    private static Set<Integer> incompatibleCasts = new HashSet<Integer>(Arrays.asList(0,23,24,26,27)); //Ingot,Gem,Nugget,and blanks

    public static void registerStampingRecipe(CastingRecipe recipe)
    {
        if (isValidCast(recipe.cast))
        {
            StampingRecipe stampingRecipe = new StampingRecipe(recipe);
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

    public static StampingRecipe getRecipe(ItemStack ingot, ItemStack cast)
    {
        for (StampingRecipe recipe : recipes)
        {
            if (recipe.matches(ingot,cast)) return recipe;
        }
        return null;
    }

    public static boolean isValidCast(ItemStack stack)
    {
        return stack != null && stack.getItem() == TinkerSmeltery.metalPattern && !incompatibleCasts.contains(stack.getItemDamage());
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
