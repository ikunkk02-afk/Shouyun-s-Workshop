package com.shouyun.workshop.datagen;

import com.shouyun.workshop.ShouyunWorkshop;
import com.shouyun.workshop.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public final class ModRecipeProvider extends FabricRecipeProvider {
	public ModRecipeProvider(FabricDataOutput output,
			CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	public void generate(RecipeExporter exporter) {
		for (var tier : com.shouyun.workshop.item.NecromancerTier.values()) {
			var result = ModItems.NECROMANCER_STAVES.get(tier);
			if (tier == com.shouyun.workshop.item.NecromancerTier.NETHERITE) {
				net.minecraft.data.server.recipe.SmithingTransformRecipeJsonBuilder.create(
						net.minecraft.recipe.Ingredient.ofItems(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
						net.minecraft.recipe.Ingredient.ofItems(ModItems.NECROMANCER_STAVES.get(com.shouyun.workshop.item.NecromancerTier.DIAMOND)),
						net.minecraft.recipe.Ingredient.ofItems(Items.NETHERITE_INGOT), RecipeCategory.COMBAT, result)
						.criterion(hasItem(Items.NETHERITE_INGOT), conditionsFromItem(Items.NETHERITE_INGOT))
						.offerTo(exporter, ShouyunWorkshop.id(tier.id()).toString());
				continue;
			}
			var recipe = ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, result)
					.pattern("MBM").pattern(" R ").pattern(" S ")
					.input('B', Items.BONE).input('R', Items.ROTTEN_FLESH).input('S', Items.STICK);
			if (tier == com.shouyun.workshop.item.NecromancerTier.WOODEN) recipe.input('M', net.minecraft.registry.tag.ItemTags.PLANKS);
			else recipe.input('M', switch (tier) {
				case STONE -> Items.COBBLESTONE;
				case IRON -> Items.IRON_INGOT;
				case GOLDEN -> Items.GOLD_INGOT;
				default -> Items.DIAMOND;
			});
			recipe.criterion(hasItem(Items.ROTTEN_FLESH), conditionsFromItem(Items.ROTTEN_FLESH)).offerTo(exporter, ShouyunWorkshop.id(tier.id()));
		}
		ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.GLASS_HAMMER)
				.pattern("GGG")
				.pattern("GGG")
				.pattern(" S ")
				.input('G', Items.GLASS)
				.input('S', Items.STICK)
				.criterion(hasItem(Items.GLASS), conditionsFromItem(Items.GLASS))
				.offerTo(exporter, ShouyunWorkshop.id("glass_hammer"));

		ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.NETHERITE_HAMMER)
				.pattern("NNN")
				.pattern(" O ")
				.pattern(" B ")
				.input('N', Items.NETHERITE_INGOT)
				.input('O', Items.OBSIDIAN)
				.input('B', Items.BLAZE_ROD)
				.criterion(hasItem(Items.NETHERITE_INGOT), conditionsFromItem(Items.NETHERITE_INGOT))
				.offerTo(exporter, ShouyunWorkshop.id("netherite_hammer"));
	}
}
