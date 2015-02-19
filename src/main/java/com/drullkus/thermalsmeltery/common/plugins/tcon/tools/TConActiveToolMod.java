package com.drullkus.thermalsmeltery.common.plugins.tcon.tools;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import tconstruct.library.ActiveToolMod;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.library.tools.ToolCore;
import tconstruct.tools.TActiveOmniMod;
import tconstruct.tools.TinkerTools;
import tconstruct.util.config.PHConstruct;
import tconstruct.world.TinkerWorld;

import java.util.Random;

public class TConActiveToolMod extends TActiveOmniMod
{
    Random random = new Random();

    @Override
    public boolean beforeBlockBreak (ToolCore tool, ItemStack stack, int x, int y, int z, EntityLivingBase entity)
    {
        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        baconator(tool, stack, entity, tags);

        if (entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isCreativeMode)
            return false;
        TinkerTools.modLapis.midStreamModify(stack, tool);
        if (autoSmelt(tool, tags, stack, x, y, z, entity))
            return true;

        return false;
    }

    private boolean autoSmelt (ToolCore tool, NBTTagCompound tags, ItemStack stack, int x, int y, int z, EntityLivingBase entity)
    {
        World world = entity.worldObj;
        int meta = world.getBlockMetadata(x, y, z);
        Block block = world.getBlock(x, y, z);
        if (block == null)
            return false;

        if (tags.hasKey("Voiding") && block.quantityDropped(meta, 0, random) != 0)
        {
            world.setBlockToAir(x, y, z);
            if (entity instanceof EntityPlayer && !((EntityPlayer) entity).capabilities.isCreativeMode)
                tool.onBlockDestroyed(stack, world, block, x, y, z, entity);
            if (!world.isRemote)
            {
                world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (meta << 12));
            }
        }
        else
        {
            autoLavaSmelt(tool, tags, stack, x, y, z, entity);
        }

        return false;
    }

    private boolean autoLavaSmelt (ToolCore tool, NBTTagCompound tags, ItemStack stack, int x, int y, int z, EntityLivingBase entity)
    {
        World world = entity.worldObj;
        Block block = world.getBlock(x, y, z);
        if (block == null)
            return false;

        int blockMeta = world.getBlockMetadata(x, y, z);

        if (!block.getMaterial().isToolNotRequired() && !ForgeHooks.canToolHarvestBlock(block, blockMeta, stack))
            return false;

        if (tags.getBoolean("Lava") && block.quantityDropped(blockMeta, 0, random) > 0)
        {
            int itemMeta = block.damageDropped(blockMeta);
            int amount = block.quantityDropped(random);
            Item item = block.getItemDropped(blockMeta, random, EnchantmentHelper.getFortuneModifier(entity));

            // apparently some things that don't drop blocks (like glass panes without silktouch) return null.
            if (item == null)
                return false;

            ItemStack result = FurnaceRecipes.smelting().getSmeltingResult(new ItemStack(item, amount, itemMeta));
            if (result != null)
            {
                world.setBlockToAir(x, y, z);
                if (entity instanceof EntityPlayer && !((EntityPlayer) entity).capabilities.isCreativeMode)
                    tool.onBlockDestroyed(stack, world, block, x, y, z, entity);
                if (!world.isRemote)
                {
                    ItemStack spawnme = new ItemStack(result.getItem(), amount * result.stackSize, result.getItemDamage());
                    if (result.hasTagCompound())
                        spawnme.setTagCompound(result.getTagCompound());
                    if (!(result.getItem() instanceof ItemBlock) && PHConstruct.lavaFortuneInteraction)
                    {
                        int loot = EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, stack);
                        if (loot > 0)
                        {
                            spawnme.stackSize *= (random.nextInt(loot + 1) + 1);
                        }
                    }
                    EntityItem entityitem = new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, spawnme);

                    entityitem.delayBeforeCanPickup = 10;
                    world.spawnEntityInWorld(entityitem);
                    world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (blockMeta << 12));

                    int i = spawnme.stackSize;
                    float f = FurnaceRecipes.smelting().func_151398_b(spawnme);
                    int j;

                    if (f == 0.0F)
                    {
                        i = 0;
                    }
                    else if (f < 1.0F)
                    {
                        j = MathHelper.floor_float((float) i * f);

                        if (j < MathHelper.ceiling_float_int((float) i * f) && (float) Math.random() < (float) i * f - (float) j)
                        {
                            ++j;
                        }

                        i = j;
                    }

                    while (i > 0)
                    {
                        j = EntityXPOrb.getXPSplit(i);
                        i -= j;
                        entity.worldObj.spawnEntityInWorld(new EntityXPOrb(world, x, y + 0.5, z, j));
                    }
                }
                for (int i = 0; i < 5; i++)
                {
                    float f = (float) x + random.nextFloat();
                    float f1 = (float) y + random.nextFloat();
                    float f2 = (float) z + random.nextFloat();
                    float f3 = 0.52F;
                    float f4 = random.nextFloat() * 0.6F - 0.3F;
                    world.spawnParticle("smoke", f - f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle("flame", f - f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);

                    world.spawnParticle("smoke", f + f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle("flame", f + f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);

                    world.spawnParticle("smoke", f + f4, f1, f2 - f3, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle("flame", f + f4, f1, f2 - f3, 0.0D, 0.0D, 0.0D);

                    world.spawnParticle("smoke", f + f4, f1, f2 + f3, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle("flame", f + f4, f1, f2 + f3, 0.0D, 0.0D, 0.0D);
                }
                return true;
            }
        }
        return false;
    }

    private void baconator (ToolCore tool, ItemStack stack, EntityLivingBase entity, NBTTagCompound tags)
    {
        final int pigiron = TinkerTools.MaterialID.PigIron;
        int bacon = 0;
        bacon += tags.getInteger("Head") == pigiron ? 1 : 0;
        bacon += tags.getInteger("Handle") == pigiron ? 1 : 0;
        bacon += tags.getInteger("Accessory") == pigiron ? 1 : 0;
        bacon += tags.getInteger("Extra") == pigiron ? 1 : 0;
        int chance = tool.getPartAmount() * 100;
        if (random.nextInt(chance) < bacon)
        {
            if (entity instanceof EntityPlayer)
                AbilityHelper.spawnItemAtPlayer((EntityPlayer) entity, new ItemStack(TinkerWorld.strangeFood, 1, 2));
            else
                AbilityHelper.spawnItemAtEntity(entity, new ItemStack(TinkerWorld.strangeFood, 1, 2), 0);
        }
    }
}
