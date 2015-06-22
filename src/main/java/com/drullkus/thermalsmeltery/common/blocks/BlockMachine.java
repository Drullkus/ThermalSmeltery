package com.drullkus.thermalsmeltery.common.blocks;

import cofh.api.tileentity.ISidedTexture;
import cofh.core.block.BlockCoFHBase;
import cofh.core.block.TileCoFHBase;
import cofh.core.render.IconRegistry;
import cofh.core.util.CoreUtils;
import cofh.lib.util.helpers.*;
import com.drullkus.thermalsmeltery.ThermalSmeltery;
import com.drullkus.thermalsmeltery.common.items.ItemBlockSmeltery;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.IFluidHandler;
import thermalexpansion.block.TileTEBase;

import java.util.ArrayList;
import java.util.List;

public class BlockMachine extends BlockCoFHBase
{
    public static final String[] NAMES = new String[]{"extruder", "stamper"};
    public static final String[] COLOURS = new String[]{"Blank", "Blue", "Red", "Yellow", "Orange", "Green", "Purple"};
    public static boolean[] enable = new boolean[]{true, true};
    public static boolean[] creativeTiers = new boolean[]{true, false, false, true};
    public static ItemStack[] defaultAugments = new ItemStack[3];
    public static boolean defaultAutoTransfer = true;
    public static boolean defaultRedstoneControl = true;
    public static boolean defaultReconfigSides = true;
    public static ItemStack extruder;
    public static ItemStack stamper;

