package amatsagu.advancementrecipes.mixin;

import amatsagu.advancementrecipes.data.Progression;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Hooks advancement completion so questline categories complete the instant their last goal is
 * earned, replacing the old datapack {@code minecraft:location} polling on every
 * "complete_category" advancement.
 */
@Mixin(PlayerAdvancements.class)
public abstract class PlayerAdvancementsMixin {
	@Shadow
	private ServerPlayer player;

	@Inject(method = "award", at = @At("TAIL"))
	private void advancementrecipes$refreshProgression(AdvancementHolder holder, String criterion, CallbackInfoReturnable<Boolean> cir) {
		if (player == null || !Progression.isGoal(holder.id())) {
			return;
		}
		Progression.refresh(player);
	}
}
