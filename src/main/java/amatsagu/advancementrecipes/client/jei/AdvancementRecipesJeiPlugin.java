package amatsagu.advancementrecipes.client.jei;

import amatsagu.advancementrecipes.AdvancementRecipes;
import amatsagu.advancementrecipes.data.QuestData;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;

@JeiPlugin
public class AdvancementRecipesJeiPlugin implements IModPlugin {
	private static final Identifier PLUGIN_UID = AdvancementRecipes.id("jei_plugin");

	@Override
	public Identifier getPluginUid() {
		return PLUGIN_UID;
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		AdvancementRecipes.LOGGER.info("Registering JEI ingredient info pages for gated recipes");
		for (Map.Entry<Identifier, List<Identifier>> entry : QuestData.getAllItemGates().entrySet()) {
			Identifier itemId = entry.getKey();
			List<Identifier> questIds = entry.getValue();
			if (questIds == null || questIds.isEmpty()) {
				continue;
			}

			Item item = BuiltInRegistries.ITEM.getValue(itemId);
			if (item == null || item == Items.AIR) {
				continue;
			}

			String formattedQuests = QuestData.formatQuestList(questIds);
			Component infoText = Component.literal("§6Advancement Recipes Progression:\n§rCrafting and usage of this item requires completing quest " + formattedQuests + ".");
			registration.addIngredientInfo(item, infoText);
		}
	}
}
