package me.basiqueevangelist.fastworldactions;

import me.basiqueevangelist.fastworldactions.task.WorldActionTask;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FastWorldActions implements ModInitializer {
	@Override
	public void onInitialize() {
		WorldActionTask.init();
	}
}