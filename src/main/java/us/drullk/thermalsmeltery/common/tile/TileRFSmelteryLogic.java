package us.drullk.thermalsmeltery.common.tile;

import cofh.api.energy.IEnergyReceiver;
import cofh.api.energy.IEnergyStorage;
import mantle.blocks.abstracts.InventoryLogic;
import mantle.blocks.iface.IActiveLogic;
import mantle.blocks.iface.IFacingLogic;
import mantle.blocks.iface.IMasterLogic;
import mantle.blocks.iface.IServantLogic;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import tconstruct.TConstruct;
import us.drullk.thermalsmeltery.ThermalSmeltery;
import us.drullk.thermalsmeltery.common.core.handler.TSmeltConfig;

public class TileRFSmelteryLogic extends InventoryLogic implements IActiveLogic, IFacingLogic, IFluidTank, IMasterLogic, IEnergyReceiver, IEnergyStorage, IFluidHandler
{
	private boolean debug = true;

	private int tick;

	private final int maxTick = TSmeltConfig.tConSmelteryTickFrequency >= 120 ? 120 : TSmeltConfig.tConSmelteryTickFrequency;

	public static final int MAX_SMELTERY_SIZE_DIAMETER = 8, MB_P_BLOCK = TConstruct.ingotLiquidValue * 20;

	public boolean validStructure;

	protected byte direction;

	public TileRFSmelteryLogic(int invSize)
	{
		super(invSize);
	}

	@Override
	public Container getGuiContainer(InventoryPlayer inventoryplayer, World world, int x, int y, int z)
	{
		return null;
	}

	@Override
	protected String getDefaultName()
	{
		return "TSmeltery.RFSmeltery";
	}

