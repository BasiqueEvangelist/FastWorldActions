package me.basiqueevangelist.fastworldactions;

import me.basiqueevangelist.fastworldactions.action.WorldAction;
import me.basiqueevangelist.fastworldactions.action.WorldActionSync;
import me.basiqueevangelist.fastworldactions.task.*;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FastWorldActions implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("FastWorldActions");

	@Override
	public void onInitialize() {
		WorldActionTask.init();
		WorldActionSync.init();
	}

	public static WorldActionRunBuilder action(WorldAction action) {
		return new WorldActionRunBuilder(action);
	}
}