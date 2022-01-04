package pl.panda.gui.items;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import pl.panda.gui.event.ItemResponse;
import pl.panda.gui.event.SoundResponse;
import pl.panda.gui.utils.GsonUtils;
import pl.panda.gui.utils.Utils;

import java.io.Serializable;
import java.util.*;

public class Item implements Serializable {

    private ItemResponse itemResponse;
    private SoundResponse soundResponse;
    private boolean sticked;
    private ItemStack itemStack;
    private ItemMeta itemMeta;

    public Item(Material material, int amount) {
        set(material, amount);
    }

    public Item(Material material) {
        set(material, 1);
    }

    public Item(ItemStack itemStack) {
        set(itemStack);
    }

    public Item(Material material, short data) {
        set(material, 1, data);
    }

    public Item(Material material, int amount, int data) {
        set(material, amount, (short)data);
    }

    public Item(Material material, int amount, short data) {
        set(material, amount, data);
    }

    public Item set(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();
        return this;
    }

    public Item set(Material material, int amount, short data) {
        this.itemStack = new ItemStack(material, amount, data);
        this.itemMeta = itemStack.getItemMeta();
        return this;
    }

    public Item set(Material material, int amount) {
        this.itemStack = new ItemStack(material, amount);
        this.itemMeta = itemStack.getItemMeta();
        return this;
    }

    public Item setName(String name) {
        itemMeta.setDisplayName(Utils.fixColors(name));
        update();
        return this;
    }

    public Item setLore(List<String> lore) {
        itemMeta.setLore(Utils.fixColors(lore));
        update();
        return this;
    }

    public Item setLore(String... lore) {
        itemMeta.setLore(Utils.fixColors(Arrays.asList(lore)));
        update();
        return this;
    }

    public Item setEnchantments(Map<Enchantment, Integer> enchantments) {
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet())
            itemMeta.addEnchant(entry.getKey(), entry.getValue(), true);
        update();
        return this;
    }

    public Item flag(ItemFlag... flags) {
        itemMeta.addItemFlags(flags);
        return this;
    }

    public Item skullOwner(String name) {
        if (!(itemStack.getItemMeta() instanceof SkullMeta)) return this;
        itemStack.setDurability((byte) 3);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setOwner(name);
        itemStack.setItemMeta(skullMeta);
        return this;
    }

    public Item update() {
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public Item setResponse(ItemResponse itemResponse) {
        this.itemResponse = itemResponse;
        return this;
    }

    public ItemResponse getResponse() {
        return itemResponse;
    }

    public Item setSound(SoundResponse soundResponse) {
        this.soundResponse = soundResponse;
        return this;
    }

    public SoundResponse getSoundResponse() {
        return soundResponse;
    }

    public Item sticked(boolean sticked) {
        this.sticked = sticked;
        return this;
    }

    public boolean isSticked() {
        return sticked;
    }

    public ItemStack get() {
        return itemStack;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return sticked == item.sticked && Objects.equals(itemResponse, item.itemResponse) && Objects.equals(soundResponse, item.soundResponse) && Objects.equals(itemStack, item.itemStack) && Objects.equals(itemMeta, item.itemMeta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemResponse, soundResponse, sticked, itemStack, itemMeta);
    }

    public static String toJson(Item item) {
        return GsonUtils.parseToJson(item);
    }

    public static Item fromJson(String json) {
        return GsonUtils.parseToObject(json, Item.class);
    }

}
