package me.basiqueevangelist.fastworldactions;

import me.basiqueevangelist.fastworldactions.action.WorldAction;
import me.basiqueevangelist.fastworldactions.action.WorldActionSync;
import me.basiqueevangelist.fastworldactions.task.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FastWorldActions implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("FastWorldActions");

	@Override
	public void onInitialize() {
		WorldActionTask.init();
		WorldActionSync.init();

		PayloadTypeRegistry.playS2C().register(WorldActionPacket.TYPE, WorldActionPacket.STREAM_CODEC);
	}

	public static WorldActionRunBuilder action(WorldAction action) {
		return new WorldActionRunBuilder(action);
	}

	@ApiStatus.Internal
	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath("fast-world-actions", path);
	}
}