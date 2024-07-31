package net.lightning.trealm;

import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.resource.featuretoggle.ToggleableFeature;

public class ModItem extends Item implements ToggleableFeature, ItemConvertible, FabricItem, RegistrableItem {
    public final RegistryData registryData;

    public ModItem(RegistryData registryData) {
        this(new FabricItemSettings(), registryData);
    }
    public ModItem(Item.Settings settings, RegistryData registryData) {
        super(settings);
        this.registryData = registryData;
        TravelersRealmDataGenerator.ITEMS.add(this);
    }

    public RegistryData getRegistrationData() {
        return this.registryData;
    }
}
