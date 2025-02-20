package me.thanhmagics.utils;

import me.thanhmagics.ItemUpgrade;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

public class SelectionGUI2 implements Listener {

    private static final Map<UUID,SelectionGUI2> storages = new HashMap<>();
    private static final Map<UUID,UUID> players = new HashMap<>();
    private static final List<UUID> opens = new ArrayList<>();
    private static boolean regEvent = false;

    private UUID uuid = UUID.randomUUID();
    private SG2_OnClick onClickPI = null;
    private ItemStack decorItem = null;
    private int page;
    private int maxPage;
    private int size;
    private String title = "Inventory";
    private boolean pageTravelling = false;

    private final LinkedList<Item> items = new LinkedList<>();
    private final List<SG2_Runnable> onClose = new ArrayList<>();
    private final Map<Integer,Item> specialItems = new HashMap<>();
    private final Map<Integer,ItemStack> decorItems = new HashMap<>();
    private final Map<Integer,Item> itemsIndex = new HashMap<>();


    public SelectionGUI2(Items items)  {
        if (items != null) this.items.addAll(items.getItems());
        if (!regEvent) {
            ItemUpgrade.getInstance().getServer().getPluginManager().registerEvents(this,ItemUpgrade.getInstance());
            regEvent = true;
        }
    }

    public SelectionGUI2 addPageTravelling() {
        pageTravelling = true;
        return this;
    }

    public SelectionGUI2 addDecorItems() {
        this.decorItem = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName("&a").build();
        return this;
    }

    public SelectionGUI2 addDecorItems(ItemStack itemStack,int... slots) {
        for (int i : slots) decorItems.put(i,itemStack);
        return this;
    }

    public SelectionGUI2 addSpecialItem(int slot,Item item) {
        specialItems.put(slot,item);
        return this;
    }

    public SelectionGUI2 addSpecialItemInBottom(int index,Item item) {
        specialItems.put(size - 9 + index,item);
        return this;
    }

    public SelectionGUI2 addOnCloseRunnable(SG2_Runnable... runs) {
        onClose.addAll(Arrays.asList(runs));
        return this;
    }

    public SelectionGUI2 open(Player player,int page) {
        uuid = UUID.randomUUID();
        this.page = page;
        Inventory inventory = Bukkit.createInventory(null,size,Utils.applyColor(title));
        if (decorItem != null) {
            addDecorItems(decorItem, 0,1,2,3,4,5,6,7,8,size - 1,size - 2,size - 3,size - 4,size - 5,size - 6,size - 7,size - 8,size - 9);
            int i1 = (size - 18) / 9;
            for (int i = 0; i < i1; i++) {
                addDecorItems(decorItem,9 + (9 * i),9 + ((9 * i) + 8));
            }
        }
        size = this.items.size() <= 28 ? ((this.items.size()-1) / 7) * 9 + 27: 54;
        maxPage = this.items.size() <= 28 ? 1 : (this.items.size() / 26) + 1;
        decorItems.forEach(inventory::setItem);
        if (pageTravelling) {
            addSpecialItem(size - 1, new Item(new ItemBuilder(Material.ARROW)
                    .setDisplayName("&aTrang Tiếp Theo &7(" + page + "/" + maxPage + ")")
                    .addLore(" ")
                    .addLore(maxPage == page ? "&cBạn Đang Ở Trang Cuối!" : "&aẤn Để Sang Trang!")
                    .build()) {
                @Override
                public void onClick(ClickType clickType, SelectionGUI2 instance) {
                    if (page < maxPage) open(player, page + 1);
                }
            });
            addSpecialItem(size - 2, new Item(new ItemBuilder(Material.ARROW)
                    .setDisplayName("&aTrang Trước Đó &7(" + page + "/" + maxPage + ")")
                    .addLore(" ")
                    .addLore(page <= 1 ? "&cBạn Đang Ở Trang Đầu!" : "&aẤn Để Trở Về Trang Trước!")
                    .build()) {
                @Override
                public void onClick(ClickType clickType, SelectionGUI2 instance) {
                    if (page > 1) open(player, page - 1);
                }
            });
        }
        specialItems.forEach((k,v) -> {
            inventory.setItem(k,v.getItemStack());
            itemsIndex.put(k,v);
        });
        for (int i = page == 1 ? 0 : (size - 26) * (page - 1); i < items.size(); i++) {
            if (Utils.isInvFull(inventory)) break;
            Item is = items.get(i);
            int fe = inventory.firstEmpty();
            itemsIndex.put(fe,is);
            inventory.setItem(fe, is.getItemStack());
        }
        opens.remove(player.getUniqueId());
        player.openInventory(inventory);
        opens.add(player.getUniqueId());
        players.put(player.getUniqueId(),uuid);
        storages.put(uuid,this);
        return this;
    }

    public SelectionGUI2 addOnClickPI(SG2_OnClick clickPI) {
        this.onClickPI = clickPI;
        return this;
    }


    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!players.containsKey(player.getUniqueId())) return;
        SelectionGUI2 selectionGUI2 = storages.get(players.get(player.getUniqueId()));
        if (event.getClickedInventory() instanceof PlayerInventory) {
            event.setCancelled(true);
            if (selectionGUI2.onClickPI != null) selectionGUI2.onClickPI.onClick(event.getCurrentItem(),event.getClick(),selectionGUI2,event);
        } else {
            Item clicked = selectionGUI2.itemsIndex.get(event.getSlot());
            if (clicked == null) {
                event.setCancelled(true);
                return;
            }
            clicked.onClick(event.getClick(),selectionGUI2);
            event.setCancelled(clicked.cancel);
        }
    }

    @EventHandler
    public void onCloseInv(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (players.containsKey(player.getUniqueId())) {
            if (opens.contains(player.getUniqueId())) {
                SelectionGUI2 instance = storages.get(players.get(player.getUniqueId()));
                instance.onClose.forEach(e -> e.run(player,instance));
            }
            storages.remove(players.get(player.getUniqueId()));
            players.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (players.containsKey(player.getUniqueId())) {
            if (opens.contains(player.getUniqueId())) {
                SelectionGUI2 instance = storages.get(players.get(player.getUniqueId()));
                instance.onClose.forEach(e -> e.run(player,instance));
            }
            storages.remove(players.get(player.getUniqueId()));
            players.remove(player.getUniqueId());
        }
    }


    public String getTitle() {
        return title;
    }

    public SelectionGUI2 setTitle(String title) {
        this.title = title;
        return this;
    }

    public SG2_OnClick getOnClickPI() {
        return onClickPI;
    }

    public SelectionGUI2 setOnClickPI(SG2_OnClick onClickPI) {
        this.onClickPI = onClickPI;
        return this;
    }

    public int getPage() {
        return page;
    }

    public boolean isPageTravelling() {
        return pageTravelling;
    }

    public ItemStack getDecorItem() {
        return decorItem;
    }

    public UUID getUuid() {
        return uuid;
    }

    public static abstract class SG2_OnClick {
        public abstract void onClick(ItemStack clicked,ClickType type,SelectionGUI2 instance,InventoryClickEvent event);
    }

    public static abstract class SG2_Runnable {
        public abstract void run(Player player,SelectionGUI2 instance);
    }

    public static abstract class Item {

        private ItemStack itemStack;

        private boolean cancel = true;

        public Item(ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        public void setCancel(boolean cancel) {
            this.cancel = cancel;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }

        public abstract void onClick(ClickType clickType,SelectionGUI2 instance);
    }

    public static abstract class Items {

        public abstract LinkedList<Item> getItems();
    }


}
