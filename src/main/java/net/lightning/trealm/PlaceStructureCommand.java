package net.lightning.trealm;

import com.mojang.brigadier.CommandDispatcher;
import net.lightning.trealm.AbyssLevel.Tile;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import static net.lightning.trealm.TravelersRealm.MOD_NAMESPACE;


public class PlaceStructureCommand {

    public static final int ROOM_SIZE = 35;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("startabyss")
//          .then(CommandManager.argument("structure_id", StringArgumentType.string())
//            .then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
            .executes(context -> {
//                String structureId = StringArgumentType.getString(context, "structure_id");
//                BlockPos pos = BlockPosArgumentType.getBlockPos(context, "pos");
                placeAbyssLevel(context.getSource().getWorld());
                return 1;
            }));
        dispatcher.register(CommandManager.literal("startwave")
                .executes(context -> {
                    startwave(context.getSource().getWorld());
                            return 1;
                }));
    }

    public static long placeAbyssLevel(ServerWorld world) {
        AbyssLevel abyssLevel = AbyssLevel.genLevel();
        Tile[][] level = abyssLevel.toGrid();

        for (int x = 0; x < level.length; x++){
            for (int z = 0; z < level[0].length; z++) {
                Tile currentTile = level[x][z];
                if (currentTile != null) {
                    BlockPos pos = new BlockPos(x * ROOM_SIZE, 200, z * ROOM_SIZE);
                    PlaceStructureCommand.placeStructure(world, currentTile.structure.name, pos);
                }
            }
        }
        return abyssLevel.seed;
    }

    public static int placeStructure(ServerWorld world, String structureId, BlockPos pos) {
        StructureTemplateManager structureManager = world.getStructureTemplateManager();
        Identifier structureIdentifier = new Identifier(MOD_NAMESPACE, structureId);
        StructureTemplate template = structureManager.getTemplate(structureIdentifier).orElse(null);

        if (template != null) {
            StructurePlacementData placementData = new StructurePlacementData();
            template.place(world, pos, pos, placementData, world.getRandom(), 2);

            return 1;
        }
        return 0;
    }

    public static int startwave(ServerWorld world){
        EntityType<?> entityType = EntityType.ZOMBIE;
        MobEntity mobEntity = (MobEntity) entityType.create(world);

        if (mobEntity != null) {
            // Custom Attributes
            mobEntity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(40.0D);
            mobEntity.setHealth(40.0F);
            mobEntity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(10.0D);
            mobEntity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.35D);

            // Potion Effects
            mobEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 6000, 1));
            mobEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 6000, 2));

            ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
            sword.addEnchantment(Enchantments.SHARPNESS, 5); // Sharpness V
            sword.addEnchantment(Enchantments.UNBREAKING, 3); // Unbreaking III
            mobEntity.equipStack(EquipmentSlot.MAINHAND, sword);

            // Enchant the helmet
            ItemStack helmet = new ItemStack(Items.DIAMOND_HELMET);
            helmet.addEnchantment(Enchantments.PROTECTION, 4); // Protection IV
            helmet.addEnchantment(Enchantments.RESPIRATION, 3); // Respiration III
            mobEntity.equipStack(EquipmentSlot.HEAD, helmet);

            // Enchant the chestplate
            ItemStack chestplate = new ItemStack(Items.DIAMOND_CHESTPLATE);
            chestplate.addEnchantment(Enchantments.PROTECTION, 4); // Protection IV
            chestplate.addEnchantment(Enchantments.THORNS, 3); // Thorns III
            mobEntity.equipStack(EquipmentSlot.CHEST, chestplate);

            // Enchant the leggings
            ItemStack leggings = new ItemStack(Items.DIAMOND_LEGGINGS);
            leggings.addEnchantment(Enchantments.PROTECTION, 4); // Protection IV
            mobEntity.equipStack(EquipmentSlot.LEGS, leggings);

            // Enchant the boots
            ItemStack boots = new ItemStack(Items.DIAMOND_BOOTS);
            boots.addEnchantment(Enchantments.PROTECTION, 4); // Protection IV
            boots.addEnchantment(Enchantments.FEATHER_FALLING, 4); // Feather Falling IV
            mobEntity.equipStack(EquipmentSlot.FEET, boots);

            // Equipment
            mobEntity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND_SWORD));
            mobEntity.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
            mobEntity.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.DIAMOND_CHESTPLATE));
            mobEntity.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.DIAMOND_LEGGINGS));
            mobEntity.equipStack(EquipmentSlot.FEET, new ItemStack(Items.DIAMOND_BOOTS));
            mobEntity.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.0F);
            mobEntity.setEquipmentDropChance(EquipmentSlot.HEAD, 0.0F);
            mobEntity.setEquipmentDropChance(EquipmentSlot.CHEST, 0.0F);
            mobEntity.setEquipmentDropChance(EquipmentSlot.LEGS, 0.0F);
            mobEntity.setEquipmentDropChance(EquipmentSlot.FEET, 0.0F);

            // Custom Name
            mobEntity.setCustomName(Text.literal("Dungeon Boss"));
            mobEntity.setCustomNameVisible(true);
            mobEntity.setPos(0, 5, 0);
            world.spawnEntity(mobEntity);
        }
        return 1;
    }
}
