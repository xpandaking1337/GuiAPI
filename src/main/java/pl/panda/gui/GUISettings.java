package pl.panda.gui;

public interface GUISettings {

    default boolean canPickUp() {
        return false;
    }

    default boolean canEnter() {
        return false;
    }

    default boolean canClose() {
        return true;
    }

}
