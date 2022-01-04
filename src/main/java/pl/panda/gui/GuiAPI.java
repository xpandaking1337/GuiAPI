package pl.panda.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class GuiAPI implements Listener, GuiAPIInterface {

    private JavaPlugin plugin;
    private final Map<UUID, GUI> playersGui = new HashMap<>();

    @Override
    public void openGUI(Player player, GUI gui) {
        this.playersGui.put(player.getUniqueId(), gui);
        gui.open(player);
    }

    @Override
    public GUI getGUI(Player player) {
        return playersGui.get(player.getUniqueId());
    }

    @Override
    public GuiAPI create(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        return this;
    }

    @EventHandler
    private void onInventoryClick(final InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player)) return;
        GUI openedGUI = getGUI((Player) event.getWhoClicked());
        if (openedGUI == null) return;
        openedGUI.handleOnClick(event);
    }

    @EventHandler
    private void onInventoryDrag(final InventoryDragEvent event) {
        if(!(event.getWhoClicked() instanceof Player)) return;
        GUI openedGUI = getGUI((Player) event.getWhoClicked());
        if (openedGUI == null) return;
        openedGUI.handleOnDrag(event);
    }

    @EventHandler
    private void onInventoryOpen(final InventoryOpenEvent event) {
        if(!(event.getPlayer() instanceof Player)) return;
        GUI openedGUI = getGUI((Player) event.getPlayer());
        if (openedGUI == null) return;
        openedGUI.handleInventoryOpen(event);
    }

    @EventHandler
    private void onInventoryClose(final InventoryCloseEvent event) {
        if(!(event.getPlayer() instanceof Player)) return;
        GUI openedGUI = getGUI((Player) event.getPlayer());
        if (openedGUI == null) return;
        openedGUI.handleInventoryClose(event);
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }
}
