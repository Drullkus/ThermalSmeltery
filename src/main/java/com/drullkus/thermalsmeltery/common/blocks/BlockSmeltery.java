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

        //TODO: Remove this dependency. Needs immediate fixing before release.
public class BlockSmeltery extends BlockTEBase
{
    public static final String[] NAMES = new String[]{"extruder", "stamper"};
    public static boolean[] enable = new boolean[]{true,true};
    public static boolean[] creativeTiers = new boolean[]{true,false,false,true};
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

    @Override
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

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for(int i = 0; i < NAMES.length; ++i) {
            if(enable[i]) {
                for(int tier = 0; tier < 4; ++tier) {
                    if(creativeTiers[tier]) {
                        list.add(ItemBlockSmeltery.setDefaultTag(new ItemStack(item, 1, i), (byte)tier));
                    }
                }
            }
        }
    }

    @Override
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

    @Override
    public boolean onBlockActivated(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
        TileEntity var10 = var1.getTileEntity(var2, var3, var4);
        return (var10 instanceof IFluidHandler) && FluidHelper.fillHandlerWithContainer(var1, (IFluidHandler)var10, var5) || super.onBlockActivated(var1, var2, var3, var4, var5, var6, var7, var8, var9);
    }

    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    public boolean canRenderInPass(int var1) {
        renderPass = var1;
        return var1 < 2;
    }

    @Override
    public boolean isNormalCube(IBlockAccess var1, int var2, int var3, int var4) {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockAccess var1, int var2, int var3, int var4, ForgeDirection var5) {
        return true;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return true;
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        ISidedTexture te = (ISidedTexture)world.getTileEntity(x, y, z);
        return te == null?null:te.getTexture(side, renderPass);
    }

    @Override
    public IIcon getIcon(int var1, int var2) {
        return var1 == 0?IconRegistry.getIcon("SmelteryBottom"):(var1 == 1?IconRegistry.getIcon("SmelteryTop"):(var1 != 3?IconRegistry.getIcon("SmelterySide"):IconRegistry.getIcon("SmelteryFace" + var2)));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister var1)
    {
        IconRegistry.addIcon("SmelteryBottom", "thermalsmeltery:machine/machineBottom", var1);
        IconRegistry.addIcon("SmelteryTop", "thermalsmeltery:machine/machineTop", var1);
        IconRegistry.addIcon("SmelterySide", "thermalsmeltery:machine/machineSide", var1);

        for(int var2 = 0; var2 < Types.values().length; ++var2) {
            IconRegistry.addIcon("SmelteryFace" + var2, "thermalsmeltery:machine/machineFace" + StringHelper.titleCase(NAMES[var2]), var1);
            IconRegistry.addIcon("SmelteryActive" + var2, "thermalsmeltery:machine/machineActive" + StringHelper.titleCase(NAMES[var2]), var1);
        }

        IconRegistry.addIcon("sideConfig_0", "thermalsmeltery:machine/sideBlank", var1);
        IconRegistry.addIcon("sideConfig_1", "thermalsmeltery:machine/sideBlue", var1);
        IconRegistry.addIcon("sideConfig_2", "thermalsmeltery:machine/sideRed", var1);
        IconRegistry.addIcon("sideConfig_3", "thermalsmeltery:machine/sideYellow", var1);
        IconRegistry.addIcon("sideConfig_4", "thermalsmeltery:machine/sideOrange", var1);
        IconRegistry.addIcon("sideConfig_5", "thermalsmeltery:machine/sideGreen", var1);
        IconRegistry.addIcon("sideConfig_6", "thermalsmeltery:machine/sidePurple", var1);
        IconRegistry.addIcon("topConfig_0", "thermalsmeltery:machine/topBlank", var1);
        IconRegistry.addIcon("topConfig_1", "thermalsmeltery:machine/topBlue", var1);
        IconRegistry.addIcon("topConfig_2", "thermalsmeltery:machine/topRed", var1);
        IconRegistry.addIcon("topConfig_3", "thermalsmeltery:machine/topYellow", var1);
        IconRegistry.addIcon("topConfig_4", "thermalsmeltery:machine/topOrange", var1);
        IconRegistry.addIcon("topConfig_5", "thermalsmeltery:machine/topGreen", var1);
        IconRegistry.addIcon("topConfig_6", "thermalsmeltery:machine/topPurple", var1);
        IconRegistry.addIcon("bottomConfig_0", "thermalsmeltery:machine/bottomBlank", var1);
        IconRegistry.addIcon("bottomConfig_1", "thermalsmeltery:machine/bottomBlue", var1);
        IconRegistry.addIcon("bottomConfig_2", "thermalsmeltery:machine/bottomRed", var1);
        IconRegistry.addIcon("bottomConfig_3", "thermalsmeltery:machine/bottomYellow", var1);
        IconRegistry.addIcon("bottomConfig_4", "thermalsmeltery:machine/bottomOrange", var1);
        IconRegistry.addIcon("bottomConfig_5", "thermalsmeltery:machine/bottomGreen", var1);
        IconRegistry.addIcon("bottomConfig_6", "thermalsmeltery:machine/bottomPurple", var1);
    }

    @Override
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

    @Override
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

    @Override
    public boolean postInit() {
//        TECraftingHandler.addMachineUpgradeRecipes(extruder);
//        TECraftingHandler.addMachineUpgradeRecipes(stamper);
//        TECraftingHandler.addSecureRecipe(assembler);
//        TECraftingHandler.addSecureRecipe(charger);
        return true;
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
