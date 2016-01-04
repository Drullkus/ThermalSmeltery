package us.drullk.thermalsmeltery.common.tile;

import cofh.api.energy.IEnergyReceiver;
import mantle.blocks.abstracts.MultiServantLogic;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileRFSmelteryInterfaceLogic extends MultiServantLogic implements IFluidHandler, IEnergyReceiver
{
	@Override
	public boolean canUpdate ()
	{
		return false; // No ticking!
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
}
