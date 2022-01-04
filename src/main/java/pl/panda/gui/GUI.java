package pl.panda.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;
import pl.panda.gui.event.GUIResponse;
import pl.panda.gui.items.Item;
import pl.panda.gui.enums.Size;
import pl.panda.gui.pages.Navigation;
import pl.panda.gui.pages.Page;
import pl.panda.gui.utils.Utils;

import java.util.*;

public abstract class GUI<T extends JavaPlugin> implements InventoryHolder, GUISettings {

    private JavaPlugin plugin;
    private Map<Integer, Item> items = new HashMap<>();
    private HashSet<Integer> stickedItems = new HashSet<>();
    private GUIResponse guiResponse;
    private Navigation navigation;
    private Size size;
    private InventoryType type;
    private Player player;
    private Page page;

    public GUI(T plugin, Size size) {
        this.plugin = plugin;
        this.size = size;

        this.page = new Page();
    }

    public GUI(T plugin, InventoryType type) {
        this.plugin = plugin;
        this.type = type;

        this.page = new Page();
    }

    public abstract String getTitle();
    public abstract GUISettings getSettings();
    public abstract Integer[] blankSlots();

    public void open(Player player) {
        this.player = player;
        player.openInventory(getInventory());
    }

    public void addItem(Item item) {
        setItem(getBlankIndex(), item);
    }

    public int getBlankIndex() {
        for (int page = 0; page < getPagesCount() + 1; page++) {
            for (int index = 0; index < getPageSize(); index++) {
                if(this.items.get((page * getPageSize()) + index) == null
                    && navigation != null
                    && navigation.getNavigationItem(this, index) == null
                    && !isStickedItem(index)
                    && !isBlankSlot(index)) {
                        return (page * getPageSize()) + index;
                }
            }
        }
        return -1;
    }

    public void addItems(Item... items) {
        for(Item item : items) addItem(item);
    }

    public void setItem(int slot, Item item) {
        if(isBlankSlot(slot)) return;
        if (item.isSticked()) stickedItems.add(slot);
        this.items.put(slot, item);
    }

    public void setItem(int page, int slot, Item item) {
        if(slot < 0 || slot > getPageSize()) return;

        setItem((page * getPageSize()) + slot, item);
    }

    public void removeItem(int slot) {
        items.remove(slot);
    }

    public void removeItem(int page, int slot) {
        if(slot < 0 || slot > getPageSize()) return;

        removeItem((page * getPageSize()) + slot);
    }

    public Item getItem(int slot) {
        if(slot < 0 || slot > getMaxSlot()) return null;

        return items.get(slot);
    }

    public Item getItem(int page, int slot) {
        if(slot < 0 || slot > getPageSize()) return null;

        return getItem((page * getPageSize()) + slot);
    }

    public Page getPage() {
        return page;
    }

    public int getMaxSlot() {
        int slot = 0;

        for(int nextSlot : items.keySet()) {
            if(items.get(nextSlot) != null && nextSlot > slot)
                slot = nextSlot;
        }
        return slot;
    }

    public int getItemsCount() {
        return items.size();
    }

    public int getPageSize() {
        return size.getGuiSize();
    }

    public int getPagesCount() {
        if(getMaxSlot() > getItemsCount()) return (int) Math.ceil(((double) getMaxSlot() + 1) / ((double) getPageSize()));
        return (int) Math.ceil(((double) getItemsCount()) / ((double) getPageSize()));
    }

    public boolean isNextPageExists() {
        return page.getCurrentPage().get() < getPagesCount() - 1;
    }

    public boolean isPreviousPageExists() {
        return page.getCurrentPage().get() > 0;
    }

    public boolean nextPage() {
        if(isNextPageExists()) {
            page.getCurrentPage().getAndIncrement();
            update();
            return true;
        }
        return false;
    }

    public boolean previousPage() {
        if(isPreviousPageExists()) {
            page.getCurrentPage().getAndDecrement();
            update();
            return true;
        }
        return false;
    }

    public Player getInventoryOwner() {
        return this.player;
    }

    public boolean isStickedItem(int slot) {
        return this.stickedItems.contains(slot);
    }

    public boolean isBlankSlot(int slot) {
        return Arrays.stream(this.blankSlots()).anyMatch(integer -> integer == slot);
    }

    public void setupGUIResponse(GUIResponse guiResponse) {
        this.guiResponse = guiResponse;
    }

