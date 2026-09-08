package com.shouyun.workshop;

import com.shouyun.workshop.entity.ModEntities;
import com.shouyun.workshop.flight.WindFlightManager;
import com.shouyun.workshop.handler.WhirlwindHandler;
import com.shouyun.workshop.item.ModItems;
import com.shouyun.workshop.network.ModNetworking;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ShouyunWorkshop implements ModInitializer {
	public static final String MOD_ID = "shouyun_workshop";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.register();
		ModEntities.register();
		ModNetworking.registerServer();
		WhirlwindHandler.register();
		WindFlightManager.register();
		com.shouyun.workshop.summon.NecromancerSummons.register();
		LOGGER.info("Shouyun's Workshop initialized");
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
