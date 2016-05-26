package com.zerofall.ezstorage;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import com.zerofall.ezstorage.config.EZConfig;
import com.zerofall.ezstorage.crafting.CraftingManager;
import com.zerofall.ezstorage.events.CoreEvents;
import com.zerofall.ezstorage.events.SecurityEvents;
import com.zerofall.ezstorage.gui.GuiHandler;
import com.zerofall.ezstorage.init.EZBlocks;
import com.zerofall.ezstorage.init.EZItems;
import com.zerofall.ezstorage.network.EZNetwork;
import com.zerofall.ezstorage.proxy.CommonProxy;

/** EZStorage main mod class */
@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
public class EZStorage {
	
    @Mod.Instance(Reference.MOD_ID)
    public static EZStorage instance;
    
    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    public static CommonProxy proxy;
    
    public static SimpleNetworkWrapper networkWrapper;
    public static Configuration config;
    
    public EZTab creativeTab;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	config = new Configuration(event.getSuggestedConfigurationFile());
    	EZConfig.syncConfig();
    	this.creativeTab = new EZTab();
    	EZBlocks.mainRegistry();
    	EZItems.mainRegistry();
    	NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
    	networkWrapper = EZNetwork.registerNetwork();
    	MinecraftForge.EVENT_BUS.register(new CoreEvents());
    	MinecraftForge.EVENT_BUS.register(new SecurityEvents());
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
    	CraftingManager.mainRegistry();
    	proxy.registerRenders();
    }
    
}
