package us.drullk.thermalsmeltery.common.tile;

import mantle.blocks.abstracts.InventoryLogic;
import mantle.blocks.iface.IActiveLogic;
import mantle.blocks.iface.IFacingLogic;
import mantle.blocks.iface.IMasterLogic;
import mantle.blocks.iface.IServantLogic;
import mantle.world.CoordTuple;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import tconstruct.TConstruct;
import tconstruct.library.crafting.Smeltery;
import us.drullk.thermalsmeltery.ThermalSmeltery;
import us.drullk.thermalsmeltery.common.blocks.IRFSmeltery;
import us.drullk.thermalsmeltery.common.core.handler.TSmeltConfig;

import java.util.ArrayList;
import java.util.Random;

public class TileRFSmelteryLogic extends InventoryLogic implements IActiveLogic, IFacingLogic, IFluidTank, IMasterLogic, ITileRFSmeltery
{
	public static final int MAX_SMELTERY_SIZE_DIAMETER = 7, MAX_SMELTERY_HEIGHT = 16, MB_P_BLOCK = TConstruct.ingotLiquidValue * 16;

	private static final int maxTick = TSmeltConfig.tConSmelteryTickFrequency >= TSmeltConfig.tickCeiling ? TSmeltConfig.tickCeiling : TSmeltConfig.tConSmelteryTickFrequency;

	public boolean validStructure;

	protected byte direction;

	private boolean inUse = false, debug = true, needsUpdate;

	private int tick, maxInvCapacity, smelteryBottomHeight, smelteryTopHeight, diameter, maxFluidCapacity, maxRFCapacity = 10000, RFStorage = 0, totalRFCost = 0;

	private int[] meltingTempPoints, currentActiveTemp;

	private ArrayList<FluidStack> fluidStorage = new ArrayList<FluidStack>();

	private CoordTuple minCoordTuple, maxCoordTuple;

	Random rand = new Random();

	public TileRFSmelteryLogic()
	{
		super(0);
	}

	public void setNeedsUpdate()
	{
		needsUpdate = true;
	}

	@Override
	public Container getGuiContainer(InventoryPlayer inventoryplayer, World world, int x, int y, int z)
	{
		return null;
	}

	@Override
	protected String getDefaultName()
	{
		return "TSmelt.RFSmeltery";
	}

	@Override
	public void updateEntity()
	{
		if(!needsUpdate)
		{
			tick++;
		}

		if(tick >= maxTick || needsUpdate)
		{
			needsUpdate = false;

			doMetrics();

			if(validStructure)
			{
				reassertRFStorage();

				reassertFluidStorage();

				reassertItemStorage();

				doActions();
			}

			tick = 0;
		}
	}

	private void reassertFluidStorage()
	{
		ArrayList<FluidStack> fluidStored = fluidStorage;
		int fluidAmountStored = getTotalFluidAmount();
		int excessFluidAmount;

		if(fluidAmountStored > maxFluidCapacity)
		{
			for(int c = fluidStored.size() - 1; c <= 0 || fluidAmountStored <= maxFluidCapacity; c++)
			{
				excessFluidAmount = fluidAmountStored - maxFluidCapacity;

				if(excessFluidAmount >= fluidStored.get(c).amount)
				{
					/*while(fluidStored.get(c).amount >= 1000)
					{
						fluidStored.get(c).amount -= 1000;

						//TODO: Put blocks of fluid in the world
					}*/

					fluidStored.remove(c);

					fluidAmountStored = getTotalFluidAmount();
				}
				else
				{
					fluidStored.get(c).amount -= excessFluidAmount;

					fluidAmountStored = getTotalFluidAmount();
				}
			}
		}

		fluidStorage = fluidStored;
	}

	/*
	First, check to see if it has items.
		If yes, get temps for each of them and save to ArrayList
		Do we have RF?
			Update Progress Arraylist
			If progress equals to temp in array list
				Smelt that item!
	*/