    public BlockMachine()
    {
        super(Material.iron);
        this.setHardness(15.0F);
        this.setResistance(25.0F);
        this.setBlockName("thermalsmeltery.machine");
        this.setCreativeTab(ThermalSmeltery.itemTab);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        switch (meta)
        {
            case 0:
                return new TileExtruder();
            case 1:
                return new TileStamper();
            default:
                return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void getSubBlocks(Item item, CreativeTabs tab, List list)
    {
        for (int i = 0; i < NAMES.length; ++i)
        {
            if (enable[i])
            {
                for (int tier = 0; tier < 4; ++tier)
                {
                    if (creativeTiers[tier])
                    {
                        list.add(ItemBlockSmeltery.setDefaultTag(new ItemStack(item, 1, i), (byte)tier));
                    }
                }
            }
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack)
    {
        if (stack.stackTagCompound != null)
        {
            TileMachineBase machine = (TileMachineBase)world.getTileEntity(x, y, z);
            machine.setInvName(ItemHelper.getNameFromItemStack(stack));
            machine.readAugmentsFromNBT(stack.stackTagCompound);
            machine.installAugments();
            machine.setEnergyStored(stack.stackTagCompound.getInteger("Energy"));
            int placing = BlockHelper.determineXZPlaceFacing(player);
            byte facing = MachineHelper.getFacing(stack);
            byte[] sideCache = MachineHelper.getSideCache(stack, machine.getDefaultSides());
            machine.sideCache[0] = sideCache[0];
            machine.sideCache[1] = sideCache[1];
            machine.sideCache[placing] = 0;
            machine.sideCache[BlockHelper.getLeftSide(placing)] = sideCache[BlockHelper.getLeftSide(facing)];
            machine.sideCache[BlockHelper.getRightSide(placing)] = sideCache[BlockHelper.getRightSide(facing)];
            machine.sideCache[BlockHelper.getOppositeSide(placing)] = sideCache[BlockHelper.getOppositeSide(facing)];
        }

        super.onBlockPlacedBy(world, x, y, z, player, stack);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        boolean result = (tile instanceof IFluidHandler) && FluidHelper.fillHandlerWithContainer(world, (IFluidHandler)tile, player);
        if (!result)
        {
            PlayerInteractEvent event = new PlayerInteractEvent(player, PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK, x, y, z, side, world);
            if (!MinecraftForge.EVENT_BUS.post(event) && event.getResult() != Event.Result.DENY && event.useBlock != Event.Result.DENY)
            {
                if (MachineHelper.isHoldingDebugger(player) || MachineHelper.isHoldingMultimeter(player))
                {
                    result = true;
                } else if (player.isSneaking())
                {
                    if (MachineHelper.isHoldingUsableWrench(player, x, y, z))
                    {
                        if (ServerHelper.isServerWorld(world) && this.canDismantle(player, world, x, y, z))
                        {
                            this.dismantleBlock(player, world, x, y, z, false);
                        }
                        MachineHelper.usedWrench(player, x, y, z);
                        result = true;
                    } else
                    {
                        result = false;
                    }
                } else
                {
                    TileMachineBase machine = (TileMachineBase)tile;
                    if (MachineHelper.isHoldingUsableWrench(player, x, y, z))
                    {
                        if (ServerHelper.isServerWorld(world))
                        {
                            machine.onWrench(player, side);
                        }
                        MachineHelper.usedWrench(player, x, y, z);
                        result = true;
                    } else
                    {
                        result = !ServerHelper.isServerWorld(world) || machine.openGui(player);
                    }
                }
            } else
            {
                result = false;
            }
        }
        return result;
    }

    @Override
    public int getRenderBlockPass()
    {
        return 1;
    }

    @Override
    public boolean canRenderInPass(int pass)
    {
        renderPass = pass;
        return pass < 2;
    }

    @Override
    public boolean isNormalCube(IBlockAccess world, int x, int y, int z)
    {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection dir)
    {
        return true;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return true;
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
    {
        ISidedTexture te = (ISidedTexture)world.getTileEntity(x, y, z);
        return te == null ? null : te.getTexture(side, renderPass);
    }

    @Override
    public IIcon getIcon(int side, int meta)
    {
        return side == 0 ? IconRegistry.getIcon("SmelteryBottom") : (side == 1 ? IconRegistry.getIcon("SmelteryTop") : (side != 3 ? IconRegistry.getIcon("SmelterySide") : IconRegistry.getIcon("SmelteryFace" + meta)));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register)
    {
        IconRegistry.addIcon("SmelteryBottom", "thermalsmeltery:machine/machineBottom", register);
        IconRegistry.addIcon("SmelteryTop", "thermalsmeltery:machine/machineTop", register);
        IconRegistry.addIcon("SmelterySide", "thermalsmeltery:machine/machineSide", register);

        for (int i = 0; i < NAMES.length; ++i)
        {
            IconRegistry.addIcon("SmelteryFace" + i, "thermalsmeltery:machine/machineFace" + StringHelper.titleCase(NAMES[i]), register);
            IconRegistry.addIcon("SmelteryActive" + i, "thermalsmeltery:machine/machineActive" + StringHelper.titleCase(NAMES[i]), register);
        }

        for (int i = 0; i < COLOURS.length; i++)
        {
            IconRegistry.addIcon("sideConfig_" + i, "thermalsmeltery:machine/side" + COLOURS[i], register);
            IconRegistry.addIcon("topConfig_" + i, "thermalsmeltery:machine/top" + COLOURS[i], register);
            IconRegistry.addIcon("bottomConfig_" + i, "thermalsmeltery:machine/bottom" + COLOURS[i], register);
        }
    }

    @Override
    public NBTTagCompound getItemStackTag(World world, int x, int y, int z)
    {
        NBTTagCompound tag = super.getItemStackTag(world, x, y, z);
        TileMachineBase machine = (TileMachineBase)world.getTileEntity(x, y, z);
        if (machine != null)
        {
            if (tag == null)
            {
                tag = new NBTTagCompound();
            }
            if (!machine.tileName.isEmpty())
            {
                tag = ItemHelper.setItemStackTagName(tag, machine.tileName);
            }

            if (machine.isSecured())
            {
                tag = SecurityHelper.setItemStackTagSecure(tag, machine);
            }
            tag = RedstoneControlHelper.setItemStackTagRS(tag, machine);

            MachineHelper.setItemStackTagReconfig(tag, machine);
            tag.setInteger("Energy", machine.getEnergyStored(ForgeDirection.UNKNOWN));
            machine.writeAugmentsToNBT(tag);
        }

        return tag;
    }

    @Override
    public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, NBTTagCompound tagCompound, World world, int x, int y, int z, boolean returnDrops, boolean simulate)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        ItemStack stack = new ItemStack(this, 1, meta);
        if (tagCompound != null)
        {
            stack.setTagCompound(tagCompound);
        }

        if (!simulate)
        {
            if (te instanceof TileCoFHBase)
            {
                ((TileCoFHBase)te).blockDismantled();
            }

            world.setBlockToAir(x, y, z);
            if (!returnDrops)
            {
                float mult = 0.3F;
                double vX = (double)(world.rand.nextFloat() * mult) + (double)(1.0F - mult) * 0.5D;
                double vY = (double)(world.rand.nextFloat() * mult) + (double)(1.0F - mult) * 0.5D;
                double vZ = (double)(world.rand.nextFloat() * mult) + (double)(1.0F - mult) * 0.5D;
                EntityItem drop = new EntityItem(world, (double)x + vX, (double)y + vY, (double)z + vZ, stack);
                drop.delayBeforeCanPickup = 10;
                world.spawnEntityInWorld(drop);
                if (player != null)
                {
                    CoreUtils.dismantleLog(player.getCommandSenderName(), this, meta, (double)x, (double)y, (double)z);
                }
            }
        }

        ArrayList<ItemStack> result = new ArrayList<ItemStack>();
        result.add(stack);
        return result;
    }

    @Override
    public boolean initialize()
    {
        MachineHelper.initialize();
        TileStamper.initialize();
        TileExtruder.initialize();

        if (defaultAutoTransfer)
        {
            defaultAugments[0] = ItemHelper.cloneStack(MachineHelper.generalAutoTransfer);
        }

        if (defaultRedstoneControl)
        {
            defaultAugments[1] = ItemHelper.cloneStack(MachineHelper.generalRedstoneControl);
        }

        if (defaultReconfigSides)
        {
            defaultAugments[2] = ItemHelper.cloneStack(MachineHelper.generalReconfigSides);
        }

        extruder = ItemBlockSmeltery.setDefaultTag(new ItemStack(this, 1, 0));
        stamper = ItemBlockSmeltery.setDefaultTag(new ItemStack(this, 1, 1));
        GameRegistry.registerCustomItemStack("extruder", extruder);
        GameRegistry.registerCustomItemStack("stamper", stamper);

        return true;
    }

    @Override
    public boolean postInit()
    {
        return true;
    }

    static
    {
        enable[0] = true;
        enable[1] = true;
    }
}
