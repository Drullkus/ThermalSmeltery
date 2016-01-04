package us.drullk.thermalsmeltery.common.gui;

import us.drullk.thermalsmeltery.common.tile.TileMachineBase;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler
{
    /**
     * Returns a Server side Container to be displayed to the user.
     *
     * @param ID     The Gui ID Number
     * @param player The player viewing the Gui
     * @param world  The current world
     * @param x      X Position
     * @param y      Y Position
     * @param z      Z Position
     * @return A GuiScreen/Container to be displayed to the user, null if none.
     */
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch (ID)
        {
            case 0:
                TileEntity tile = world.getTileEntity(x, y, z);
                if (tile instanceof TileMachineBase)
                {
                    return ((TileMachineBase)tile).getGuiServer(player.inventory);
                }
        }
        return null;
    }

    /**
     * Returns a Container to be displayed to the user. On the client side, this
     * needs to return a instance of GuiScreen On the server side, this needs to
     * return a instance of Container
     *
     * @param ID     The Gui ID Number
     * @param player The player viewing the Gui
     * @param world  The current world
     * @param x      X Position
     * @param y      Y Position
     * @param z      Z Position
     * @return A GuiScreen/Container to be displayed to the user, null if none.
     */
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch (ID)
        {
            case 0:
                TileEntity tile = world.getTileEntity(x, y, z);
                if (tile instanceof TileMachineBase)
                {
                    return ((TileMachineBase)tile).getGuiClient(player.inventory);
                }
        }
        return null;
    }
}
