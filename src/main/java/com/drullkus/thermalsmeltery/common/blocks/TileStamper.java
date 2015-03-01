package com.drullkus.thermalsmeltery.common.blocks;

import com.drullkus.thermalsmeltery.common.gui.client.GuiStamper;
import com.drullkus.thermalsmeltery.common.gui.container.ContainerStamper;
import com.drullkus.thermalsmeltery.common.plugins.tcon.smeltery.StampingRecipe;
import com.drullkus.thermalsmeltery.common.plugins.tcon.smeltery.StampingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import thermalexpansion.block.machine.MachineHelper;

public class TileStamper extends TileSmelteryBase
{
    static final int TYPE = BlockSmeltery.Types.STAMPER.ordinal();

    @Override
    public int getType()
    {
        return 1;
    }

    public static void initialize()
    {
        defaultSideConfigSmeltery[TYPE] = new SideConfig();
        defaultSideConfigSmeltery[TYPE].numGroup = 7;
        defaultSideConfigSmeltery[TYPE].slotGroups = new int[][]{new int[0], {0, 1}, {2, 3}, {0}, {1}, {2}, {3}};
        defaultSideConfigSmeltery[TYPE].allowInsertion = new boolean[]{false, true, false, false, false, true, true};
        defaultSideConfigSmeltery[TYPE].allowExtraction = new boolean[]{false, true, true, true, true, true, true};
        defaultSideConfigSmeltery[TYPE].sideTex = new int[]{0, 1, 2, 3, 4, 5, 6};
        defaultSideConfigSmeltery[TYPE].defaultSides = new byte[]{(byte)3, (byte)1, (byte)2, (byte)2, (byte)2, (byte)2};
//        int basePower = MathHelper.clampI(ThermalExpansion.config.get("block.tweak", "Machine.Crucible.BasePower", 400), 100, 500);
//        ThermalExpansion.config.set("block.tweak", "Machine.Crucible.BasePower", var0);
        int basePower = 400;
        defaultEnergyConfigSmeltery[TYPE] = new EnergyConfig();
        defaultEnergyConfigSmeltery[TYPE].setParams(basePower / 10, basePower, Math.max(480000, basePower * 1200));
//        sounds[TYPE] = CoreUtils.getSoundName("ThermalExpansion", "blockMachineCrucible");
//        enableSound[TYPE] = CoFHCore.configClient.get("sound", "Machine.Crucible", true);
        GameRegistry.registerTileEntity(TileStamper.class, "thermalsmeltery.Stamper");
    }

    public TileStamper()
    {
        this.inventory = new ItemStack[5];
    }

    @Override
    protected int getMaxInputSlot()
    {
        return 1;
    }

    @Override
    protected boolean canStart()
    {
        return hasValidInput();
    }

    @Override
    protected boolean hasValidInput()
    {
        return getRecipe() != null;
    }

    @Override
    protected void processStart()
    {
        MachineHelper.setProcessMax(this, getRecipeTime(getRecipe()));
    }

    @Override
    public boolean isOutput(int side)
    {
        return super.isOutput(side) || this.sideCache[side] > 4;
    }

    @Override
    protected void processFinish()
    {
        StampingRecipe recipe = getRecipe();
        ItemStack main = recipe.getMainResult();
        ItemStack secondary = recipe.getSecondaryResult();
        int var4;
        if (this.inventory[2] == null)
        {
            this.inventory[2] = main;
        } else if (this.inventory[2].isItemEqual(main))
        {
            if (this.inventory[2].stackSize + main.stackSize <= main.getMaxStackSize())
            {
                this.inventory[2].stackSize += main.stackSize;
            } else
            {
                var4 = main.getMaxStackSize() - this.inventory[2].stackSize;
                this.inventory[2].stackSize += var4;
                if (this.inventory[3] == null)
                {
                    this.inventory[3] = main;
                    this.inventory[3].stackSize = main.stackSize - var4;
                } else
                {
                    this.inventory[3].stackSize += main.stackSize - var4;
                }
            }
        } else if (this.inventory[3] == null)
        {
            this.inventory[3] = main;
        } else
        {
            this.inventory[3].stackSize += main.stackSize;
        }

        if (secondary != null)
        {
            if (this.inventory[4] == null)
            {
                this.inventory[4] = secondary;
            } else
            {
                this.inventory[4].stackSize += secondary.stackSize;
            }
        }

        this.inventory[0].stackSize -= recipe.metal.stackSize;

        if (this.inventory[0].stackSize <= 0)
        {
            this.inventory[0] = null;
        }
    }

    @Override
    public boolean isItemValid(ItemStack itemStack, int slot, int i1)
    {
        return slot == 0 ? StampingRegistry.isValidMetal(itemStack) : slot == 1 && StampingRegistry.isValidCast(itemStack);
    }

    private StampingRecipe getRecipe()
    {
        return StampingRegistry.getRecipe(inventory[0], inventory[1]);
    }

    private int getRecipeTime(StampingRecipe recipe)
    {
        return recipe.coolTime * 500; //TODO: something sensible here;
    }

    @Override
    public Object getGuiClient(InventoryPlayer inventoryPlayer)
    {
        return new GuiStamper(inventoryPlayer, this);
    }

    @Override
    public Object getGuiServer(InventoryPlayer inventoryPlayer)
    {
        return new ContainerStamper(inventoryPlayer, this);
    }
}
