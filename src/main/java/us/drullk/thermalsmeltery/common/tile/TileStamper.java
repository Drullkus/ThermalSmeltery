package us.drullk.thermalsmeltery.common.tile;

import us.drullk.thermalsmeltery.common.core.handler.TSmeltConfig;
import us.drullk.thermalsmeltery.common.gui.client.GuiStamper;
import us.drullk.thermalsmeltery.common.gui.container.ContainerStamper;
import us.drullk.thermalsmeltery.common.plugins.tcon.smeltery.MachineRecipeRegistry;
import us.drullk.thermalsmeltery.common.plugins.tcon.smeltery.StampingRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class TileStamper extends TileMachineBase
{
    static final int TYPE = 1;

    @Override
    public int getType()
    {
        return TYPE;
    }

    public static void initialize()
    {
        defaultSideConfigSmeltery[TYPE] = new SideConfig();
        defaultSideConfigSmeltery[TYPE].numGroup = 7;
        defaultSideConfigSmeltery[TYPE].slotGroups = new int[][]{new int[0], {0, 1}, {2}, {3}, {2, 3}, {0}, {1}};
        defaultSideConfigSmeltery[TYPE].allowInsertion = new boolean[]{false, true, false, false, false, true, true};
        defaultSideConfigSmeltery[TYPE].allowExtraction = new boolean[]{false, false, true, true, true, false, false};
        defaultSideConfigSmeltery[TYPE].sideTex = new int[]{0, 1, 2, 3, 4, 5, 6};
        defaultSideConfigSmeltery[TYPE].defaultSides = new byte[]{(byte)3, (byte)1, (byte)2, (byte)2, (byte)2, (byte)2};
        int basePower = 400;
        defaultEnergyConfigSmeltery[TYPE] = new EnergyConfig();
        defaultEnergyConfigSmeltery[TYPE].setParams(basePower / 10, basePower, Math.max(480000, basePower * 1200));
//        sounds[TYPE] = CoreUtils.getSoundName("ThermalExpansion", "blockMachineCrucible");
//        enableSound[TYPE] = CoFHCore.configClient.get("sound", "Machine.Crucible", true);
        GameRegistry.registerTileEntity(TileStamper.class, "thermalsmeltery.Stamper");
    }

    public TileStamper()
    {
    }

    @Override
    protected int getInventorySize()
    {
        return 5;
    }

    @Override
    protected int getMaxInputSlot()
    {
        return 1;
    }

    @Override
    protected boolean hasRoomForOutput()
    {
        StampingRecipe recipe = getRecipe();
        ItemStack main = recipe.getMainResult();
        ItemStack secondary = recipe.getSecondaryResult();
        return canFit(main, 2) && canFit(secondary, 3);
    }

    @Override
    protected boolean hasValidInput()
    {
        return getRecipe() != null;
    }

    @Override
    protected void processStart()
    {
        processMax = getRecipeTime(getRecipe());
        processRem = processMax;
    }

    @Override
    protected void processFinish()
    {
        StampingRecipe recipe = getRecipe();
        ItemStack main = recipe.getMainResult();
        ItemStack secondary = recipe.getSecondaryResult();
        if (this.inventory[2] == null)
        {
            this.inventory[2] = main;
        } else
        {
            this.inventory[2].stackSize += main.stackSize;
        }

        if (secondary != null)
        {
            if (this.inventory[3] == null)
            {
                this.inventory[3] = secondary;
            } else
            {
                this.inventory[3].stackSize += secondary.stackSize;
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
        return slot == 0 ? MachineRecipeRegistry.isValidMetal(itemStack) : slot == 1 && MachineRecipeRegistry.isValidCast(itemStack);
    }

    private StampingRecipe getRecipe()
    {
        return MachineRecipeRegistry.getStampingRecipe(inventory[0], inventory[1]);
    }

    private int getRecipeTime(StampingRecipe recipe)
    {
        if (recipe == null) return 0;
        return recipe.coolTime * 1000 * TSmeltConfig.stamperMultiplier;

        /**
         Controls the speed of the machine
         */
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
