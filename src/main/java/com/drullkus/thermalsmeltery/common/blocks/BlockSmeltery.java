package com.drullkus.thermalsmeltery.common.blocks;

import cofh.api.tileentity.ISidedTexture;
import cofh.core.render.IconRegistry;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.FluidHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.StringHelper;
import com.drullkus.thermalsmeltery.ThermalSmeltery;
import com.drullkus.thermalsmeltery.common.items.ItemBlockSmeltery;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;
import thermalexpansion.block.BlockTEBase;
import thermalexpansion.item.TEAugments;
import thermalexpansion.util.ReconfigurableHelper;

import java.util.List;

public class BlockSmeltery extends BlockTEBase
{
    public static final String[] NAMES = new String[]{"extruder", "stamper"};
    public static boolean[] enable = new boolean[Types.values().length];
    public static boolean[] creativeTiers = new boolean[4];
    public static ItemStack[] defaultAugments = new ItemStack[3];
    public static boolean defaultAutoTransfer = true;
    public static boolean defaultRedstoneControl = true;
    public static boolean defaultReconfigSides = true;
    public static ItemStack extruder;
    public static ItemStack stamper;

    public BlockSmeltery() {
        super(Material.iron);
        this.setHardness(15.0F);
        this.setResistance(25.0F);
        this.setBlockName("thermalsmeltery.machine");
        this.setCreativeTab(ThermalSmeltery.itemTab);
    }

    public TileEntity createNewTileEntity(World var1, int var2) {
        if(var2 >= Types.values().length) {
            return null;
        } else {
            switch(Types.values()[var2]) {
                case EXTRUDER:
                    return new TileExtruder();
                case STAMPER:
                    return new TileStamper();
                default:
                    return null;
            }
        }
    }

    public void getSubBlocks(Item var1, CreativeTabs var2, List var3) {
        int var4;
        for(var4 = 0; var4 < Types.values().length; ++var4) {
            if(enable[var4]) {
                for(int var5 = 0; var5 < 4; ++var5) {
                    if(creativeTiers[var5]) {
                        var3.add(ItemBlockSmeltery.setDefaultTag(new ItemStack(var1, 1, var4), (byte)var5));
                    }
                }
            }
        }
    }

    public void onBlockPlacedBy(World var1, int var2, int var3, int var4, EntityLivingBase var5, ItemStack var6) {
        if(var6.stackTagCompound != null) {
            TileSmelteryBase var7 = (TileSmelteryBase)var1.getTileEntity(var2, var3, var4);
            var7.readAugmentsFromNBT(var6.stackTagCompound);
            var7.installAugments();
            var7.setEnergyStored(var6.stackTagCompound.getInteger("Energy"));
            int var8 = BlockHelper.determineXZPlaceFacing(var5);
            byte var9 = ReconfigurableHelper.getFacing(var6);
            byte[] var10 = ReconfigurableHelper.getSideCache(var6, var7.getDefaultSides());
            var7.sideCache[0] = var10[0];
            var7.sideCache[1] = var10[1];
            var7.sideCache[var8] = 0;
            var7.sideCache[BlockHelper.getLeftSide(var8)] = var10[BlockHelper.getLeftSide(var9)];
            var7.sideCache[BlockHelper.getRightSide(var8)] = var10[BlockHelper.getRightSide(var9)];
            var7.sideCache[BlockHelper.getOppositeSide(var8)] = var10[BlockHelper.getOppositeSide(var9)];
        }

        super.onBlockPlacedBy(var1, var2, var3, var4, var5, var6);
    }

    public boolean onBlockActivated(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
        TileEntity var10 = var1.getTileEntity(var2, var3, var4);
        return (var10 instanceof IFluidHandler) && FluidHelper.fillHandlerWithContainer(var1, (IFluidHandler)var10, var5) || super.onBlockActivated(var1, var2, var3, var4, var5, var6, var7, var8, var9);
    }

    public int getRenderBlockPass() {
        return 1;
    }

