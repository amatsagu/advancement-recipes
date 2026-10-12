package amatsagu.advancementrecipes;

import amatsagu.advancementrecipes.data.QuestData;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.world.level.gamerules.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdvancementRecipes implements ModInitializer {
	public static final String MOD_ID = "advancement_recipes";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	/**
	 * Universal recipe sync function. Grants every recipe a player has earned through their
	 * completed quest advancements. This replaces the old per-update migration functions
	 * (update_26_1/update_26_3/update_patch/update_tr) and keeps existing saves in sync.
	 */
	private static final Identifier RECIPE_SYNC_FUNCTION =
			Identifier.fromNamespaceAndPath("rework", "sync_advancement_recipes");

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Advancement Recipes (Fabric 26.3)");
		QuestData.init();

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			Boolean isLimited = server.getGameRules().get(GameRules.LIMITED_CRAFTING);
			if (Boolean.FALSE.equals(isLimited)) {
				server.getGameRules().set(GameRules.LIMITED_CRAFTING, true, server);
				LOGGER.info("Enforced gamerule limitedCrafting = true for Advancement Recipes progression");
			}
		});

		// Re-sync a player's unlocked recipes with their quest progress every time they join.
		// The function is executed as the player (so @s resolves to them) with gamemaster
		// permissions, mirroring how vanilla runs advancement reward functions. It is
		// idempotent - re-granting an already unlocked recipe is a no-op.
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayer player = handler.player;
			if (player == null) {
				return;
			}
			var syncFunction = server.getFunctions().get(RECIPE_SYNC_FUNCTION);
			if (syncFunction.isEmpty()) {
				LOGGER.warn("Recipe sync function {} is missing - quest recipes will not be synced on join", RECIPE_SYNC_FUNCTION);
				return;
			}
			int knownBefore = player.getRecipeBook().pack().known().size();
			server.getFunctions().execute(syncFunction.get(),
					player.createCommandSourceStack()
							.withSuppressedOutput()
							.withPermission(LevelBasedPermissionSet.GAMEMASTER));
			int knownAfter = player.getRecipeBook().pack().known().size();
			LOGGER.info("Synchronized quest recipes for {} ({} -> {} unlocked recipes)",
					player.getName().getString(), knownBefore, knownAfter);
		});
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
