# GuiAPI
GUI API for Bukkit 1.8 - 1.18

## Features:
 - Works with Bukkit 1.8 - 1.18
 - Compatible with Java 8 - 17
 - Create GUI with multiple pages
 - Setup GUI Response
 - Add custom actions when clicked items
 - Add sounds response when clicked items
 - Automatic translation of `&` based colors

## How to use?
#### Java code

```java
public class TestPlugin extends JavaPlugin implements Listener {

    private GuiAPI guiAPI;
    private TestPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        guiAPI = GuiAPIInterface.INSTANCE.create(this); //Initialize GUI API

        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    private void onBreak(BlockBreakEvent event) { //Implement test event to open inventory.
        Player player = event.getPlayer();
        this.guiAPI.openGUI(player, new TestGUI(this)); //Open TestGUI inventory
    }

    public GuiAPI getGuiAPI() {
        return guiAPI;
    }

}

public class TestGUI extends GUI<TestPlugin> {

    public TestGUI(TestPlugin plugin) {
        super(plugin, Size.THREE);

        this.setupNavigation(new Navigation()
                .response(new NavigateResponse() { //Setup page items response
                    @Override
                    public Item nextPage(GUI inventory) {
                        if (isNextPageExists()) {
                            return new Item(Material.ARROW, 1)
                                    .setName("&6Next page")
                                    .setLore("&aClick to switch to " + (inventory.getPage().getNextPageCount()) + " page");
                        }
                        else return new Item(Material.ARROW, 1)
                                .setName("&cNext page doesn't exists");
                    }

                    @Override
                    public Item previousPage(GUI inventory) {
                        if (isPreviousPageExists()) {
                            return new Item(Material.ARROW, 1)
                                    .setName("&6Previous page")
                                    .setLore("&aClick to switch to " + (inventory.getPage().getPreviousPageCount()) + " page");
                        }
                        else return new Item(Material.ARROW, 1)
                                .setName("&cPrevious page doesn't exists");
                    }
                })
                .setup(26, Navigation.NavigationType.NEXT) //Remember to setup slots with navigation types, otherwise the buttons will not appear
                .setup(18, Navigation.NavigationType.PREVIOUS)
        );

        this.setupGUIResponse(new GUIResponse() { //Setup GUI Response when inventory is opened or closed
            @Override
            public void onOpen(GUI inventory) {
                inventory.getInventoryOwner().sendMessage("GUI opened.");
            }

            @Override
            public void onClose(GUI inventory) {
                inventory.getInventoryOwner().sendMessage("GUI closed.");
            }
        });

        configureGlassSlots(); //Fill slots with glass;

        setItem(4, new Item(Material.SKULL_ITEM)
                .skullOwner(getInventoryOwner().getDisplayName())
                .setName("&a" + getInventoryOwner().getDisplayName())
                .setLore(
                        "&7Gamemode: &6" + getInventoryOwner().getGameMode().toString(),
                        "&7Health: &6" + getInventoryOwner().getHealth(),
                        "&7Food: &6" + getInventoryOwner().getFoodLevel(),
                        "",
                        "&aClick to get the diamond"
                )
                .setResponse(event -> { //Action to execute when player click the item
                    Inventory inventory = event.getWhoClicked().getInventory();
                    inventory.addItem(new ItemStack(Material.DIAMOND, 1));
                })
                .setSound(() -> Sound.LAVA_POP) //Play a sound when player click the item
                .sticked(true) //Set sticked to true, this item will appear on all pages
        );

        for (int i = 0; i < 3; i++) {
            addItem(new Item(Material.APPLE)); //Add apple to first empty slot
        }

        setItem(1, 13, new Item(Material.DIAMOND_SWORD)); //Add diamond sword to second page.

    }

    private void configureGlassSlots() {
        final int[] tiles = {
                0, 1, 2, 3, 5, 6, 7, 8
        };
        final Item glass = new Item(Material.STAINED_GLASS_PANE, (short) 15).sticked(true); //Set sticked to true, this item will appear on all pages
        Arrays.stream(tiles).forEach(index -> setItem(index, glass));
    }

    @Override
    public String getTitle() {
        return "&6Test GUI {CURRENT_PAGE}/{MAX_PAGE}"; //GUI title, {CURRENT_PAGE} - returns current page, {MAX_PAGE} - returns pages count
    }

    @Override
    public GUISettings getSettings() {
        return new GUISettings() {
            @Override
            public boolean canPickUp() {
                return false; //Pickup items to inventory
            }

            @Override
            public boolean canEnter() {
                return false; //Enter items to inventory
            }

            @Override
            public boolean canClose() {
                return true; //Prevent to close inventory
            }
        };
    }

    @Override
    public Integer[] blankSlots() {
        return IntStream.range(19, 27).boxed().toArray(Integer[]::new); //Slots beetween 19 and 26 always is empty, cannot be filled
    }
}
```

