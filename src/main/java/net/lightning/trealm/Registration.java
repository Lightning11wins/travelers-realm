package net.lightning.trealm;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider.TranslationBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.Collection;

import static net.lightning.trealm.TravelersRealm.MOD_NAMESPACE;

public abstract class Registration implements ItemConvertible {
    public interface Registrable {
        void register();
        void registerDisplayName(TranslationBuilder translationBuilder);
    }
    public interface RegistrableItem extends Registrable {
        void registerModel(ItemModelGenerator itemModelGenerator);
    }
    public interface RegistrableBlock extends Registrable {
        void registerModel(BlockStateModelGenerator blockStateModelGenerator);
    }

    protected Identifier identifier, texture;
    protected String displayName;

    public Registration(Identifier identifier, Identifier texture, String displayName) {
        this.identifier = identifier;
        this.texture = texture;
        this.displayName = displayName;
    }

    public static class ItemRegistration extends Registration implements RegistrableItem {
        protected Item item;

        public ItemRegistration(Item item, Identifier identifier, Identifier texture, String displayName) {
            super(identifier, texture, displayName);
            this.item = item;
            TravelersRealmDataGenerator.ITEMS.add(this);
        }

        public void register() {
            Registry.register(Registries.ITEM, this.identifier, this.asItem());
        }
        public void registerModel(ItemModelGenerator itemModelGenerator) {
            final Identifier texture = this.texture;
            if (texture != null) {
                final TextureMap textureMap = new TextureMap().put(TextureKey.LAYER0, texture);
                Models.GENERATED.upload(this.identifier.withPrefixedPath("item/"), textureMap, itemModelGenerator.writer);
            }
        }
        public void registerDisplayName(TranslationBuilder translationBuilder) {
            final String displayName = this.displayName;
            if (displayName != null) {
                translationBuilder.add("item." + this.identifier.toTranslationKey(), displayName);
            }
        }

        public ItemRegistration addTo(Collection<ItemRegistration> collection) {
            collection.add(this);
            return this;
        }

        @Override
        public Item asItem() {
            return this.item;
        }

        public static class Builder {
            protected Item item;
            protected Identifier identifier, texture;
            protected String displayName;

            public Builder() {
                this(new FabricItemSettings());
            }
            public Builder(FabricItemSettings settings) {
                this.item = new Item(settings);
            }

            public Builder identifier(String texture) {
                return this.identifier(new Identifier(MOD_NAMESPACE, texture));
            }
            public Builder identifier(Identifier identifier) {
                this.identifier = identifier;
                return this;
            }
            public Builder texture(String texture) {
                return this.texture(new Identifier(MOD_NAMESPACE, texture));
            }
            public Builder texture(Identifier texture) {
                this.texture = texture;
                return this;
            }
            public Builder displayName(String displayName) {
                this.displayName = displayName;
                return this;
            }

            public ItemRegistration build() {
                return new ItemRegistration(this.item, this.identifier, this.texture, this.displayName);
            }
        }
    }

    public static class BlockRegistration extends Registration implements RegistrableBlock {
        protected Block block;
        protected Item blockItem;

        public BlockRegistration(Block block, Identifier identifier, Identifier texture, String displayName) {
            super(identifier, texture, displayName);
            this.block = block;
            this.blockItem = new BlockItem(block, new FabricItemSettings());
            TravelersRealmDataGenerator.BLOCKS.add(this);
        }

        public void register() {
            Registry.register(Registries.BLOCK, this.identifier, this.block);
            Registry.register(Registries.ITEM, this.identifier, this.blockItem);
        }
        public void registerModel(BlockStateModelGenerator blockStateModelGenerator) {
            final Identifier texture = this.texture, identifier = this.identifier.withPrefixedPath("block/");
            if (texture != null) {
                final TextureMap textures = new TextureMap()
                    .put(TextureKey.ALL, texture)
                    .put(TextureKey.PARTICLE, texture);
                Models.CUBE_ALL.upload(identifier, textures, blockStateModelGenerator.modelCollector);
                blockStateModelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(this.block, identifier));
            }
        }
        public void registerDisplayName(TranslationBuilder translationBuilder) {
            final String displayName = this.displayName;
            if (displayName != null) {
                translationBuilder.add("block." + this.identifier.toTranslationKey(), displayName);
            }
        }

        public BlockRegistration addTo(Collection<BlockRegistration> collection) {
            collection.add(this);
            return this;
        }

        @Override
        public Item asItem() {
            return this.blockItem;
        }

        public static class Builder {
            protected Block block;
            protected Identifier identifier, texture;
            protected String displayName;

            public Builder() {
                this(Block.Settings.create());
            }
            public Builder(Block blockTemplate) {
                this(blockTemplate.getSettings());
            }
            public Builder(Block.Settings settings) {
                this.block = new Block(settings);
            }

            public Builder identifier(String texture) {
                return this.identifier(new Identifier(MOD_NAMESPACE, texture));
            }
            public Builder identifier(Identifier identifier) {
                this.identifier = identifier;
                return this;
            }
            public Builder texture(String texture) {
                return this.texture(new Identifier(MOD_NAMESPACE, texture));
            }
            public Builder texture(Identifier texture) {
                this.texture = texture;
                return this;
            }
            public Builder displayName(String displayName) {
                this.displayName = displayName;
                return this;
            }

            public BlockRegistration build() {
                return new BlockRegistration(this.block, this.identifier, this.texture, this.displayName);
            }
        }
    }
}