	@Override
	public void updateEntity()
	{
		tick++;

		if(tick >= maxTick)
		{
			tick = 0;
			//detectEntities();
		}
		int radius = getSmelteryDiameter(xCoord, yCoord, zCoord);

		if(radius != -1)
		{
			if(debug)
			{
				ThermalSmeltery.logger.info("Radius: " + radius);
			}
		}
		else
		{
			if(debug)
			{
				ThermalSmeltery.logger.warn("Invalid RF Smeltery Construction!");
			}
		}
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

	@Override
	public String getInventoryName()
	{
		return getDefaultName();
	}

	@Override
	public void openInventory()
	{

	}

	@Override
	public void closeInventory()
	{

	}

	@Override
	public void notifyChange(IServantLogic servant, int x, int y, int z)
	{
		checkValidPlacement();
	}

	public int[] checkValidPlacement()
	{
		final int[] initialPlacement;

		switch(getRenderDirection())
		{
			case 2: // +z
				initialPlacement = new int[]{xCoord, yCoord, zCoord + 1};
				break;
			case 3: // -z
				initialPlacement = new int[]{xCoord, yCoord, zCoord - 1};
				break;
			case 4: // +x
				initialPlacement = new int[]{xCoord + 1, yCoord, zCoord};
				break;
			case 5: // -x
				initialPlacement = new int[]{xCoord - 1, yCoord, zCoord};
				break;
			default:
				initialPlacement = new int[]{xCoord, yCoord, zCoord};
				if(debug)
				{
					ThermalSmeltery.logger.warn("There was a problem with determining rotation direction of RFSmeltery!");
				}
				break;
		}

		return initialPlacement;
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
				for(int c = 0; c < MAX_SMELTERY_SIZE_DIAMETER || c == diameterSpaceVerified; c++)
				{
					if(worldObj.getBlock(x, y, z + c) == null || worldObj.isAirBlock(x, y, z + c))
					{
						diameterSpaceVerified++;
					}
				}
				break;
			case 3: // -z
				for(int c = 0; c < MAX_SMELTERY_SIZE_DIAMETER || c == diameterSpaceVerified; c++)
				{
					if(worldObj.getBlock(x, y, z - c) == null || worldObj.isAirBlock(x, y, z - c))
					{
						diameterSpaceVerified++;
					}
				}
				break;
			case 4: // +x
				for(int c = 0; c < MAX_SMELTERY_SIZE_DIAMETER || c == diameterSpaceVerified; c++)
				{
					if(worldObj.getBlock(x + c, y, z) == null || worldObj.isAirBlock(x + c, y, z))
					{
						diameterSpaceVerified++;
					}
				}
				break;
			case 5: // -x
				for(int c = 0; c < MAX_SMELTERY_SIZE_DIAMETER || c == diameterSpaceVerified; c++)
				{
					if(worldObj.getBlock(x - c, y, z) == null || worldObj.isAirBlock(x - c, y, z))
					{
						diameterSpaceVerified++;
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

	/*public int getSmelteryDiameter(int x, int y, int z)
	{
		// Parameters x, y, z should "put" us in the smeltery. Or rather, scanning the empty space.
		// checkValidPlacement() puts this scanning method "inside the smeltery"
		// Returns the radius, excluding the center block.

		// Adjust the x-position of the block until the difference between the outer walls is at most 1
		// basically this means we center the block inside the smeltery on the x axis.
		// Smeltery must be square!
		int xDiff1 = 1, xDiff2 = 1; // x-difference

		for(int i = 1; i < MAX_SMELTERY_SIZE_DIAMETER; i++) // Don't check farther than needed (MAX_SMELTERY_SIZE_DIAMETER)
		{
			if(xDiff1 == i && (worldObj.getBlock(x - xDiff1, y, z) == null || worldObj.isAirBlock(x - xDiff1, y, z)))
			{
				if(debug)
				{
					worldObj.setBlock(x - xDiff1, y + 1, z, Blocks.redstone_ore);
				}
				xDiff1++;
			}

			if(xDiff2 == i && (worldObj.getBlock(x + xDiff2, y, z) == null || worldObj.isAirBlock(x + xDiff2, y, z)))
			{
				if(debug)
				{
					worldObj.setBlock(x + xDiff2, y + 1, z, Blocks.diamond_ore);
				}
				xDiff2++;
			}
		}

		// Same for z-axis
		int zDiff1 = 1, zDiff2 = 1;
		for(int i = 1; i < MAX_SMELTERY_SIZE_DIAMETER; i++) // Don't check farther than needed
		{
			if(zDiff1 == i && (worldObj.getBlock(x, y, z - zDiff1) == null || worldObj.isAirBlock(x, y, z - zDiff1)))
			{
				if(debug)
				{
					worldObj.setBlock(x, y + 1, z - zDiff1, Blocks.emerald_ore);
				}
				zDiff1++;
			}

			if(zDiff2 == i && (worldObj.getBlock(x, y, z + zDiff2) == null || worldObj.isAirBlock(x, y, z + zDiff2)))
			{
				if(debug)
				{
					worldObj.setBlock(x, y + 1, z + zDiff2, Blocks.gold_ore);
				}
				zDiff2++;
			}
		}

		if((xDiff1 == xDiff2))
		{
			if(xDiff1 + xDiff2 - 1 == (zDiff1 > zDiff2 ? zDiff1 : zDiff2))
			{
				//System.out.println(zDiff1 + " and " + zDiff2 + " at " + xCoord + ", "+ yCoord + ", "+ zCoord);
				return xDiff1;
			}
		}

		if((zDiff1 == zDiff2))
		{
			if(zDiff1 + zDiff2 - 1 == (xDiff1 > xDiff2 ? xDiff1 : xDiff2))
			{
				//System.out.println(xDiff1 + " and " + xDiff2 + " at " + xCoord + ", "+ yCoord + ", "+ zCoord);
				return zDiff1;
			}
		}

		if(debug)
		{
			ThermalSmeltery.logger.warn("There was a problem! " + xDiff1 + ", " + xDiff2 + ", " + zDiff1 + ", " + zDiff2);
		}
		return -1;
	}*/

	public void checkValidStructure(int x, int y, int z, int[] sides)
	{
		int xSize, ySize, zSize;

		if(debug)
		{
			worldObj.setBlock(x, y + 1, z, Blocks.redstone_block);
			worldObj.setBlock(x - sides[0] + 1, y + 2, z - sides[2] + 1, Blocks.diamond_block);
			worldObj.setBlock(x + sides[1] - 1, y + 2, z + sides[3] - 1, Blocks.emerald_block);
		}

	}

	@Override
	public int receiveEnergy(int i, boolean b)
	{
		return 0;
	}

	@Override
	public int extractEnergy(int i, boolean b)
	{
		return 0;
	}

	@Override
	public int getEnergyStored()
	{
		return 0;
	}

	@Override
	public int getMaxEnergyStored()
	{
		return 0;
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
		return 0;
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection forgeDirection)
	{
		return false;
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
		return 0;
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
}
