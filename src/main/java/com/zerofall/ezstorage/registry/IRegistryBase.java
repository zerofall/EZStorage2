package com.zerofall.ezstorage.registry;

import java.util.Random;

import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Interface to help with item and block registration */
public interface IRegistryBase {

	public static final Random random = new Random();

	/** gets the shorthand name of this item for registering */
	public String getShorthandName();

	/** register this item with the renderer */
	@SideOnly(Side.CLIENT)
	public void registerRender();

}
