package amatsagu.advancementrecipes;

import amatsagu.advancementrecipes.data.QuestData;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.gamerules.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdvancementRecipes implements ModInitializer {
	public static final String MOD_ID = "advancement_recipes";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

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
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
