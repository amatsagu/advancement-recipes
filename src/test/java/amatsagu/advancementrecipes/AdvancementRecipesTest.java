package amatsagu.advancementrecipes;

import amatsagu.advancementrecipes.data.QuestData;
import net.minecraft.resources.Identifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

public class AdvancementRecipesTest {
	@BeforeAll
	public static void setup() {
		QuestData.init();
	}

	@Test
	public void testQuestDataLoaded() {
		Assertions.assertFalse(QuestData.getAllItemGates().isEmpty(), "Item gates should not be empty");

		// Test key items
		Identifier anvil = Identifier.parse("minecraft:anvil");
		List<Identifier> anvilQuests = QuestData.getQuestsForItem(anvil);
		Assertions.assertNotNull(anvilQuests, "Anvil should be gated");
		Assertions.assertTrue(anvilQuests.contains(Identifier.parse("rework:copper/get_iron")), "Anvil should require Heavy Metal");

		Identifier enchantingTable = Identifier.parse("minecraft:enchanting_table");
		List<Identifier> enchantQuests = QuestData.getQuestsForItem(enchantingTable);
		Assertions.assertNotNull(enchantQuests, "Enchanting table should be gated");
		Assertions.assertTrue(enchantQuests.contains(Identifier.parse("rework:iron/get_diamond")), "Enchanting table should require Diamonds!");

		Identifier crafter = Identifier.parse("minecraft:crafter");
		List<Identifier> crafterQuests = QuestData.getQuestsForItem(crafter);
		Assertions.assertNotNull(crafterQuests, "Crafter should be gated");
		Assertions.assertTrue(crafterQuests.contains(Identifier.parse("rework:iron/get_redstone")), "Crafter should require Unlimited Power");

		Identifier bed = Identifier.parse("minecraft:white_bed");
		List<Identifier> bedQuests = QuestData.getQuestsForItem(bed);
		Assertions.assertNotNull(bedQuests, "Bed should be gated");
		Assertions.assertTrue(bedQuests.contains(Identifier.parse("rework:wood/get_wool")), "Bed should require Power Of Free Wool");

		// Test nether_brick_fence is gated by fortress
		Identifier netherBrickFence = Identifier.parse("minecraft:nether_brick_fence");
		List<Identifier> fenceQuests = QuestData.getQuestsForItem(netherBrickFence);
		Assertions.assertNotNull(fenceQuests, "Nether brick fence should be gated");
		Assertions.assertTrue(fenceQuests.contains(Identifier.parse("rework:iron/fortress")), "Nether brick fence should require A Terrible Fortress");

		// Test unlocked by default items (e.g. dirt, stick)
		Identifier dirt = Identifier.parse("minecraft:dirt");
		Assertions.assertNull(QuestData.getQuestsForItem(dirt), "Dirt should be unlocked by default");

		// Test category mapping and formatting
		Assertions.assertEquals("Questline III", QuestData.getQuestCategory(Identifier.parse("rework:copper/get_iron")));
		Assertions.assertEquals("Questline IV", QuestData.getQuestCategory(Identifier.parse("rework:iron/get_diamond")));
		Assertions.assertEquals("Questline I", QuestData.getQuestCategory(Identifier.parse("rework:wood/root")));
		Assertions.assertEquals("Tutorial", QuestData.getQuestCategory(Identifier.parse("rework:tutorial/dirt")));

		String formattedAnvil = QuestData.formatQuestList(anvilQuests);
		Assertions.assertTrue(formattedAnvil.contains("(Questline III)"), "Formatted anvil quest should include category: " + formattedAnvil);
	}
}
