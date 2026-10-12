package amatsagu.advancementrecipes.data;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.UUID;

/**
 * Central gate used to hard-block non-crafting recipes (smelting, stonecutting, smithing, crafting
 * by the Crafter, ...) exactly the way {@code limitedCrafting} blocks the crafting table.
 *
 * <p>The vanilla enforcing check lives in {@code RecipeCraftingHolder#setRecipeUsed(ServerPlayer, ...)}
 * and is only wired up by {@code CraftingMenu}. We replicate it:
 * special recipes are always allowed, everything else requires the acting player's recipe book to
 * contain the recipe. Unattended machines use the stored operator; if there is no operator (or the
 * operator is offline) gated recipes are refused.
 */
public final class RecipeGate {
	private RecipeGate() {
	}

	/** Is this recipe allowed for the block entity's operator? */
	public static boolean isAllowed(MinecraftServer server, BlockEntity blockEntity, RecipeHolder<?> recipe) {
		if (allowed(recipe)) {
			return true;
		}
		UUID operator = (blockEntity instanceof OperatorHolder holder) ? holder.advancementRecipes$getOperator() : null;
		return isOperatorAllowed(server, operator, recipe.id());
	}

	/** Is this recipe allowed for a player who is directly using the station? */
	public static boolean isAllowed(ServerPlayer player, RecipeHolder<?> recipe) {
		if (allowed(recipe)) {
			return true;
		}
		return player != null && player.getRecipeBook().contains(recipe.id());
	}

	private static boolean allowed(RecipeHolder<?> recipe) {
		return recipe == null || recipe.value().isSpecial();
	}

	private static boolean isOperatorAllowed(MinecraftServer server, UUID operator, ResourceKey<Recipe<?>> recipe) {
		if (server == null || operator == null) {
			return false;
		}
		ServerPlayer player = server.getPlayerList().getPlayer(operator);
		return player != null && player.getRecipeBook().contains(recipe);
	}
}
