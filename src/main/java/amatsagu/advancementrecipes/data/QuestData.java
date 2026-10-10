package amatsagu.advancementrecipes.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class QuestData {
	private static final Logger LOGGER = LoggerFactory.getLogger("AdvancementRecipes/QuestData");

	private static final Map<Identifier, String> QUEST_TITLES = new HashMap<>();
	private static final Map<Identifier, Identifier> RECIPE_TO_QUEST = new HashMap<>();
	private static final Map<Identifier, List<Identifier>> ITEM_TO_QUESTS = new HashMap<>();
	private static boolean initialized = false;

	public static synchronized void init() {
		if (initialized) {
			return;
		}
		initialized = true;

		try (InputStream stream = QuestData.class.getResourceAsStream("/assets/advancement_recipes/quest_gates.json")) {
			if (stream == null) {
				LOGGER.error("Could not find /assets/advancement_recipes/quest_gates.json resource!");
				return;
			}

			JsonObject root = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();

			if (root.has("quest_titles")) {
				JsonObject titles = root.getAsJsonObject("quest_titles");
				for (Map.Entry<String, JsonElement> entry : titles.entrySet()) {
					QUEST_TITLES.put(Identifier.parse(entry.getKey()), entry.getValue().getAsString());
				}
			}

			if (root.has("recipe_to_quest")) {
				JsonObject recipes = root.getAsJsonObject("recipe_to_quest");
				for (Map.Entry<String, JsonElement> entry : recipes.entrySet()) {
					RECIPE_TO_QUEST.put(Identifier.parse(entry.getKey()), Identifier.parse(entry.getValue().getAsString()));
				}
			}

			if (root.has("item_to_quests")) {
				JsonObject items = root.getAsJsonObject("item_to_quests");
				for (Map.Entry<String, JsonElement> entry : items.entrySet()) {
					Identifier itemId = Identifier.parse(entry.getKey());
					JsonArray arr = entry.getValue().getAsJsonArray();
					List<Identifier> qList = new ArrayList<>();
					for (JsonElement el : arr) {
						qList.add(Identifier.parse(el.getAsString()));
					}
					ITEM_TO_QUESTS.put(itemId, Collections.unmodifiableList(qList));
				}
			}

			LOGGER.info("Loaded {} quest titles, {} recipe gates, and {} item gates.",
					QUEST_TITLES.size(), RECIPE_TO_QUEST.size(), ITEM_TO_QUESTS.size());
		} catch (Exception e) {
			LOGGER.error("Failed to load quest gates JSON", e);
		}
	}

	public static String getQuestTitle(Identifier questId) {
		String title = QUEST_TITLES.get(questId);
		if (title != null && !title.isEmpty()) {
			return title;
		}
		String path = questId.getPath();
		int slash = path.lastIndexOf('/');
		return slash >= 0 ? path.substring(slash + 1) : path;
	}

	public static Identifier getQuestForRecipe(Identifier recipeId) {
		return RECIPE_TO_QUEST.get(recipeId);
	}

	public static List<Identifier> getQuestsForItem(Identifier itemId) {
		return ITEM_TO_QUESTS.get(itemId);
	}

	public static Map<Identifier, List<Identifier>> getAllItemGates() {
		return Collections.unmodifiableMap(ITEM_TO_QUESTS);
	}

	public static boolean isQuestCompleted(Identifier questId) {
		Minecraft client = Minecraft.getInstance();
		if (client == null || client.getConnection() == null) {
			return false;
		}
		ClientAdvancements clientAdvancements = client.getConnection().getAdvancements();
		if (clientAdvancements == null) {
			return false;
		}
		AdvancementHolder holder = clientAdvancements.get(questId);
		if (holder == null) {
			return false;
		}
		AdvancementProgress progress = clientAdvancements.progress().get(holder);
		return progress != null && progress.isDone();
	}

	public static boolean isItemUnlocked(Identifier itemId) {
		List<Identifier> quests = ITEM_TO_QUESTS.get(itemId);
		if (quests == null || quests.isEmpty()) {
			return true;
		}
		for (Identifier qId : quests) {
			if (isQuestCompleted(qId)) {
				return true;
			}
		}
		return false;
	}

	public static String getQuestCategory(Identifier questId) {
		if (questId == null) {
			return "";
		}
		String path = questId.getPath();
		int slash = path.indexOf('/');
		String categoryKey = slash >= 0 ? path.substring(0, slash) : path;
		return switch (categoryKey) {
			case "tutorial" -> "Tutorial";
			case "wood" -> "Questline I";
			case "stone" -> "Questline II";
			case "copper" -> "Questline III";
			case "iron" -> "Questline IV";
			case "diamond" -> "Questline V";
			case "end" -> "Questline VI";
			case "secret" -> "Secret";
			default -> "";
		};
	}

	public static String formatQuestWithCategory(Identifier questId) {
		String title = getQuestTitle(questId);
		String category = getQuestCategory(questId);
		if (!category.isEmpty()) {
			return "\"" + title + "\" (" + category + ")";
		}
		return "\"" + title + "\"";
	}

	public static String formatQuestList(List<Identifier> questIds) {
		if (questIds == null || questIds.isEmpty()) {
			return "";
		}
		if (questIds.size() == 1) {
			return formatQuestWithCategory(questIds.get(0));
		}
		if (questIds.size() == 2) {
			return formatQuestWithCategory(questIds.get(0)) + " or " + formatQuestWithCategory(questIds.get(1));
		}
		return formatQuestWithCategory(questIds.get(0)) + " (or alternative quests)";
	}

	public static List<Component> getQuestTooltipLines(List<Identifier> questIds, boolean unlocked) {
		List<Component> lines = new ArrayList<>();
		if (questIds == null || questIds.isEmpty()) {
			lines.add(Component.literal("§7Unlocked by default"));
			return lines;
		}

		String prefixColor = unlocked ? "§7" : "§c";
		String header;
		if (unlocked) {
			header = questIds.size() == 1 ? "Unlocked - completed quest:" : "Unlocked - completed quest path:";
		} else {
			header = questIds.size() == 1 ? "Usage or recipe blocked - finish quest:" : "Usage or recipe blocked - finish either of the quests:";
		}
		lines.add(Component.literal(prefixColor + header));

		for (Identifier qId : questIds) {
			lines.add(Component.literal(prefixColor + "  • " + formatQuestWithCategory(qId)));
		}
		return lines;
	}

	public static List<Component> getRecipeTooltipLines(Identifier questId, boolean unlocked) {
		List<Component> lines = new ArrayList<>();
		if (questId == null) {
			lines.add(Component.literal("§7Unlocked by default"));
			return lines;
		}

		String prefixColor = unlocked ? "§7" : "§c";
		String header = unlocked ? "Unlocked - completed quest:" : "Usage or recipe blocked - finish quest:";
		lines.add(Component.literal(prefixColor + header));
		lines.add(Component.literal(prefixColor + "  • " + formatQuestWithCategory(questId)));
		return lines;
	}
}
