package pl.panda.gui.enums;

public enum Size {

    ONE(9),
    TWO(18),
    THREE(27),
    FOUR(36),
    FIVE(45),
    SIX(54);

    private int guiSize;

    Size(int guiSize) {
        this.guiSize = guiSize;
    }

    public int getGuiSize() {
        return guiSize;
    }
}
