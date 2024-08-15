package net.lightning.trealm;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.OptionalLong;
import java.util.Set;

public class ModDimensions {
    public static final RegistryKey<DimensionOptions> ABYSSDIM_KEY = RegistryKey.of(RegistryKeys.DIMENSION,
            new Identifier(TravelersRealm.MOD_NAMESPACE, "abyss"));
    public static final RegistryKey<World> ABYSSDIM_LEVEL_KEY = RegistryKey.of(RegistryKeys.WORLD,
            new Identifier(TravelersRealm.MOD_NAMESPACE, "abyss"));
    public static final RegistryKey<DimensionType> ABYSS_DIM_TYPE = RegistryKey.of(RegistryKeys.DIMENSION_TYPE,
            new Identifier(TravelersRealm.MOD_NAMESPACE, "abyss_type"));

    public static void bootstrapType(Registerable<DimensionType> context) {
        context.register(ABYSS_DIM_TYPE, new DimensionType(
                OptionalLong.of(12000),
                false, //hasSkylight
                false, //hasCeiling
                false, //ultraWarm
                true, //natural
                1.0, //coordinateScale
                false, //bedworks
                false, //respawnAnchorWorks
                0, //minY
                256, //height
                256, //logicalHeight
                BlockTags.INFINIBURN_OVERWORLD, //infiniburn
                DimensionTypes.THE_END_ID, // effectsLocation
                1.0f, //ambientLight
                new DimensionType.MonsterSettings(false, false, UniformIntProvider.create(0, 0), 0)));


    }
}