    public void setupNavigation(Navigation navigation) {
        this.navigation = navigation;
    }

    public void handleOnClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (event.getCurrentItem() == null) return;
        if (!event.getClickedInventory().getHolder().equals(getInventory().getHolder())) return;
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = ((Player) event.getWhoClicked());

        int index = event.getSlot();

        if(!this.getSettings().canPickUp()) {
            event.setCancelled(true);
        }

        if(navigation != null && navigation.getResponse() != null && navigation.isActionToExecute(index)) {
            final Item clickedItem = new Item(event.getCurrentItem());
            navigation.execute(this, clickedItem);
            return;
        }

        if(isStickedItem(index)) {
            final Item clickedItem = getItem(0, index);
            if(clickedItem != null) {
                if(clickedItem.getResponse() != null) {
                    clickedItem.getResponse().onClick(event);
                }
                if(clickedItem.getSoundResponse() != null) {
                    player.playSound(player.getLocation(), clickedItem.getSoundResponse().onResult(), 1F, 1F);
                }
            }
            return;
        }

        final Item clickedItem = getItem(page.getCurrentPage().get(), index);
        if(clickedItem != null) {
            if(clickedItem.getResponse() != null) {
                clickedItem.getResponse().onClick(event);
            }
            if(clickedItem.getSoundResponse() != null) {
                player.playSound(player.getLocation(), clickedItem.getSoundResponse().onResult(), 1F, 1F);
            }
        }

    }

    public void handleOnDrag(final InventoryDragEvent event) {
        if (!event.getInventory().getHolder().equals(getInventory().getHolder())) return;
        if (!(event.getWhoClicked() instanceof Player)) return;
        if(!this.getSettings().canEnter()) {
            event.setCancelled(true);
        }
    }

    public void handleInventoryOpen(final InventoryOpenEvent event) {
        if(!(event.getPlayer() instanceof Player)) return;
        if (!event.getInventory().getHolder().equals(getInventory().getHolder())) return;
        if(guiResponse != null) guiResponse.onOpen(this);
    }

    public void handleInventoryClose(final InventoryCloseEvent event) {
        if(!(event.getPlayer() instanceof Player)) return;
        if (!event.getInventory().getHolder().equals(getInventory().getHolder())) return;
        if(!getSettings().canClose()) {
            Bukkit.getScheduler().runTaskLater(
                    plugin,
                    () -> open((Player)event.getPlayer()), 5L);
            return;
        }
        if(guiResponse != null) guiResponse.onClose(this);

        Bukkit.getScheduler().runTaskLater(
                plugin,
                () -> ((Player) event.getPlayer()).updateInventory(), 5L);
    }

    public void update() {

        String title = getTitle()
                .replace("{CURRENT_PAGE}", String.valueOf(page.getCurrentPageCount()))
                .replace("{MAX_PAGE}", String.valueOf(getPagesCount()));

        if(!player.getOpenInventory().getTitle().equals(title)) {
            player.openInventory(getInventory());
            return;
        }

        player.getOpenInventory().getTopInventory().setContents(getInventory().getContents());
    }

    @Override
    public Inventory getInventory() {
        Inventory inventory = type == null ?
                Bukkit.createInventory(this, getPageSize(), Utils.fixColors(getTitle()
                        .replace("{CURRENT_PAGE}", String.valueOf(page.getCurrentPageCount()))
                        .replace("{MAX_PAGE}", String.valueOf(getPagesCount()))))
                :
                Bukkit.createInventory(this, type, Utils.fixColors(getTitle()
                        .replace("{CURRENT_PAGE}", String.valueOf(page.getCurrentPageCount()))
                        .replace("{MAX_PAGE}", String.valueOf(getPagesCount()))
                ));

        for(int key = page.getCurrentPage().get() * getPageSize(); key < (page.getCurrentPage().get() + 1) * getPageSize(); key++) {
            if(key > getMaxSlot()) break;

            if(items.containsKey(key)) {
                inventory.setItem(key - (page.getCurrentPage().get() * getPageSize()), items.get(key).get());
            }
        }

        if(navigation != null && navigation.getResponse() != null) {
            navigation.getMap().forEach((index, item) -> {
                final Item navigationItem = navigation.getNavigationItem(this, index);
                if(navigationItem != null) inventory.setItem(index, navigationItem.get());
            });
        }

        stickedItems.forEach(index -> inventory.setItem(index, items.get(index).get()));

        return inventory;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }
}