    public boolean canRenderInPass(int var1) {
        renderPass = var1;
        return var1 < 2;
    }

    public boolean isNormalCube(IBlockAccess var1, int var2, int var3, int var4) {
        return false;
    }

    public boolean isSideSolid(IBlockAccess var1, int var2, int var3, int var4, ForgeDirection var5) {
        return true;
    }

    public boolean renderAsNormalBlock() {
        return true;
    }

    public IIcon getIcon(IBlockAccess var1, int var2, int var3, int var4, int var5) {
        ISidedTexture var6 = (ISidedTexture)var1.getTileEntity(var2, var3, var4);
        return var6 == null?null:var6.getTexture(var5, renderPass);
    }

    public IIcon getIcon(int var1, int var2) {
        return var1 == 0?IconRegistry.getIcon("MachineBottom"):(var1 == 1?IconRegistry.getIcon("MachineTop"):(var1 != 3?IconRegistry.getIcon("MachineSide"):IconRegistry.getIcon("MachineFace" + var2)));
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister var1) {

        for(int var2 = 0; var2 < Types.values().length; ++var2) {
            IconRegistry.addIcon("MachineFace" + var2, "thermalsmeltery:machine/Machine_Face_" + StringHelper.titleCase(NAMES[var2]), var1);
            IconRegistry.addIcon("MachineActive" + var2, "thermalsmeltery:machine/Machine_Active_" + StringHelper.titleCase(NAMES[var2]), var1);
        }
    }

    public NBTTagCompound getItemStackTag(World var1, int var2, int var3, int var4) {
        NBTTagCompound var5 = super.getItemStackTag(var1, var2, var3, var4);
        TileSmelteryBase var6 = (TileSmelteryBase)var1.getTileEntity(var2, var3, var4);
        if(var6 != null) {
            if(var5 == null) {
                var5 = new NBTTagCompound();
            }

            ReconfigurableHelper.setItemStackTagReconfig(var5, var6);
            var5.setInteger("Energy", var6.getEnergyStored(ForgeDirection.UNKNOWN));
            var6.writeAugmentsToNBT(var5);
        }

        return var5;
    }

    public boolean initialize() {
        TileStamper.initialize();
        TileExtruder.initialize();
        if(defaultAutoTransfer) {
            defaultAugments[0] = ItemHelper.cloneStack(TEAugments.generalAutoTransfer);
        }

        if(defaultRedstoneControl) {
            defaultAugments[1] = ItemHelper.cloneStack(TEAugments.generalRedstoneControl);
        }

        if(defaultReconfigSides) {
            defaultAugments[2] = ItemHelper.cloneStack(TEAugments.generalReconfigSides);
        }

        extruder = ItemBlockSmeltery.setDefaultTag(new ItemStack(this, 1, Types.EXTRUDER.ordinal()));
        stamper = ItemBlockSmeltery.setDefaultTag(new ItemStack(this, 1, Types.STAMPER.ordinal()));
        GameRegistry.registerCustomItemStack("extruder", extruder);
        GameRegistry.registerCustomItemStack("stamper", stamper);

        return true;
    }

    public boolean postInit() {
//        TECraftingHandler.addMachineUpgradeRecipes(extruder);
//        TECraftingHandler.addMachineUpgradeRecipes(stamper);
//        TECraftingHandler.addSecureRecipe(assembler);
//        TECraftingHandler.addSecureRecipe(charger);
        return true;
    }

    public static void refreshItemStacks() {
        extruder = ItemBlockSmeltery.setDefaultTag(extruder);
        stamper = ItemBlockSmeltery.setDefaultTag(stamper);
    }

    static {
        String var0 = "block.feature";
        enable[Types.EXTRUDER.ordinal()] = true;//ThermalExpansion.config.get(var0, "Machine.Furnace", true);
        enable[Types.STAMPER.ordinal()] = true;//ThermalExpansion.config.get(var0, "Machine.Pulverizer", true);
    }

    public static enum Types
    {
        EXTRUDER,
        STAMPER;
    }
}
