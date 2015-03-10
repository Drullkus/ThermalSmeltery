package cofh.thermalexpansion.block.machine;

public class MachineHelper
{
    public static int getProcessRemaining(TileMachineBase machineBase)
    {
        return machineBase.processRem;
    }

    public static int getProcessMax(TileMachineBase machineBase)
    {
        return machineBase.processMax;
    }

    public static int getProcessMod(TileMachineBase machineBase)
    {
        return machineBase.processMod;
    }

    public static int getEnergyMod(TileMachineBase machineBase)
    {
        return machineBase.energyMod;
    }

    public static void setProcessMax(TileMachineBase machineBase, int val)
    {
        machineBase.processMax = val;
        machineBase.processRem = val;
    }

    public static void updateProcessRemaining(TileMachineBase machineBase, int val)
    {
        machineBase.processRem += val;
    }

    public static void setWasActive(TileMachineBase machineBase, boolean val)
    {
        machineBase.wasActive = val;
    }
}
