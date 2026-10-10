package amatsagu.advancementrecipes.client;

import amatsagu.advancementrecipes.AdvancementRecipes;
import amatsagu.advancementrecipes.data.QuestData;
import net.fabricmc.api.ClientModInitializer;

public class AdvancementRecipesClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		AdvancementRecipes.LOGGER.info("Initializing Advancement Recipes Client");
		QuestData.init();
	}
}
