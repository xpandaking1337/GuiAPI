package pl.panda.gui.event;

import pl.panda.gui.GUI;
import pl.panda.gui.items.Item;

public interface NavigateResponse {

    Item nextPage(GUI inventory);
    Item previousPage(GUI inventory);

}
