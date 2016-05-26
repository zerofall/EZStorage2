package com.zerofall.ezstorage.proxy;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.zerofall.ezstorage.init.EZBlocks;
import com.zerofall.ezstorage.init.EZItems;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void registerRenders() {
		EZBlocks.registerRenders();
		EZItems.registerRenders();
	}
	
}
