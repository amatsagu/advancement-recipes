package amatsagu.advancementrecipes.data;

import java.util.UUID;

/**
 * Duck interface injected onto {@link net.minecraft.world.level.block.entity.BlockEntity} so any
 * station block entity can remember the player ("operator") who last placed/used it. That operator's
 * unlocked recipes decide what unattended machines (furnaces, crafters, campfires, ...) may process.
 */
public interface OperatorHolder {
	UUID advancementRecipes$getOperator();

	void advancementRecipes$setOperator(UUID operator);
}
