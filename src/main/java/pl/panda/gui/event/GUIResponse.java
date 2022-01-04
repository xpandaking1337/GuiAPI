package pl.panda.gui.event;

import pl.panda.gui.GUI;

public interface GUIResponse {

    void onOpen(GUI inventory);
    void onClose(GUI inventory);
}
