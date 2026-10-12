package amatsagu.advancementrecipes.mixin;

import amatsagu.advancementrecipes.data.OperatorHolder;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

/**
 * Gives every block entity a persistent "operator" (the player who placed or last opened it) so the
 * unattended stations can be gated by that player's unlocked recipes.
 */
@Mixin(BlockEntity.class)
public abstract class BlockEntityOperatorMixin implements OperatorHolder {
	@Unique
	private static final String ADVANCEMENT_RECIPES_OPERATOR = "AdvancementRecipesOperator";

	@Unique
	private UUID advancementRecipes$operator;

	@Override
	public UUID advancementRecipes$getOperator() {
		return advancementRecipes$operator;
	}

	@Override
	public void advancementRecipes$setOperator(UUID operator) {
		advancementRecipes$operator = operator;
		((BlockEntity) (Object) this).setChanged();
	}

	@Inject(method = "saveAdditional", at = @At("HEAD"))
	private void advancementrecipes$saveOperator(ValueOutput output, CallbackInfo ci) {
		if (advancementRecipes$operator != null) {
			output.store(ADVANCEMENT_RECIPES_OPERATOR, Codec.STRING, advancementRecipes$operator.toString());
		}
	}

	@Inject(method = "loadAdditional", at = @At("HEAD"))
	private void advancementrecipes$loadOperator(ValueInput input, CallbackInfo ci) {
		advancementRecipes$operator = input.read(ADVANCEMENT_RECIPES_OPERATOR, Codec.STRING)
				.map(UUID::fromString)
				.orElse(null);
	}
}
