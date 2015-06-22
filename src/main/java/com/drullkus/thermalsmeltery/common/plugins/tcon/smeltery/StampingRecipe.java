package com.drullkus.thermalsmeltery.common.plugins.tcon.smeltery;

import net.minecraft.item.ItemStack;
import tconstruct.library.crafting.CastingRecipe;
import tconstruct.library.crafting.LiquidCasting;
import tconstruct.smeltery.TinkerSmeltery;

public class StampingRecipe
{
    public ItemStack output;
    public ItemStack secondaryResult;
    public ItemStack metal;
    public ItemStack cast;
    public int coolTime;

    public StampingRecipe(ItemStack replacement, ItemStack metal, ItemStack cast, int delay)
    {
        this.metal = metal;
        this.cast = cast;
        output = replacement;
        coolTime = delay;
    }

    public StampingRecipe(LiquidCasting tableCasting, CastingRecipe recipe)
    {
        this.output = recipe.output.copy();
        this.cast = recipe.cast.copy();
        this.coolTime = recipe.coolTime;
        float value = 0;
        try
        {
            this.metal = tableCasting.getCastingRecipe(recipe.castingMetal, new ItemStack(TinkerSmeltery.metalPattern, 1, 0)).getResult();
            value = (float)tableCasting.getCastingAmount(recipe.castingMetal, recipe.cast)/tableCasting.getCastingAmount(recipe.castingMetal, new ItemStack(TinkerSmeltery.metalPattern, 1, 0));
            metal.stackSize = (int)Math.max(Math.floor(value), 1);
            if (value>1)
                value-=metal.stackSize;
        }catch(NullPointerException e)
        {
            this.metal = null; //Ender Pearls -> Frying Pan?
        }

        if (value > 0.1)
        {
            secondaryResult = output.copy();
            secondaryResult.stackSize /= value;
            secondaryResult.stackSize -= output.stackSize;
            if (secondaryResult.stackSize <= 0) secondaryResult = null;
        }
    }

    public boolean matches(ItemStack metal, ItemStack inputCast)
    {
        return MachineRecipeRegistry.isOreDictMatch(this.metal, metal) && metal.stackSize >= this.metal.stackSize && ((cast != null && cast.getItemDamage() == Short.MAX_VALUE && inputCast.getItem() == cast.getItem()) || ItemStack.areItemStacksEqual(this.cast, inputCast));
    }

    public ItemStack getMainResult()
    {
        return output.copy();
    }

    public ItemStack getSecondaryResult()
    {
        return secondaryResult==null?null:secondaryResult.copy();
    }
}
