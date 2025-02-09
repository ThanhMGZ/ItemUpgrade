package me.thanhmagics.utils;

import me.thanhmagics.ItemUpgrade;
import me.thanhmagics.core.PlayerDT;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryBuilder implements Listener {

    private static boolean regEvent = false;

    public InventoryBuilder() {
        if (!regEvent) {
            ItemUpgrade.getInstance().getServer().getPluginManager().registerEvents(this,ItemUpgrade.getInstance());
            regEvent = !regEvent;
        }
    }

    private int size = 54;

    private String display = "";

    private UUID uuid = UUID.randomUUID();

    private Map<Integer, ItemStack> decorItems = new HashMap<>();

    private Map<Integer,IBConfig> actionItems = new HashMap<>();

    private Inventory inventory;

    public static Map<UUID,InventoryBuilder> inventoryBuilders = new HashMap<>();

    public InventoryBuilder setSize(int size) {
        this.size = size;
        return this;
    }

    public InventoryBuilder setTitle(String title) {
        display = Utils.applyColor(title);
        return this;
    }

    public Inventory getInventory() {
        return inventory;
    }

    private Inventory build() {
        Inventory inventory = Bukkit.createInventory(null,size,Utils.applyColor(display));
        for (Integer integer : decorItems.keySet()) {
            inventory.setItem(integer,decorItems.get(integer));
        }
        for (Integer integer : actionItems.keySet()) {
            inventory.setItem(integer,actionItems.get(integer).itemStack());
        }
        return inventory;
    }

    public InventoryBuilder addActionItem(int i, IBConfig config) {
        actionItems.remove(i);
        actionItems.put(i,config);
        return this;
    }

    public InventoryBuilder addDecorItem(int i, ItemStack itemStack) {
        decorItems.remove(i);
        decorItems.put(i,itemStack);
        return this;
    }

    public void open(PlayerDT... playerData) {
        for (PlayerDT data : playerData) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(data.uuid);
            if (player.isOnline()) {
                this.inventory = build();
                player.getPlayer().openInventory(this.inventory);
                data.inv = uuid;
                inventoryBuilders.put(uuid, this);
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        PlayerDT playerData = ItemUpgrade.getInstance().data.playerDT.get(player.getUniqueId());
        if (playerData.inv == null) return;
        if (inventoryBuilders.containsKey(playerData.inv)) {
            event.setCancelled(true);
            if (event.getClickedInventory() instanceof PlayerInventory) return;
            InventoryBuilder inventoryBuilder = inventoryBuilders.get(playerData.inv);
            if (inventoryBuilder.actionItems.containsKey(event.getSlot())) {
                inventoryBuilder.actionItems.get(event.getSlot()).onClick(event.getCurrentItem(),
                        event.getSlot(),event.getClick(),inventoryBuilder);
            }
        }
    }
    @EventHandler
    public void onCloseInv(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        PlayerDT slayer = ItemUpgrade.getInstance().data.playerDT.get(player.getUniqueId());
        if (slayer.inv != null) slayer.inv = null;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerDT slayer = ItemUpgrade.getInstance().data.playerDT.get(player.getUniqueId());
        if (slayer.inv != null) slayer.inv = null;
    }
    public static abstract class IBConfig {

        public abstract ItemStack itemStack();

        public abstract void onClick(ItemStack clicked, int stt, ClickType clickType, InventoryBuilder instance);

    }

}
