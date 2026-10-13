package amatsagu.advancementrecipes;

import amatsagu.advancementrecipes.data.Progression;
import amatsagu.advancementrecipes.data.QuestData;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.gamerules.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class AdvancementRecipes implements ModInitializer {
	public static final String MOD_ID = "advancement_recipes";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Advancement Recipes (Fabric 26.3)");
		QuestData.init();
		Progression.init();

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			Boolean isLimited = server.getGameRules().get(GameRules.LIMITED_CRAFTING);
			if (Boolean.FALSE.equals(isLimited)) {
				server.getGameRules().set(GameRules.LIMITED_CRAFTING, true, server);
				LOGGER.info("Enforced gamerule limitedCrafting = true for Advancement Recipes progression");
			}
		});

		// Event-driven only: on join, realign roots/categories and re-grant completed quest recipes.
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayer player = handler.player;
			if (player == null) {
				return;
			}

			synchronize(player, server);
		});

		// Datapack reloads happen on a live server (e.g. /reload after updating the mod). Re-sync the
		// players who are already online so they receive recipes added to quests they already finished,
		// instead of waiting for their next reconnect.
		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
			if (!success) {
				return;
			}
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				synchronize(player, server);
			}
		});
	}

	/** Realigns questline roots/categories and re-grants the recipe rewards of completed quests. */
	private static void synchronize(ServerPlayer player, MinecraftServer server) {
		Progression.refresh(player);

		int knownBefore = player.getRecipeBook().pack().known().size();
		syncRecipeRewards(player, server);
		int knownAfter = player.getRecipeBook().pack().known().size();
		LOGGER.info("Synchronized quest recipes for {} ({} -> {} unlocked recipes)",
				player.getName().getString(), knownBefore, knownAfter);
	}

	/** Re-grants the recipe rewards of every completed quest advancement to keep the recipe book in sync on join. */
	private static void syncRecipeRewards(ServerPlayer player, MinecraftServer server) {
		ServerAdvancementManager manager = server.getAdvancements();
		Set<ResourceKey<Recipe<?>>> recipes = new LinkedHashSet<>();

		for (AdvancementHolder holder : manager.getAllAdvancements()) {
			if (!holder.id().getNamespace().equals("rework")) {
				continue;
			}
			if (!player.getAdvancements().getOrStartProgress(holder).isDone()) {
				continue;
			}
			recipes.addAll(holder.value().rewards().recipes());
		}

		if (!recipes.isEmpty()) {
			player.awardRecipesByKey(new ArrayList<>(recipes));
		}
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
