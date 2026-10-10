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
		net.minecraft.SharedConstants.tryDetectVersion();
		net.minecraft.server.Bootstrap.bootStrap();
		for (net.minecraft.world.item.Item item : net.minecraft.core.registries.BuiltInRegistries.ITEM) {
			item.builtInRegistryHolder().bindComponents(net.minecraft.core.component.DataComponentMap.EMPTY);
		}
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

		// Test backpacks
		Identifier baseBackpack = Identifier.parse("nemos_backpacks:backpack");
		List<Identifier> baseQuests = QuestData.getQuestsForItem(baseBackpack);
		Assertions.assertNotNull(baseQuests, "Base backpack should be gated");
		Assertions.assertTrue(baseQuests.contains(Identifier.parse("rework:stone/craft_bundle")), "Base backpack should require Pack It Up!");

		Identifier blackBackpack = Identifier.parse("nemos_backpacks:black_backpack");
		List<Identifier> blackQuests = QuestData.getQuestsForItem(blackBackpack);
		Assertions.assertNotNull(blackQuests, "Dyed backpack should be gated");
		Assertions.assertTrue(blackQuests.contains(Identifier.parse("rework:stone/backpack")), "Dyed backpack should require On Your Back");

		Identifier copperBackpack = Identifier.parse("nemos_backpacks:copper_backpack");
		List<Identifier> copperQuests = QuestData.getQuestsForItem(copperBackpack);
		Assertions.assertNotNull(copperQuests, "Copper backpack should be gated");
		Assertions.assertTrue(copperQuests.contains(Identifier.parse("rework:stone/complete_category")), "Copper backpack should require Copper Completionist");

		Identifier ironBackpack = Identifier.parse("nemos_backpacks:iron_backpack");
		List<Identifier> ironQuests = QuestData.getQuestsForItem(ironBackpack);
		Assertions.assertNotNull(ironQuests, "Iron backpack should be gated");
		Assertions.assertTrue(ironQuests.contains(Identifier.parse("rework:copper/complete_category")), "Iron backpack should require Iron Completionist");

		Identifier goldenBackpack = Identifier.parse("nemos_backpacks:golden_backpack");
		List<Identifier> goldenQuests = QuestData.getQuestsForItem(goldenBackpack);
		Assertions.assertNotNull(goldenQuests, "Golden backpack should be gated");
		Assertions.assertTrue(goldenQuests.contains(Identifier.parse("rework:iron/nether")), "Golden backpack should require Nether");

		Identifier diamondBackpack = Identifier.parse("nemos_backpacks:diamond_backpack");
		List<Identifier> diamondQuests = QuestData.getQuestsForItem(diamondBackpack);
		Assertions.assertNotNull(diamondQuests, "Diamond backpack should be gated");
		Assertions.assertTrue(diamondQuests.contains(Identifier.parse("rework:iron/complete_category")), "Diamond backpack should require Diamond Completionist");

		Identifier netheriteBackpack = Identifier.parse("nemos_backpacks:netherite_backpack");
		List<Identifier> netheriteQuests = QuestData.getQuestsForItem(netheriteBackpack);
		Assertions.assertNotNull(netheriteQuests, "Netherite backpack should be gated");
		Assertions.assertTrue(netheriteQuests.contains(Identifier.parse("rework:diamond/complete_category")), "Netherite backpack should require Netherite Completionist");

		// Test Farmer's Delight items
		Identifier cookingPot = Identifier.parse("farmersdelight:cooking_pot");
		List<Identifier> cookingPotQuests = QuestData.getQuestsForItem(cookingPot);
		Assertions.assertNotNull(cookingPotQuests, "Cooking pot should be gated");
		Assertions.assertTrue(cookingPotQuests.contains(Identifier.parse("rework:copper/get_iron")), "Cooking pot should require Heavy Metal");

		Identifier skillet = Identifier.parse("farmersdelight:skillet");
		List<Identifier> skilletQuests = QuestData.getQuestsForItem(skillet);
		Assertions.assertNotNull(skilletQuests, "Skillet should be gated");
		Assertions.assertTrue(skilletQuests.contains(Identifier.parse("rework:copper/get_iron")), "Skillet should require Heavy Metal");

		Identifier cuttingBoard = Identifier.parse("farmersdelight:cutting_board");
		List<Identifier> cuttingBoardQuests = QuestData.getQuestsForItem(cuttingBoard);
		Assertions.assertNotNull(cuttingBoardQuests, "Cutting board should be gated");
		Assertions.assertTrue(cuttingBoardQuests.contains(Identifier.parse("rework:wood/seeds")), "Cutting board should require Farming Roots");

		Identifier ironKnife = Identifier.parse("farmersdelight:iron_knife");
		List<Identifier> ironKnifeQuests = QuestData.getQuestsForItem(ironKnife);
		Assertions.assertNotNull(ironKnifeQuests, "Iron knife should be gated");
		Assertions.assertTrue(ironKnifeQuests.contains(Identifier.parse("rework:copper/complete_category")), "Iron knife should require Iron Completionist");

		Identifier diamondKnife = Identifier.parse("farmersdelight:diamond_knife");
		List<Identifier> diamondKnifeQuests = QuestData.getQuestsForItem(diamondKnife);
		Assertions.assertNotNull(diamondKnifeQuests, "Diamond knife should be gated");
		Assertions.assertTrue(diamondKnifeQuests.contains(Identifier.parse("rework:iron/complete_category")), "Diamond knife should require Diamond Completionist");

		Identifier applePie = Identifier.parse("farmersdelight:apple_pie");
		List<Identifier> applePieQuests = QuestData.getQuestsForItem(applePie);
		Assertions.assertNotNull(applePieQuests, "Apple pie should be gated");
		Assertions.assertTrue(applePieQuests.contains(Identifier.parse("rework:wood/bread")), "Apple pie should require Baker's Delight");

		// Test cooking quest progression: Stone, Copper, Iron
		Identifier hamburger = Identifier.parse("farmersdelight:hamburger");
		List<Identifier> hamburgerQuests = QuestData.getQuestsForItem(hamburger);
		Assertions.assertNotNull(hamburgerQuests, "Hamburger should be gated");
		Assertions.assertTrue(hamburgerQuests.contains(Identifier.parse("rework:stone/hearty_meal")), "Hamburger should require Hearty Lunch");
		Assertions.assertEquals("Hearty Lunch", QuestData.getQuestTitle(Identifier.parse("rework:stone/hearty_meal")));
		Assertions.assertEquals("Questline II", QuestData.getQuestCategory(Identifier.parse("rework:stone/hearty_meal")));

		Identifier steakAndPotatoes = Identifier.parse("farmersdelight:steak_and_potatoes");
		List<Identifier> steakQuests = QuestData.getQuestsForItem(steakAndPotatoes);
		Assertions.assertNotNull(steakQuests, "Steak and potatoes should be gated");
		Assertions.assertTrue(steakQuests.contains(Identifier.parse("rework:copper/pot_cooking")), "Steak and potatoes should require Home Cooking");
		Assertions.assertEquals("Home Cooking", QuestData.getQuestTitle(Identifier.parse("rework:copper/pot_cooking")));
		Assertions.assertEquals("Questline III", QuestData.getQuestCategory(Identifier.parse("rework:copper/pot_cooking")));

		Identifier honeyGlazedHam = Identifier.parse("farmersdelight:honey_glazed_ham_block");
		List<Identifier> feastQuests = QuestData.getQuestsForItem(honeyGlazedHam);
		Assertions.assertNotNull(feastQuests, "Honey glazed ham should be gated");
		Assertions.assertTrue(feastQuests.contains(Identifier.parse("rework:iron/grand_feast")), "Honey glazed ham should require Grand Feast");
		Assertions.assertEquals("Grand Feast", QuestData.getQuestTitle(Identifier.parse("rework:iron/grand_feast")));
		Assertions.assertEquals("Questline IV", QuestData.getQuestCategory(Identifier.parse("rework:iron/grand_feast")));

		// Test Simple Magnets
		Identifier basicMagnet = Identifier.parse("simplemagnets:basicmagnet");
		List<Identifier> basicMagnetQuests = QuestData.getQuestsForItem(basicMagnet);
		Assertions.assertNotNull(basicMagnetQuests, "Basic magnet should be gated");
		Assertions.assertTrue(basicMagnetQuests.contains(Identifier.parse("rework:copper/complete_category")), "Basic magnet should be unlocked by Iron Completionist");

		Identifier basicCoil = Identifier.parse("simplemagnets:basic_demagnetization_coil");
		List<Identifier> basicCoilQuests = QuestData.getQuestsForItem(basicCoil);
		Assertions.assertNotNull(basicCoilQuests, "Basic demagnetization coil should be gated");
		Assertions.assertTrue(basicCoilQuests.contains(Identifier.parse("rework:iron/basic_magnet")), "Basic coil should require Attraction");
		Assertions.assertEquals("Attraction", QuestData.getQuestTitle(Identifier.parse("rework:iron/basic_magnet")));
		Assertions.assertEquals("Questline IV", QuestData.getQuestCategory(Identifier.parse("rework:iron/basic_magnet")));

		Identifier advancedMagnet = Identifier.parse("simplemagnets:advancedmagnet");
		List<Identifier> advMagnetQuests = QuestData.getQuestsForItem(advancedMagnet);
		Assertions.assertNotNull(advMagnetQuests, "Advanced magnet should be gated");
		Assertions.assertTrue(advMagnetQuests.contains(Identifier.parse("rework:diamond/complete_category")), "Advanced magnet should require Netherite Completionist");

		Identifier advancedCoil = Identifier.parse("simplemagnets:advanced_demagnetization_coil");
		List<Identifier> advCoilQuests = QuestData.getQuestsForItem(advancedCoil);
		Assertions.assertNotNull(advCoilQuests, "Advanced demagnetization coil should be gated");
		Assertions.assertTrue(advCoilQuests.contains(Identifier.parse("rework:end/advanced_magnet")), "Advanced coil should require Polar Reversal");
		Assertions.assertEquals("Polar Reversal", QuestData.getQuestTitle(Identifier.parse("rework:end/advanced_magnet")));
		Assertions.assertEquals("Questline VI", QuestData.getQuestCategory(Identifier.parse("rework:end/advanced_magnet")));

		// Test multiline tooltip lines for single and multi-quest items
		List<net.minecraft.network.chat.Component> singleQuestLines = QuestData.getQuestTooltipLines(anvilQuests, false);
		Assertions.assertEquals(2, singleQuestLines.size(), "Single quest should have 2 lines (state + quest)");
		Assertions.assertTrue(singleQuestLines.get(0).getString().contains("Usage or recipe blocked - finish quest:"));
		Assertions.assertTrue(singleQuestLines.get(1).getString().contains("•"));
		Assertions.assertTrue(singleQuestLines.get(1).getString().contains("Heavy Metal"));

		Identifier leatherHorseArmor = Identifier.parse("minecraft:leather_horse_armor");
		List<Identifier> multiQuests = QuestData.getQuestsForItem(leatherHorseArmor);
		Assertions.assertNotNull(multiQuests);
		Assertions.assertTrue(multiQuests.size() >= 2);
		List<net.minecraft.network.chat.Component> multiQuestLines = QuestData.getQuestTooltipLines(multiQuests, false);
		Assertions.assertEquals(1 + multiQuests.size(), multiQuestLines.size(), "Multiple quests should have header + 1 line per quest");
		Assertions.assertTrue(multiQuestLines.get(0).getString().contains("Usage or recipe blocked - finish either of the quests:"));
		Assertions.assertTrue(multiQuestLines.get(1).getString().contains("•"));
		Assertions.assertTrue(multiQuestLines.get(2).getString().contains("•"));

		// Test Glow Paste
		Identifier glowPaste = Identifier.parse("glowpaste:glow_paste");
		List<Identifier> glowPasteQuests = QuestData.getQuestsForItem(glowPaste);
		Assertions.assertNotNull(glowPasteQuests, "Glow paste should be gated");
		Assertions.assertTrue(glowPasteQuests.contains(Identifier.parse("rework:iron/swamp")), "Glow paste should require Swampland explorer");
		Assertions.assertEquals(Identifier.parse("rework:iron/swamp"), QuestData.getQuestForRecipe(glowPaste));

		// Test Slime Ball from Honey Recipe
		Identifier slimeFromHoney = Identifier.parse("advancement_recipes:slime_ball_from_honey");
		Assertions.assertEquals(Identifier.parse("rework:iron/swamp"), QuestData.getQuestForRecipe(slimeFromHoney));

		// Test Furnace gating (unlocked at wood/stone "Stone Age")
		Identifier furnaceId = Identifier.parse("minecraft:furnace");
		Assertions.assertEquals(Identifier.parse("rework:wood/stone"), QuestData.getQuestForRecipe(furnaceId));
		Assertions.assertTrue(QuestData.getQuestsForItem(furnaceId).contains(Identifier.parse("rework:wood/stone")));

		// Test Chest and Barrel gating (unlocked at wood/item_frame "A Frame On View")
		Identifier chestId = Identifier.parse("minecraft:chest");
		Assertions.assertEquals(Identifier.parse("rework:wood/item_frame"), QuestData.getQuestForRecipe(chestId));
		Assertions.assertTrue(QuestData.getQuestsForItem(chestId).contains(Identifier.parse("rework:wood/item_frame")));

		Identifier barrelId = Identifier.parse("minecraft:barrel");
		Assertions.assertEquals(Identifier.parse("rework:wood/item_frame"), QuestData.getQuestForRecipe(barrelId));
		Assertions.assertTrue(QuestData.getQuestsForItem(barrelId).contains(Identifier.parse("rework:wood/item_frame")));

		// Test Copper Chest gating (unlocked at stone/copper_ingot "Top Tier Copper")
		Identifier copperChestId = Identifier.parse("minecraft:copper_chest");
		Assertions.assertEquals(Identifier.parse("rework:stone/copper_ingot"), QuestData.getQuestForRecipe(copperChestId));
		Assertions.assertTrue(QuestData.getQuestsForItem(copperChestId).contains(Identifier.parse("rework:stone/copper_ingot")));

		// Test Storage Drawers gating
		Identifier oakDrawer = Identifier.parse("storagedrawers:oak_full_drawers_1");
		Assertions.assertEquals(Identifier.parse("rework:wood/item_frame"), QuestData.getQuestForRecipe(oakDrawer));
		Assertions.assertTrue(QuestData.getQuestsForItem(oakDrawer).contains(Identifier.parse("rework:wood/item_frame")));

		Identifier upgradeTemplate = Identifier.parse("storagedrawers:upgrade_template");
		Assertions.assertEquals(Identifier.parse("rework:wood/item_frame"), QuestData.getQuestForRecipe(upgradeTemplate));
		Assertions.assertTrue(QuestData.getQuestsForItem(upgradeTemplate).contains(Identifier.parse("rework:wood/item_frame")));

		Identifier copperUpgrade = Identifier.parse("storagedrawers:copper_storage_upgrade");
		Assertions.assertEquals(Identifier.parse("rework:stone/copper_ingot"), QuestData.getQuestForRecipe(copperUpgrade));
		Assertions.assertTrue(QuestData.getQuestsForItem(copperUpgrade).contains(Identifier.parse("rework:stone/copper_ingot")));

		Identifier ironUpgrade = Identifier.parse("storagedrawers:iron_storage_upgrade");
		Assertions.assertEquals(Identifier.parse("rework:copper/get_iron"), QuestData.getQuestForRecipe(ironUpgrade));
		Assertions.assertTrue(QuestData.getQuestsForItem(ironUpgrade).contains(Identifier.parse("rework:copper/get_iron")));

		Identifier emeraldUpgrade = Identifier.parse("storagedrawers:emerald_storage_upgrade");
		Assertions.assertEquals(Identifier.parse("rework:copper/complete_category"), QuestData.getQuestForRecipe(emeraldUpgrade));
		Assertions.assertTrue(QuestData.getQuestsForItem(emeraldUpgrade).contains(Identifier.parse("rework:copper/complete_category")));

		Identifier crimsonDrawer = Identifier.parse("storagedrawers:crimson_full_drawers_1");
		Assertions.assertEquals(Identifier.parse("rework:iron/nether"), QuestData.getQuestForRecipe(crimsonDrawer));
		Assertions.assertTrue(QuestData.getQuestsForItem(crimsonDrawer).contains(Identifier.parse("rework:iron/nether")));

		Identifier drawerKey = Identifier.parse("storagedrawers:drawer_key");
		Assertions.assertEquals(Identifier.parse("rework:iron/nether"), QuestData.getQuestForRecipe(drawerKey));
		Assertions.assertTrue(QuestData.getQuestsForItem(drawerKey).contains(Identifier.parse("rework:iron/nether")));

		Identifier redstoneUpgrade = Identifier.parse("storagedrawers:redstone_upgrade");
		Assertions.assertEquals(Identifier.parse("rework:iron/get_redstone"), QuestData.getQuestForRecipe(redstoneUpgrade));
		Assertions.assertTrue(QuestData.getQuestsForItem(redstoneUpgrade).contains(Identifier.parse("rework:iron/get_redstone")));

		Identifier compDrawer = Identifier.parse("storagedrawers:compacting_drawers_2");
		Assertions.assertEquals(Identifier.parse("rework:iron/get_redstone"), QuestData.getQuestForRecipe(compDrawer));
		Assertions.assertTrue(QuestData.getQuestsForItem(compDrawer).contains(Identifier.parse("rework:iron/get_redstone")));

		Identifier quantifyKey = Identifier.parse("storagedrawers:quantify_key");
		Assertions.assertEquals(Identifier.parse("rework:iron/write_book"), QuestData.getQuestForRecipe(quantifyKey));
		Assertions.assertTrue(QuestData.getQuestsForItem(quantifyKey).contains(Identifier.parse("rework:iron/write_book")));

		Identifier remoteUpgrade = Identifier.parse("storagedrawers:remote_upgrade");
		Assertions.assertEquals(Identifier.parse("rework:iron/look_at_enderman"), QuestData.getQuestForRecipe(remoteUpgrade));
		Assertions.assertTrue(QuestData.getQuestsForItem(remoteUpgrade).contains(Identifier.parse("rework:iron/look_at_enderman")));

		Identifier controller = Identifier.parse("storagedrawers:controller");
		Assertions.assertEquals(Identifier.parse("rework:iron/get_diamond"), QuestData.getQuestForRecipe(controller));
		Assertions.assertTrue(QuestData.getQuestsForItem(controller).contains(Identifier.parse("rework:iron/get_diamond")));

		Identifier netheriteUpgrade = Identifier.parse("storagedrawers:netherite_storage_upgrade");
		Assertions.assertEquals(Identifier.parse("rework:diamond/complete_category"), QuestData.getQuestForRecipe(netheriteUpgrade));
		Assertions.assertTrue(QuestData.getQuestsForItem(netheriteUpgrade).contains(Identifier.parse("rework:diamond/complete_category")));

		Identifier portabilityUpgrade = Identifier.parse("storagedrawers:portability_upgrade");
		Assertions.assertEquals(Identifier.parse("rework:end/end_city"), QuestData.getQuestForRecipe(portabilityUpgrade));
		Assertions.assertTrue(QuestData.getQuestsForItem(portabilityUpgrade).contains(Identifier.parse("rework:end/end_city")));
	}
}
