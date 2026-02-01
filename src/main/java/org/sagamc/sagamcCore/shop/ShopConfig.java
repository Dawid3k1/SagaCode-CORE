package org.sagamc.sagamcCore.shop;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ShopConfig {

    public static class ShopItem {
        private final int slot;
        private final Material material;
        private final String name;
        private final List<String> lore;
        private final int price;

        public ShopItem(int slot, Material material, String name, List<String> lore, int price) {
            this.slot = slot;
            this.material = material;
            this.name = name;
            this.lore = lore;
            this.price = price;
        }

        public int getSlot() { return slot; }
        public Material getMaterial() { return material; }
        public String getName() { return name; }
        public List<String> getLore() { return lore; }
        public int getPrice() { return price; }
    }

    private final String shopTitle;
    private final String confirmationTitle;
    private final List<ShopItem> items;

    public ShopConfig(FileConfiguration config) {
        this.shopTitle = config.getString("shop.title", "&eKucharz &7(1/1)");
        this.confirmationTitle = config.getString("shop.confirmation-title", "&aPotwierdzenie zakupu");
        this.items = new ArrayList<>();

        ConfigurationSection itemsSection = config.getConfigurationSection("shop.items");
        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);
                if (itemSection != null) {
                    int slot = itemSection.getInt("slot");
                    Material material = Material.valueOf(itemSection.getString("material"));
                    String name = itemSection.getString("name");
                    List<String> lore = itemSection.getStringList("lore");
                    int price = itemSection.getInt("price");

                    items.add(new ShopItem(slot, material, name, lore, price));
                }
            }
        }
    }

    public String getShopTitle() { return shopTitle; }
    public String getConfirmationTitle() { return confirmationTitle; }
    public List<ShopItem> getItems() { return items; }

    public int getPriceForMaterial(Material material) {
        for (ShopItem item : items) {
            if (item.getMaterial() == material) {
                return item.getPrice();
            }
        }
        return 0;
    }
}