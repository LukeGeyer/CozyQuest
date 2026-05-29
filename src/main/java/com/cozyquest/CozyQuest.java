package com.cozyquest;

import com.cozyquest.registry.ModBlocks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CozyQuest implements ModInitializer {
	public static final String MOD_ID = "cozyquest";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlocks.initialize();

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			QuestManager.onPlayerJoin(handler.player);
		});

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				QuestManager.checkQuests(player); 
			}
		}); //Every tick, check quests for all players
	}
}
