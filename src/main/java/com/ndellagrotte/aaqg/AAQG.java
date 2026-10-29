package com.ndellagrotte.aaqg;

import java.util.Map;
import java.util.Set;

import com.ndellagrotte.aaqg.api.GraveRegistry;
import com.ndellagrotte.aaqg.api.IGraveInventory;
import com.ndellagrotte.aaqg.commands.ModCommands;
import com.ndellagrotte.aaqg.invman.GraveInventoryManager;
import com.ndellagrotte.aaqg.proxy.CommonProxy;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = "@{MOD_ID}", name = "@{MOD_NAME}", version = "@{MOD_VERSION}", dependencies = AAQG.DEPENDENCIES)
public class AAQG {

	public static final String DEPENDENCIES = "required-after:forge@[14.23.3.2655,)";
	private static Set<ASMData> moduleASM;

	@Mod.Instance
	public static AAQG INSTANCE = new AAQG();

	@SidedProxy(clientSide = "com.ndellagrotte.aaqg.proxy.ClientProxy", serverSide = "com.ndellagrotte.aaqg.proxy.ServerProxy")
	public static CommonProxy proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		proxy.preinit(e);
		moduleASM = e.getAsmData().getAll(GraveRegistry.class.getName());
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent e) {
		proxy.init(e);
		for (ASMData data : moduleASM) {
			try {
				Class<?> c = Class.forName(data.getClassName());
				if (IGraveInventory.class.isAssignableFrom(c)) {
					Map<String, Object> annotation = data.getAnnotationInfo();
					String reqMod = (String) annotation.get("reqMod");
					if (reqMod == null || Loader.isModLoaded(reqMod)){
						GraveInventoryManager.getInstance().registerListener((IGraveInventory) c.getDeclaredConstructor().newInstance(), annotation);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Mod.EventHandler
	public void postinit(FMLPostInitializationEvent e) {
		proxy.postinit(e);

		// Make sure to finalize the listeners so the mod actually works...
		GraveInventoryManager.getInstance().finalizeListeners();
	}

	@Mod.EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		ModCommands.initCommands(event);
	}
}