	private void doActions()
	{
		checkHasItems(); // Will set inUse to true if there are items

		if(inUse)
		{
			inUse = false;

			if(RFStorage > 0)
			{
				int highestTemperature = 0;
				int totalTemperature = 0;
				int averageTemperature;

				for(int c = 0; c >= inventory.length; c++)
				{
					if(inventory[c] == null)
					{
						meltingTempPoints[c] = 0;
					}
					else if(Smeltery.getSmelteryResult(inventory[c]) == null)
					{
						meltingTempPoints[c] = 0;
					}
					else
					{
						meltingTempPoints[c] = Smeltery.getLiquifyTemperature(inventory[c]);

						if(highestTemperature < meltingTempPoints[c])
						{
							highestTemperature = meltingTempPoints[c];
						}

						totalTemperature += meltingTempPoints[c];
					}
				}

				averageTemperature = (totalTemperature / (meltingTempPoints.length >= 1 ? meltingTempPoints.length : 1));
				totalRFCost = ((int) (2.17293 * averageTemperature * ((3 - (Math.log10(5 * diameter))) / 5))) * TSmeltConfig.multiplier;

				int fluidStored = getTotalFluidAmount();

				for(int c = 0; c >= inventory.length && totalRFCost > RFStorage; c++)
				{
					if(meltingTempPoints[c] > 0)
					{
						currentActiveTemp[c] += diameter;

						RFStorage -= totalRFCost * (diameter);

						if(currentActiveTemp[c] >= Smeltery.getLiquifyTemperature(inventory[c]))
						{
							FluidStack smelteryResult = Smeltery.getSmelteryResult(inventory[c]);

							if(fluidStored + smelteryResult.amount <= maxFluidCapacity)
							{
								fluidStored += smelteryResult.amount;
								fluidStorage.add(smelteryResult);
								inventory[c] = null;
							}
						}
					}
				}
			}
		}
		else
		{
			totalRFCost = 0;
		}
	}

	private int getTotalFluidAmount()
	{
		int fluidStored = 0;

		for(FluidStack fs : fluidStorage)
		{
			if(fs != null)
			{
				fluidStored += fs.amount;
			}
		}

		return fluidStored;
	}

	public void reassertItemStorage()
	{
		if(inventory.length > maxInvCapacity)
		{
			// Dump excess items on ground
			for(int c = inventory.length - maxInvCapacity; c > maxInvCapacity || c >= 0; c--)
			{
				ItemStack stack = inventory[c];
				if(stack != null)
				{
					float jumpX = rand.nextFloat() * 0.8F + 0.1F;
					float jumpY = rand.nextFloat() * 0.8F + 0.1F;
					float jumpZ = rand.nextFloat() * 0.8F + 0.1F;

					int offsetX = 0;
					int offsetZ = 0;
					switch(getRenderDirection())
					{
						case 2: // +z
							offsetZ = -1;
							break;
						case 3: // -z
							offsetZ = 1;
							break;
						case 4: // +x
							offsetX = -1;
							break;
						case 5: // -x
							offsetX = 1;
							break;
					}

					while(stack.stackSize > 0)
					{
						int itemSize = rand.nextInt(21) + 10;

						if(itemSize > stack.stackSize)
						{
							itemSize = stack.stackSize;
						}

						stack.stackSize -= itemSize;
						EntityItem entityitem = new EntityItem(worldObj, (double) ((float) xCoord + jumpX + offsetX), (double) ((float) yCoord + jumpY), (double) ((float) zCoord + jumpZ + offsetZ), new ItemStack(stack.getItem(), itemSize, stack.getItemDamage()));

						if(stack.hasTagCompound())
						{
							entityitem.getEntityItem().setTagCompound((NBTTagCompound) stack.getTagCompound().copy());
						}

						float offset = 0.05F;
						entityitem.motionX = (double) ((float) rand.nextGaussian() * offset);
						entityitem.motionY = (double) ((float) rand.nextGaussian() * offset + 0.2F);
						entityitem.motionZ = (double) ((float) rand.nextGaussian() * offset);
						worldObj.spawnEntityInWorld(entityitem);
					}
				}
			}

			reassertInventorySizes();
		}
		else if(inventory.length < maxInvCapacity)
		{
			reassertInventorySizes();
		}
	}

