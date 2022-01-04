package pl.panda.gui.pages;

import pl.panda.gui.GUI;
import pl.panda.gui.event.NavigateResponse;
import pl.panda.gui.items.Item;

import java.util.HashMap;
import java.util.Map;

public class Navigation {

    private Map<Integer, NavigationType> navigationMap = new HashMap<>();
    private NavigateResponse response;

    public NavigationType getNavigationType(int slot) {
        return navigationMap.values().stream().filter(type -> get(slot) == type).findFirst().orElse(null);
    }

    public Item getNavigationItem(GUI inventory, int slot) {
        final NavigationType type = getNavigationType(slot);
        if(type == null) return null;
        if(type == NavigationType.NEXT) {
            return response.nextPage(inventory);
        }
        if(type == NavigationType.PREVIOUS) {
            return response.previousPage(inventory);
        }
        return null;
    }

    public boolean isActionToExecute(int slot) {
        return get(slot) != null;
    }

    public boolean execute(GUI inventory, Item item) {
        if(response.nextPage(inventory) != null) {
            if (response.nextPage(inventory).equals(item)) {
                return inventory.nextPage();
            }
        }
        if(response.previousPage(inventory) != null) {
            if (response.previousPage(inventory).equals(item)) {
                return inventory.previousPage();
            }
        }
        return false;
    }

    public Map<Integer, NavigationType> getMap() {
        return navigationMap;
    }

    public NavigationType get(int slot) {
        return navigationMap.get(slot);
    }

    public Navigation setup(int slot, NavigationType type) {
        this.navigationMap.put(slot, type);
        return this;
    }

    public NavigateResponse getResponse() {
        return response;
    }

    public Navigation response(NavigateResponse response) {
        this.response = response;
        return this;
    }

    public enum NavigationType {

        NEXT,
        PREVIOUS

    }

}
