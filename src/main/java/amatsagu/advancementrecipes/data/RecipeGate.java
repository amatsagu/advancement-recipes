package amatsagu.advancementrecipes.data;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.UUID;

/**
 * Mirrors the {@code limitedCrafting} rule, which vanilla only wires up for the crafting table:
 * special recipes always pass, everything else needs the recipe in the acting player's book.
 * Unattended stations fall back to their stored operator and refuse when there is none.
 */
public final class RecipeGate {
	private RecipeGate() {
	}

	public static boolean isAllowed(MinecraftServer server, BlockEntity blockEntity, RecipeHolder<?> recipe) {
		if (allowed(recipe)) {
			return true;
		}

		UUID operator = (blockEntity instanceof OperatorHolder holder) ? holder.advancementRecipes$getOperator() : null;
		return isOperatorAllowed(server, operator, recipe.id());
	}

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