	private void reassertInventorySizes()
	{
		ItemStack[] temporaryInventory = new ItemStack[maxInvCapacity];
		int[] temporaryMeltingTempPoints = new int[maxInvCapacity];
		int[] temporaryCurrentActiveTemp = new int[maxInvCapacity];

		for(int c = 0; c < temporaryInventory.length; c++)
		{
			temporaryInventory[c] = inventory[c];
			temporaryMeltingTempPoints[c] = meltingTempPoints[c];
			temporaryCurrentActiveTemp[c] = currentActiveTemp[c];
		}

		inventory = temporaryInventory;
		meltingTempPoints = temporaryMeltingTempPoints;
		currentActiveTemp = temporaryCurrentActiveTemp;
	}

	private void checkHasItems()
	{
		inUse = false;
		for(int i = 0; i < maxInvCapacity; i++)
		{
			if(this.isStackInSlot(i))
			{
				inUse = true;
				break;
			}
		}
	}

	public void doMetrics()
	{
		diameter = getSmelteryDiameter(xCoord, yCoord, zCoord);

		if(diameter != -1)
		{
			if(debug)
			{
				ThermalSmeltery.logger.info("Diameter: " + diameter);
			}

			validStructure = checkValidStructure();

			if(debug)
			{
				ThermalSmeltery.logger.info("Structure: " + validStructure);
			}

			if(validStructure)
			{
				maxInvCapacity = diameter * diameter * (smelteryTopHeight - smelteryBottomHeight);
				maxFluidCapacity = maxInvCapacity * MB_P_BLOCK;
				maxRFCapacity = ((int) ((double) maxFluidCapacity / 1.5)) + 10000;
			}
		}
		else
		{
			validStructure = false;

			totalRFCost = 0;

			maxRFCapacity = 10000;

			if(debug)
			{
				ThermalSmeltery.logger.warn("Invalid RF Smeltery Construction!");
			}
		}
	}

	private void reassertRFStorage()
	{
		if(RFStorage < 0)
		{
			RFStorage = 0;
		}
		else if(RFStorage > maxRFCapacity)
		{
			RFStorage = maxRFCapacity;
		}
	}

	public int getSmelteryDiameter(int x, int y, int z)
	{
		// Parameters x, y, z should "put" us in the smeltery. Or rather, scanning the empty space.
		// checkValidPlacement() puts this scanning method "inside the smeltery"
		// Returns the diameter
		int diameterSpaceVerified = 0;

		switch(getRenderDirection())
		{
			case 2: // +z
				for(int west = 1; west <= MAX_SMELTERY_SIZE_DIAMETER && west == diameterSpaceVerified + 1; west++)
				{
					if(diameterSpaceVerified + 1 == west && (worldObj.getBlock(x, y, z + west) == null || worldObj.isAirBlock(x, y, z + west)))
					{
						if(debug)
						{
							worldObj.setBlock(x, y + 1, z + west, Blocks.gold_ore);
						}
						diameterSpaceVerified++;
					}

					if(west == MAX_SMELTERY_SIZE_DIAMETER && diameterSpaceVerified + 1 == west && (worldObj.getBlock(x, y, z + west + 1) == null || worldObj.isAirBlock(x, y, z + west + 1)))
					{
						return -1;
					}
				}
				break;
			case 3: // -z
				for(int east = 1; east <= MAX_SMELTERY_SIZE_DIAMETER && east == diameterSpaceVerified + 1; east++)
				{
					if(diameterSpaceVerified + 1 == east && (worldObj.getBlock(x, y, z - east) == null || worldObj.isAirBlock(x, y, z - east)))
					{
						if(debug)
						{
							worldObj.setBlock(x, y + 1, z - east, Blocks.emerald_ore);
						}
						diameterSpaceVerified++;
					}

					if(east == MAX_SMELTERY_SIZE_DIAMETER && diameterSpaceVerified + 1 == east && (worldObj.getBlock(x, y, z - east - 1) == null || worldObj.isAirBlock(x, y, z - east - 1)))
					{
						return -1;
					}
				}
				break;
			case 4: // +x
				for(int south = 1; south <= MAX_SMELTERY_SIZE_DIAMETER && south == diameterSpaceVerified + 1; south++)
				{
					if(diameterSpaceVerified + 1 == south && (worldObj.getBlock(x + south, y, z) == null || worldObj.isAirBlock(x + south, y, z)))
					{
						if(debug)
						{
							worldObj.setBlock(x + south, y + 1, z, Blocks.diamond_ore);
						}
						diameterSpaceVerified++;
					}

					if(south == MAX_SMELTERY_SIZE_DIAMETER && diameterSpaceVerified + 1 == south && (worldObj.getBlock(x + south + 1, y, z) == null || worldObj.isAirBlock(x + south + 1, y, z)))
					{
						return -1;
					}
				}
				break;
			case 5: // -x
				for(int north = 1; north <= MAX_SMELTERY_SIZE_DIAMETER && north == diameterSpaceVerified + 1; north++)
				{
					if(diameterSpaceVerified + 1 == north && (worldObj.getBlock(x - north, y, z) == null || worldObj.isAirBlock(x - north, y, z)))
					{
						if(debug)
						{
							worldObj.setBlock(x - north, y + 1, z, Blocks.redstone_ore);
						}
						diameterSpaceVerified++;
					}

					if(north == MAX_SMELTERY_SIZE_DIAMETER && diameterSpaceVerified + 1 == north && (worldObj.getBlock(x - north - 1, y, z) == null || worldObj.isAirBlock(x - north - 1, y, z)))
					{
						return -1;
					}
				}
				break;
			default:
				if(debug)
				{
					ThermalSmeltery.logger.warn("There was a problem with determining rotation direction of RFSmeltery!");
				}
				return -1;
		}

		return diameterSpaceVerified;
	}

