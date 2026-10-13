package amatsagu.advancementrecipes.data;

import java.util.UUID;

/**
 * Duck interface mixed into every block entity: the player who placed or last opened a station.
 * Unattended machines are gated by that player's unlocked recipes.
 */
public interface OperatorHolder {
	UUID advancementRecipes$getOperator();

	void advancementRecipes$setOperator(UUID operator);
}
