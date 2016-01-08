package us.drullk.thermalsmeltery.proxy;

import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraftforge.common.MinecraftForge;
import us.drullk.thermalsmeltery.client.RFSmelteryRenderer;

public class TSmeltClientProxy extends TSmeltCommonProxy
{
	@Override
	public void init()
	{
		RenderingRegistry.registerBlockHandler(new RFSmelteryRenderer());

		MinecraftForge.EVENT_BUS.register(this);
	}
}