	public boolean checkValidStructure()
	{
		int x = xCoord, y = yCoord, z = zCoord, smelteryBottomOffset = 1, smelteryTopOffset = 1, radius = getSmelteryCenterOffset(diameter);

		switch(getRenderDirection())
		{
			case 2: // +z
				z = zCoord - radius - 1;
				break;
			case 3: // -z
				z = zCoord + radius + 1;
				break;
			case 4: // +x
				x = xCoord - radius - 1;
				break;
			case 5: // -x
				x = xCoord + radius + 1;
				break;
		}

		checkSmelteryLayers(x, y, z, radius);

		for(int c = 1; c < MAX_SMELTERY_HEIGHT; c++)
		{
			if(smelteryBottomOffset == c)
			{
				if(checkSmelteryLayers(x, y - c, z, radius))
				{
					smelteryBottomOffset++;
				}
				else if(checkSmelteryBottom(x, y - c, z, radius))
				{
					smelteryBottomHeight = yCoord - c - 1;
				}
				else
				{
					return false;
				}
			}
			else if(smelteryTopOffset == (c - smelteryBottomOffset))
			{
				if(checkSmelteryLayers(x, y + (c - smelteryBottomOffset), z, radius))
				{
					smelteryTopOffset++;
				}
				else
				{
					smelteryTopHeight = yCoord + smelteryTopOffset;

					return true;
				}
			}
		}

		return true;
	}

	public boolean checkSmelteryBottom(int x, int y, int z, int range)
	{
		for(int yD = -range; yD <= range; yD++)
		{
			for(int xD = -range; xD <= range; xD++)
			{
				// If there is air, then this whole method is invalidated
				if((worldObj.getBlock(x, y, z) == null || worldObj.isAirBlock(x, y, z)))
				{
					return false;
				}
				else if(!(worldObj.getBlock(x, y, z) instanceof ITileRFSmeltery))
				{
					return false;
				}
			}
		}

		return true;
	}

