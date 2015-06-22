package com.drullkus.thermalsmeltery.common.gui.elements;

import cofh.lib.gui.GuiBase;
import cofh.lib.gui.element.ElementFluidTank;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import tconstruct.TConstruct;
import tconstruct.smeltery.TinkerSmeltery;

import java.util.List;

public class ElementTinkersTank extends ElementFluidTank
{
    public ElementTinkersTank(GuiBase guiBase, int i, int i1, IFluidTank iFluidTank)
    {
        super(guiBase, i, i1, iFluidTank);
    }

    @Override
    public void addTooltip(List<String> list)
    {
        super.addTooltip(list);
        FluidStack fluid = this.tank.getFluid();
        if (fluid != null)
        {
            int junk = 0;
            if (fluid.getFluid() == TinkerSmeltery.moltenGlassFluid)
            {
                int blocks = fluid.amount / 1000;
                if (blocks > 0)
                    list.add(StatCollector.translateToLocalFormatted("info.thermalsmeltery.block", blocks));
                int panels = (fluid.amount % 1000) / 250;
                if (panels > 0)
                    list.add(StatCollector.translateToLocalFormatted("info.thermalsmeltery.panel", panels));
                junk = (fluid.amount % 1000) % 250;
            } else if (fluid.getFluid() == TinkerSmeltery.moltenStoneFluid)
            {
                int blocks = fluid.amount / TConstruct.ingotLiquidValue;
                if (blocks > 0)
                    list.add(StatCollector.translateToLocalFormatted("info.thermalsmeltery.block", blocks));
                int bricks = (fluid.amount % (TConstruct.ingotLiquidValue / 4)) * 4 / TConstruct.ingotLiquidValue;
                if (bricks > 0)
                    list.add(StatCollector.translateToLocalFormatted("info.thermalsmeltery.brick", bricks));
                junk = fluid.amount % TConstruct.ingotLiquidValue;
            } else
            {
                int ingots = fluid.amount / TConstruct.ingotLiquidValue;
                if (ingots > 0)
                    list.add(StatCollector.translateToLocalFormatted("info.thermalsmeltery.ingot", ingots));
                int mB = fluid.amount % TConstruct.ingotLiquidValue;
                if (mB > 0)
                {
                    int nuggets = mB / TConstruct.nuggetLiquidValue;
                    junk = (mB % TConstruct.nuggetLiquidValue);
                    if (nuggets > 0)
                        list.add(StatCollector.translateToLocalFormatted("info.thermalsmeltery.nugget", nuggets));
                }
            }
            if (junk > 0)
                list.add("mB: " + junk);
        }
    }
}
