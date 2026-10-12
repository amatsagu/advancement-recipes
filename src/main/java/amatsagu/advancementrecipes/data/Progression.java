package amatsagu.advancementrecipes.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Server-side progression engine.
 *
 * <p>Replaces the datapack polling that used to live in the mod:
 * <ul>
 *     <li>{@code minecraft:tick} triggered questline roots,</li>
 *     <li>{@code minecraft:location} criteria on every "complete_category" advancement,</li>
 *     <li>the {@code update_*}/{@code sync_advancement_recipes} mcfunction migrations.</li>
 * </ul>
 *
 * <p>Instead, questline roots are granted the moment the previous category completes, categories
 * complete the moment their last goal is earned (driven from {@code PlayerAdvancementsMixin}), and
 * earned recipes are (re)synced directly through {@code ServerPlayer#awardRecipesByKey}. Everything
 * is event-driven: no per-tick or per-second advancement evaluation is left.
 */
public final class Progression {
	private static final Logger LOGGER = LoggerFactory.getLogger("AdvancementRecipes/Progression");

	private record Category(Identifier category, Identifier root, List<Identifier> goals) {
	}

	private static final List<Category> CATEGORIES = new ArrayList<>();
	private static final Set<Identifier> GOALS = new HashSet<>();
	private static boolean initialized = false;

	private Progression() {
	}

	public static synchronized void init() {
		if (initialized) {
			return;
		}
		initialized = true;

		try (InputStream stream = Progression.class.getResourceAsStream("/assets/advancement_recipes/quest_progression.json")) {
			if (stream == null) {
				LOGGER.error("Could not find /assets/advancement_recipes/quest_progression.json resource!");
				return;
			}

			JsonObject root = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
			JsonArray categories = root.getAsJsonArray("categories");
			for (JsonElement element : categories) {
				JsonObject object = element.getAsJsonObject();
				Identifier category = Identifier.parse(object.get("category").getAsString());
				Identifier rootId = Identifier.parse(object.get("root").getAsString());
				List<Identifier> goals = new ArrayList<>();
				for (JsonElement goal : object.getAsJsonArray("goals")) {
					goals.add(Identifier.parse(goal.getAsString()));
				}
				CATEGORIES.add(new Category(category, rootId, List.copyOf(goals)));
				GOALS.addAll(goals);
			}

			LOGGER.info("Loaded {} quest categories and {} tracked goals.", CATEGORIES.size(), GOALS.size());
		} catch (Exception e) {
			LOGGER.error("Failed to load quest progression JSON", e);
		}
	}

	/** True if the given advancement is a non-category quest goal (i.e. completing it can finish a category). */
	public static boolean isGoal(Identifier id) {
		return GOALS.contains(id);
	}

	/**
	 * Brings a player's questline roots and completed categories in line with their goal progress.
	 * Idempotent: safe to call on every join and after every goal completion.
	 */
	public static void refresh(ServerPlayer player) {
		MinecraftServer server = player.level().getServer();
		if (server == null) {
			return;
		}
		ServerAdvancementManager manager = server.getAdvancements();
		PlayerAdvancements advancements = player.getAdvancements();

		Identifier previousCategory = null;
		for (Category category : CATEGORIES) {
			// A questline root unlocks as soon as the previous category is finished (the first is always open).
			if (previousCategory == null || isDone(advancements, manager, previousCategory)) {
				grant(advancements, manager, category.root());
			}

			// The category completes as soon as every one of its goals is earned.
			if (allDone(advancements, manager, category.goals())) {
				grant(advancements, manager, category.category());
			}

			previousCategory = category.category();
		}
	}

	private static boolean isDone(PlayerAdvancements advancements, ServerAdvancementManager manager, Identifier id) {
		AdvancementHolder holder = manager.get(id);
		return holder != null && advancements.getOrStartProgress(holder).isDone();
	}

	private static boolean allDone(PlayerAdvancements advancements, ServerAdvancementManager manager, List<Identifier> goals) {
		for (Identifier goal : goals) {
			if (!isDone(advancements, manager, goal)) {
				return false;
			}
		}
		return true;
	}

	private static void grant(PlayerAdvancements advancements, ServerAdvancementManager manager, Identifier id) {
		AdvancementHolder holder = manager.get(id);
		if (holder == null || advancements.getOrStartProgress(holder).isDone()) {
			return;
		}
		// Criteria for roots/categories are "impossible"; awarding the criterion completes it and runs
		// its rewards (questline recipes, config message, ...) exactly once.
		advancements.award(holder, "default");
	}
}