	public boolean checkSmelteryLayers(int x, int y, int z, int range)
	{
		// Check walls
		for(int c = -range - 1; c <= range + 1; c++)
		{
			// Check z+ walls
			if(!(worldObj.getBlock(x + c, y, z + range + 1) != null && worldObj.getBlock(x, y, z) instanceof IRFSmeltery))
			{
				return false;
			}

			// Check z- walls
			if(!(worldObj.getBlock(x + c, y, z - range - 1) != null && worldObj.getBlock(x, y, z) instanceof IRFSmeltery))
			{
				return false;
			}

			// Check x+ walls
			if(!(worldObj.getBlock(x + range + 1, y, z + c) != null && worldObj.getBlock(x, y, z) instanceof IRFSmeltery))
			{
				return false;
			}

			// Check x- walls
			if(!(worldObj.getBlock(x - range - 1, y, z + c) != null && worldObj.getBlock(x, y, z) instanceof IRFSmeltery))
			{
				return false;
			}
		}

		// Check empty space.
		for(int yD = -range; yD <= range; yD++)
		{
			for(int xD = -range; xD <= range; xD++)
			{
				// If there is a block, then this whole method is invalidated
				if(!(worldObj.getBlock(x, y, z) == null || worldObj.isAirBlock(x, y, z)))
				{
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public byte getRenderDirection()
	{
		return direction;
	}

	@Override
	public ForgeDirection getForgeDirection()
	{
		return ForgeDirection.VALID_DIRECTIONS[direction];
	}

	@Override
	public void setDirection(int side)
	{

	}

	@Override
	public void setDirection(float yaw, float pitch, EntityLivingBase player)
	{
		int facing = MathHelper.floor_double((double) (yaw / 360) + 0.5D) & 3;
		switch(facing)
		{
			case 0:
				direction = 2;
				break;

			case 1:
				direction = 5;
				break;

			case 2:
				direction = 3;
				break;

			case 3:
				direction = 4;
				break;
		}
	}

	public int getSmelteryCenterOffset(int diameter)
	{
		return (diameter - (diameter % 2)) / 2;
	}

	@Override
	public boolean getActive()
	{
		return validStructure;
	}

	@Override
	public void setActive(boolean flag)
	{
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public String getInventoryName()
	{
		return getDefaultName();
	}

	@Override
	public void openInventory()
	{
		// Useless
	}

	@Override
	public void closeInventory()
	{
		// Useless
	}

	@Override
	public void notifyChange(IServantLogic servant, int x, int y, int z)
	{
		//TODO Fix
	}

	@Override
	public int receiveEnergy(ForgeDirection forgeDirection, int i, boolean b)
	{
		return 0;
	}

	@Override
	public int getEnergyStored(ForgeDirection forgeDirection)
	{
		return 0;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection forgeDirection)
	{
		return maxRFCapacity;
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection forgeDirection)
	{
		return validStructure;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
	{
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid)
	{
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid)
	{
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from)
	{
		return new FluidTankInfo[0];
	}

	@Override
	public FluidStack getFluid()
	{
		return null;
	}

	@Override
	public int getFluidAmount()
	{
		return 0;
	}

	@Override
	public int getCapacity()
	{
		return maxFluidCapacity;
	}

	@Override
	public FluidTankInfo getInfo()
	{
		return null;
	}

	@Override
	public int fill(FluidStack resource, boolean doFill)
	{
		return 0;
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain)
	{
		return null;
	}

	@Override
	public void readFromNBT(NBTTagCompound tags)
	{
		validStructure = tags.getBoolean("ValidStructure");
		RFStorage = tags.getInteger("PowerStored");
		inUse = tags.getBoolean("InUse");

		direction = tags.getByte("Direction");
		smelteryBottomHeight = tags.getInteger("BottomHeight");
		smelteryTopHeight = tags.getInteger("TopHeight");

		maxFluidCapacity = tags.getInteger("MaxLiquid");
		maxRFCapacity = tags.getInteger("MaxRF");
		maxInvCapacity = tags.getInteger("MaxInventory");

		int[] pos = tags.getIntArray("MinPos");
		if(pos.length > 2)
		{
			minCoordTuple = new CoordTuple(pos[0], pos[1], pos[2]);
		}
		else
		{
			minCoordTuple = new CoordTuple(xCoord, yCoord, zCoord);
		}

		pos = tags.getIntArray("MaxPos");
		if(pos.length > 2)
		{
			maxCoordTuple = new CoordTuple(pos[0], pos[1], pos[2]);
		}
		else
		{
			maxCoordTuple = new CoordTuple(xCoord, yCoord, zCoord);
		}

		meltingTempPoints = tags.getIntArray("MeltingTemperatures");
		currentActiveTemp = tags.getIntArray("ActiveTemperatures");

		NBTTagList liquidTag = tags.getTagList("Liquids", 10);
		fluidStorage.clear();

		for(int c = 0; c < liquidTag.tagCount(); c++)
		{
			NBTTagCompound nbt = liquidTag.getCompoundTagAt(c);
			FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);

			if(fluid != null)
			{
				fluidStorage.add(fluid);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tags)
	{
		super.writeToNBT(tags);

		tags.setBoolean("ValidStructure", validStructure);
		tags.setInteger("PowerStored", RFStorage);
		tags.setBoolean("InUse", inUse);

		tags.setByte("Direction", direction);
		tags.setInteger("BottomHeight", smelteryBottomHeight);
		tags.setInteger("TopHeight", smelteryTopHeight);

		tags.setInteger("MaxLiquid", maxFluidCapacity);
		tags.setInteger("MaxRF", maxRFCapacity);
		tags.setInteger("MaxInventory", maxInvCapacity);

		int[] pos;
		if(minCoordTuple == null)
		{
			pos = new int[]{xCoord, yCoord, zCoord};
		}
		else
		{
			pos = new int[]{minCoordTuple.x, minCoordTuple.y, minCoordTuple.z};
		}
		tags.setIntArray("MinPos", pos);

		if(maxCoordTuple == null)
		{
			pos = new int[]{xCoord, yCoord, zCoord};
		}
		else
		{
			pos = new int[]{maxCoordTuple.x, maxCoordTuple.y, maxCoordTuple.z};
		}
		tags.setIntArray("MaxPos", pos);

		tags.setIntArray("MeltingTemperatures", meltingTempPoints);
		tags.setIntArray("ActiveTemperatures", currentActiveTemp);

		NBTTagList taglist = new NBTTagList();

		for(FluidStack liquid : fluidStorage)
		{
			NBTTagCompound nbt = new NBTTagCompound();
			liquid.writeToNBT(nbt);
			taglist.appendTag(nbt);
		}

		tags.setTag("Liquids", taglist);
	}

	private CoordTuple getMinCoords()
	{
		int x = xCoord, y = yCoord, z = zCoord, radius = getSmelteryCenterOffset(diameter);

		CoordTuple minCoords;

		switch(getRenderDirection())
		{
			case 2: // +z
				z = zCoord - radius - 1;
				break;
			case 3: // -z
				z = zCoord + radius + 1;
				break;
			case 4: // +x
				x = xCoord - radius - 1;
				break;
			case 5: // -x
				x = xCoord + radius + 1;
				break;
		}

		minCoords = new CoordTuple(x - radius, smelteryBottomHeight, z - radius);

		return minCoords;
	}

	private CoordTuple getMaxCoords()
	{
		int x = xCoord, y = yCoord, z = zCoord, radius = getSmelteryCenterOffset(diameter);

		CoordTuple maxCoords;

		switch(getRenderDirection())
		{
			case 2: // +z
				z = zCoord - radius - 1;
				break;
			case 3: // -z
				z = zCoord + radius + 1;
				break;
			case 4: // +x
				x = xCoord - radius - 1;
				break;
			case 5: // -x
				x = xCoord + radius + 1;
				break;
		}

		maxCoords = new CoordTuple(x - radius, smelteryBottomHeight, z - radius);

		return maxCoords;
	}

	public CoordTuple getMinCoordTuple()
	{
		return minCoordTuple;
	}

	public CoordTuple getMaxCoordTuple()
	{
		return maxCoordTuple;
	}

	public int getHeight()
	{
		return smelteryTopHeight - smelteryBottomHeight;
	}

	public int getDiameter()
	{
		return diameter;
	}

	public ArrayList<FluidStack> getFluidStorage()
	{
		return fluidStorage;
	}

	public int getCapacityPerLayer()
	{
		return diameter * diameter * MB_P_BLOCK;
	}

	public int getTempForSlot (int slot)
	{
		return currentActiveTemp[slot];
	}
}
