package net.lightning.trealm;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.lightning.trealm.TravelersRealmDataGenerator.ELEMENTAL_ORE;

public class ModWorldGenerator extends FabricDynamicRegistryProvider {
    public ModWorldGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        entries.addAll(registries.getWrapperOrThrow(RegistryKeys.CONFIGURED_FEATURE));
        entries.addAll(registries.getWrapperOrThrow(RegistryKeys.PLACED_FEATURE));
    }

    @Override
    public String getName() {
        return "World Gen";
    }

    public static class ModPlacedFeatures {
        public static final RegistryKey<PlacedFeature> ELEMENTAL_ORE_PLACED_KEY = registerKey("elemental_ore_placed");

        public static void boostrap(Registerable<PlacedFeature> context) {
            final RegistryEntryLookup<ConfiguredFeature<?, ?>> registryLookup = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);
            final RegistryEntry<ConfiguredFeature<?, ?>> configuration = registryLookup.getOrThrow(ModConfiguredFeatures.ELEMENTAL_ORE_KEY);
            final List<PlacementModifier> modifiers = ModOrePlacement.modifiersWithCount(4, HeightRangePlacementModifier.trapezoid(YOffset.fixed(-80), YOffset.fixed(10)));
            context.register(ELEMENTAL_ORE_PLACED_KEY, new PlacedFeature(configuration, List.copyOf(modifiers)));
        }

        public static RegistryKey<PlacedFeature> registerKey(String name) {
            return RegistryKey.of(RegistryKeys.PLACED_FEATURE, new Identifier(TravelersRealm.MOD_NAMESPACE, name));
        }
    }

    public static class ModConfiguredFeatures {
        public static final RegistryKey<ConfiguredFeature<?, ?>> ELEMENTAL_ORE_KEY = registerKey("elemental_ore");

        public static void boostrap(Registerable<ConfiguredFeature<?, ?>> context) {
            final RuleTest deepslateReplaceable = new TagMatchRuleTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
            final List<OreFeatureConfig.Target> overworldElementalOres = List.of(OreFeatureConfig.createTarget(deepslateReplaceable, ELEMENTAL_ORE.getDefaultState()));
            context.register(ELEMENTAL_ORE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreFeatureConfig(overworldElementalOres, 6)));
        }

        public static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String name) {
            return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, new Identifier(TravelersRealm.MOD_NAMESPACE, name));
        }
    }

    public static class ModOrePlacement {
        public static List<PlacementModifier> modifiers(PlacementModifier countModifier, PlacementModifier heightModifier) {
            return List.of(countModifier, SquarePlacementModifier.of(), heightModifier, BiomePlacementModifier.of());
        }

        public static List<PlacementModifier> modifiersWithCount(int count, PlacementModifier heightModifier) {
            return modifiers(CountPlacementModifier.of(count), heightModifier);
        }

        public static List<PlacementModifier> modifiersWithRarity(int chance, PlacementModifier heightModifier) {
            return modifiers(RarityFilterPlacementModifier.of(chance), heightModifier);
        }
    }
}
