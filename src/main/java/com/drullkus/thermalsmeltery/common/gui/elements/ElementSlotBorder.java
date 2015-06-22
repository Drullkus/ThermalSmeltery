package com.drullkus.thermalsmeltery.common.gui.elements;

import cofh.lib.gui.GuiBase;
import cofh.lib.gui.element.ElementBase;
import cofh.lib.render.RenderHelper;
import net.minecraft.util.ResourceLocation;

public class ElementSlotBorder extends ElementBase
{
    public int slotColour;
    public int slotType;
    public int slotRender;

    public ElementSlotBorder(GuiBase var1, int var2, int var3)
    {
        super(var1, var2, var3);
        this.texture = new ResourceLocation("thermalexpansion:textures/gui/elements/Slots.png");
    }

    public ElementSlotBorder setSlotInfo(int colour, int type, int render)
    {
        this.slotColour = colour;
        this.slotType = type;
        this.slotRender = render;
        return this;
    }

    public ElementSlotBorder setSlotColour(int colour)
    {
        this.slotColour = colour;
        return this;
    }

    public ElementSlotBorder setSlotRender(int render)
    {
        this.slotRender = render;
        return this;
    }

    public void drawBackground(int var1, int var2, float var3)
    {
        if (this.isVisible())
        {
            RenderHelper.bindTexture(this.texture);
            this.drawSlotWithBorder(this.posX, this.posY);
        }
    }

    public void drawForeground(int var1, int var2)
    {
    }

    public boolean intersectsWith(int var1, int var2)
    {
        return false;
    }

    protected void drawSlotWithBorder(int var1, int var2)
    {
        byte var3 = 32;
        int var4 = 32;
        int var5 = this.slotColour / 3 * 128;
        int var6 = this.slotColour % 3 * 32;
        var5 += this.slotType * 32;
        switch (this.slotType)
        {
            case 0:
                var1 -= 8;
                var2 -= 8;
                break;
            case 1:
                var1 -= 4;
                var2 -= 4;
                break;
            case 2:
                var3 = 64;
                var1 -= 11;
                var2 -= 4;
                break;
            case 3:
                var3 = 32;
                var4 = 64;
                var5 = this.slotColour * 32;
                var6 = 96;
                var1 -= 8;
                var2 -= 2;
        }

        switch (this.slotRender)
        {
            case 0:
                var4 /= 2;
                break;
            case 1:
                var4 /= 2;
                var2 += var4;
                var6 += var4;
        }

        this.gui.drawTexturedModalRect(var1, var2, var5, var6, var3, var4);
    }
}
