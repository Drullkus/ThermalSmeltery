package us.drullk.thermalsmeltery.common.blocks;

import cofh.api.item.IToolHammer;
import cofh.api.transport.IItemDuct;
import cofh.lib.util.helpers.AugmentHelper;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.InventoryHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalexpansion.plugins.nei.handlers.NEIRecipeWrapper;
import cofh.thermalexpansion.util.crafting.RecipeMachine;
import cofh.thermalexpansion.util.crafting.TECraftingHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import us.drullk.thermalsmeltery.common.tile.TileMachineBase;

public class MachineHelper
{
    public static byte NUM_ENERGY_STORAGE = 3;
    public static byte NUM_MACHINE_SECONDARY = 3;
    public static byte NUM_MACHINE_SPEED = 3;
    public static final int[] ENERGY_STORAGE_MOD = new int[]{1, 2, 4, 8};
    public static final int[] MACHINE_SPEED_PROCESS_MOD = new int[]{1, 2, 4, 8};
    public static final int[] MACHINE_SPEED_ENERGY_MOD = new int[]{1, 3, 8, 20};
    public static final int[] MACHINE_SPEED_SECONDARY_MOD = new int[]{0, 5, 10, 15};
    public static final int[] MACHINE_SECONDARY_MOD = new int[]{0, 10, 15, 20};
    public static String ENERGY_STORAGE = "energyStorage";
    public static String GENERAL_AUTO_OUTPUT = "generalAutoOutput";
    public static String GENERAL_RECONFIG_SIDES = "generalReconfigSides";
    public static String GENERAL_REDSTONE_CONTROL = "generalRedstoneControl";
    public static String MACHINE_SECONDARY = "machineSecondary";
    public static String MACHINE_SPEED = "machineSpeed";
    public static String TOOL_MULTIMETER = "multimeter";
    public static String TOOL_DEBUGGER = "debugger";
    public static ItemStack coilGold;
    public static String GOLD_COIL = "powerCoilGold";
    public static String MACHINE_FRAME = "thermalexpansion:machineFrame";
    public static String COPPER_GEAR = "thermalexpansion:machineCopper";
    public static ItemStack generalAutoOutput;
    public static ItemStack generalReconfigSides;
    public static ItemStack generalRedstoneControl;
    public static ItemStack toolMultimeter;
    public static ItemStack toolDebugger;

    public static void initialize()
    {
        generalAutoOutput = getCustomStack(GENERAL_AUTO_OUTPUT);
        generalReconfigSides = getCustomStack(GENERAL_RECONFIG_SIDES);
        generalRedstoneControl = getCustomStack(GENERAL_REDSTONE_CONTROL);
        toolMultimeter = getCustomStack(TOOL_MULTIMETER);
        toolDebugger = getCustomStack(TOOL_DEBUGGER);
        coilGold = getCustomStack(GOLD_COIL);
    }

    private static ItemStack getCustomStack(String name)
    {
        return GameRegistry.findItemStack("ThermalExpansion", name, 1);
    }

    public static NBTTagCompound setItemStackTagReconfig(NBTTagCompound tag, TileMachineBase tile)
    {
        if (tile == null)
        {
            return null;
        } else
        {
            if (tag == null)
            {
                tag = new NBTTagCompound();
            }

            tag.setByte("Facing", (byte)tile.getFacing());
            tag.setByteArray("SideCache", tile.sideCache);
            return tag;
        }
    }

    public static byte getFacingFromNBT(NBTTagCompound tag)
    {
        return !tag.hasKey("Facing") ? 3 : tag.getByte("Facing");
    }

    public static byte[] getSideCacheFromNBT(NBTTagCompound tag, byte[] sideCache)
    {
        if (tag == null)
        {
            return sideCache.clone();
        } else
        {
            byte[] saved = tag.getByteArray("SideCache");
            return saved.length < 6 ? sideCache.clone() : saved;
        }
    }

    public static boolean setFacing(ItemStack stack, int facing)
    {
        if (facing >= 0 && facing <= 5)
        {
            if (stack.stackTagCompound == null)
            {
                stack.setTagCompound(new NBTTagCompound());
            }

            stack.stackTagCompound.setByte("Facing", (byte)facing);
            return true;
        } else
        {
            return false;
        }
    }

    public static boolean setSideCache(ItemStack stack, byte[] sideCache)
    {
        if (sideCache.length < 6)
        {
            return false;
        } else
        {
            if (stack.stackTagCompound == null)
            {
                stack.setTagCompound(new NBTTagCompound());
            }

            stack.stackTagCompound.setByteArray("SideCache", sideCache);
            return true;
        }
    }

    public static byte getFacing(ItemStack stack)
    {
        return stack.stackTagCompound != null && stack.stackTagCompound.hasKey("Facing") ? stack.stackTagCompound.getByte("Facing") : 3;
    }

    public static byte[] getSideCache(ItemStack stack, byte[] sideCache)
    {
        if (stack.stackTagCompound == null)
        {
            return sideCache.clone();
        } else
        {
            byte[] var2 = stack.stackTagCompound.getByteArray("SideCache");
            return var2.length < 6 ? sideCache.clone() : var2;
        }
    }

    public static int addToInventory(TileEntity tile, int side, ItemStack stack)
    {
        if (!InventoryHelper.isInsertion(tile))
        {
            return stack.stackSize;
        } else
        {
            stack = InventoryHelper.addToInsertion(tile, side, stack);
            return stack == null ? 0 : stack.stackSize;
        }
    }

    public static boolean isInventory(TileEntity tile, int side)
    {
        return !(tile instanceof ISidedInventory && ((ISidedInventory)tile).getAccessibleSlotsFromSide(BlockHelper.SIDE_OPPOSITE[side]).length <= 0) && (tile instanceof IInventory && ((IInventory)tile).getSizeInventory() > 0 || tile instanceof IItemDuct);
    }

    public static boolean isAugmentItem(ItemStack stack)
    {
        return AugmentHelper.isAugmentItem(stack);
    }

    public static boolean isHoldingMultimeter(EntityPlayer player)
    {
        return ItemHelper.isPlayerHoldingItemStack(toolMultimeter, player);
    }

    public static boolean isHoldingDebugger(EntityPlayer player)
    {
        return ItemHelper.isPlayerHoldingItemStack(toolDebugger, player);
    }

    public static boolean isHoldingUsableWrench(EntityPlayer player, int x, int y, int z)
    {
        Item item = player.getCurrentEquippedItem() != null ? player.getCurrentEquippedItem().getItem() : null;
        return item instanceof IToolHammer && ((IToolHammer)item).isUsable(player.getCurrentEquippedItem(), player, x, y, z);
    }

    public static void usedWrench(EntityPlayer player, int x, int y, int z)
    {
        Item item = player.getCurrentEquippedItem() != null ? player.getCurrentEquippedItem().getItem() : null;
        if (item instanceof IToolHammer)
        {
            ((IToolHammer)item).toolUsed(player.getCurrentEquippedItem(), player, x, y, z);
        }
    }

    /* This is the only method with direct references to ThermalExpansion classes which might require modification with
    TE updates, however I think it's probably worth it rather than reproducing the code ala TileMachineBase. */
    public static void registerRecipes(ItemStack machine, Object[] args)
    {
        NEIRecipeWrapper.addMachineRecipe(new RecipeMachine(machine, BlockMachine.defaultAugments, args));
        TECraftingHandler.addMachineUpgradeRecipes(machine);
        TECraftingHandler.addSecureRecipe(machine);
    }
}
