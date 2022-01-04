package pl.panda.gui;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public interface GuiAPIInterface {

    GuiAPI INSTANCE = new GuiAPI();

    void openGUI(Player player, GUI gui);

    GUI getGUI(Player player);

    GuiAPI create(JavaPlugin plugin);

}
