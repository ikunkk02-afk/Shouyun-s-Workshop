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
